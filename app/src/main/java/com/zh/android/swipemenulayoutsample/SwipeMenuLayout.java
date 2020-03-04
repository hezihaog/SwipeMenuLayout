package com.zh.android.swipemenulayoutsample;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

/**
 * <b>Package:</b> com.zh.android.swipemenulayoutsample <br>
 * <b>Create Date:</b> 2020/2/26  3:03 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 侧滑菜单布局，只有内容区域和菜单区域2个子View <br>
 */
public class SwipeMenuLayout extends FrameLayout {
    /**
     * 内容区域View
     */
    private View vContentView;
    /**
     * 菜单区域View
     */
    private View vMenuView;
    /**
     * 拽托帮助类
     */
    private ViewDragHelper mViewDragHelper;
    /**
     * 菜单状态改变监听
     */
    private OnMenuStateChangeListener mMenuStateChangeListener;
    /**
     * 触摸按下时的X坐标
     */
    private float mDownX;
    /**
     * 触摸按下时的Y坐标
     */
    private float mDownY;
    /**
     * 菜单管理器
     */
    private MenuManager mMenuManager;

    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        mViewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                //内容区域和菜单区域布局才能拽托
                return child == vContentView || child == vMenuView;
            }

            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                //水平方向的拖拽范围
                return vMenuView.getWidth();
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                //内容区域大小
                int contentViewWidth = vContentView.getWidth();
                //菜单区域的宽度
                int menuViewWidth = vMenuView.getWidth();
                //滑动的是内容区域
                if (child == vContentView) {
                    //不能向左滑动出界限
                    if (left > 0) {
                        return 0;
                    } else if (left < -menuViewWidth) {
                        //向右滑动，不能超过可滑动的距离
                        return -menuViewWidth;
                    } else {
                        //在上面指定范围内，可滑动
                        return left;
                    }
                } else if (child == vMenuView) {
                    //滑动的时菜单区域
                    if (left > contentViewWidth) {
                        //菜单不能滑动出内容区域，不能继续向右滑动
                        return contentViewWidth;
                    } else if (left < (contentViewWidth - menuViewWidth)) {
                        //菜单完全显示出来了，不能继续向左滑动
                        return contentViewWidth - menuViewWidth;
                    } else {
                        return left;
                    }
                }
                return 0;
            }

            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                //拽托微信更新时回调，我们在这里更新内容区域和菜单区域的位置，才能让这2部分一起联动
                if (changedView == vContentView) {
                    //拽托内容布局，让菜单布局跟着移动
                    int newLeft = vMenuView.getLeft() + dx;
                    int right = newLeft + vMenuView.getWidth();
                    vMenuView.layout(newLeft, top, right, getBottom());
                } else if (changedView == vMenuView) {
                    //拽托菜单布局，让内容布局跟着移动
                    int newLeft = left - vContentView.getWidth();
                    vContentView.layout(newLeft, vContentView.getTop(),
                            left, vContentView.getBottom());
                }
            }

            @Override
            public void onViewDragStateChanged(int state) {
                super.onViewDragStateChanged(state);
                //处理开、关菜单回调
                if (state == ViewDragHelper.STATE_IDLE) {
                    int menuViewLeft = vMenuView.getLeft();
                    int menuViewWidth = vMenuView.getWidth();
                    //打开时，菜单的左边位置
                    int openMenuLeft = getRight() - menuViewWidth;
                    //关闭时，菜单的左边位置
                    int closeMenuLeft = getRight();
                    //是否有一个菜单在打开
                    boolean isOpenMenu = requireMenuManager().isOpenMenu();
                    //由于这个方法移动一下，就回调一次，所以会重复，必须加上标志位
                    if ((menuViewLeft == openMenuLeft)) {
                        //菜单开
                        onMenuOpenFinish(true);
                    } else if (menuViewLeft == closeMenuLeft) {
                        //菜单关
                        onMenuCloseFinish(true);
                    }
                }
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                //拽托子View纵向滑动时回调，锁定顶部padding距离即可，不能不复写，否则少了顶部的padding，位置就偏去上面了
                return getPaddingTop();
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                super.onViewReleased(releasedChild, xvel, yvel);
                int contentViewWidth = vContentView.getWidth();
                //松手回弹，如果右边剩余的距离大于Menu的一半，则滚动到最后边，否则滚动回最左边
                float halfMenuWidth = vMenuView.getWidth() / 2f;
                //打开时的宽度，内容区域宽度减去一个菜单区域的宽度
                float fullOpenWidth = contentViewWidth - halfMenuWidth;
                //判断方向，向左滑动到打开，打开菜单
                if (vContentView.getRight() < fullOpenWidth) {
                    openMenu();
                } else {
                    //其他情况，关闭菜单
                    closeMenu();
                }
            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //布局内容区域，和父View一样大小
        vContentView.layout(left, top, right, bottom);
        //布局菜单区域，在内容区域的右边
        vMenuView.layout(right, top, right + vMenuView.getMeasuredWidth(), bottom);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int childCount = getChildCount();
        if (childCount > 2 || childCount <= 0) {
            throw new RuntimeException("子View必须只有2个，内容布局和菜单布局");
        }
        vContentView = getChildAt(0);
        vMenuView = getChildAt(1);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //判断是否可以滑动，可以滑动则拦截，自己消费事件
        if (!requireMenuManager().isCanSwipe(this)) {
            if (MotionEvent.ACTION_DOWN == ev.getAction()) {
                //当触摸时，如果不是当前自己并没有打开菜单，则关闭上一个菜单先（只能一个打开）
                MenuManager manager = requireMenuManager();
                if (!manager.isOpenInstance(SwipeMenuLayout.this)) {
                    manager.closeOpenInstance();
                }
            }
            return true;
        }
        //将onInterceptTouchEvent委托给ViewDragHelper
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();
                //横向滑动距离
                float distanceX = Math.abs(moveX - mDownX);
                //纵向滑动距离
                float distanceY = Math.abs(moveY - mDownY);
                //横向滑动得多，让外层不拦截事件
                if (distanceX > distanceY) {
                    requestDisallowInterceptTouchEvent(true);
                }
                break;
            default:
                break;
        }
        //将onTouchEvent委托给ViewDragHelper
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //判断是否移动到头了，未到头则继续
        if (mViewDragHelper != null) {
            if (mViewDragHelper.continueSettling(true)) {
                invalidate();
            }
        }
    }

    /**
     * 向左移动，打开菜单
     */
    public void openMenu() {
        int finalLeft = -vMenuView.getWidth();
        mViewDragHelper.smoothSlideViewTo(vContentView, finalLeft, vContentView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 设置菜单开，不通知回调
     */
    public void setMenuOpen() {
        //让内容区域移动，向左一个菜单的距离
        int finalLeft = -vMenuView.getWidth();
        int menuWidth = vMenuView.getWidth();
        //直接让layout移动
        vContentView.layout(finalLeft, getTop(), getRight(), getBottom());
        vMenuView.layout(getRight() - menuWidth, getTop(), getRight(), getBottom());
        onMenuOpenFinish(false);
    }

    /**
     * 向右移动，关闭菜单
     */
    public void closeMenu() {
        mViewDragHelper.smoothSlideViewTo(vContentView, 0, vContentView.getTop());
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 设置菜单关，不通知回调
     */
    public void setMenuClose() {
        //没有动画，直接让layout移动
        vContentView.layout(getLeft(), getTop(), getRight(), getBottom());
        vMenuView.layout(getRight(), getTop(), getRight() + vMenuView.getWidth(), getBottom());
        onMenuCloseFinish(false);
    }

    /**
     * 当菜单打开完成时调用
     *
     * @param isNeedCallListener 是否需要通知监听器
     */
    private void onMenuOpenFinish(boolean isNeedCallListener) {
        requireMenuManager().holdOpenInstance(SwipeMenuLayout.this);
        if (isNeedCallListener && mMenuStateChangeListener != null) {
            mMenuStateChangeListener.onOpenMenu();
        }
    }

    /**
     * 当菜单关闭完成时调用
     *
     * @param isNeedCallListener 是否需要通知监听器
     */
    private void onMenuCloseFinish(boolean isNeedCallListener) {
        requireMenuManager().removeOpenInstance();
        if (isNeedCallListener && mMenuStateChangeListener != null) {
            mMenuStateChangeListener.onCloseMenu();
        }
    }

    /**
     * 依附菜单管理器，使用方必须设置
     *
     * @param manager 管理器
     */
    public void attachManager(MenuManager manager) {
        mMenuManager = manager;
    }

    /**
     * 检查菜单管理器
     */
    private MenuManager requireMenuManager() {
        if (mMenuManager == null) {
            throw new NullPointerException("必须要调用attachManager()方法，设置菜单管理器");
        }
        return mMenuManager;
    }

    public interface OnMenuStateChangeListener {
        /**
         * 当打开菜单时回调
         */
        void onOpenMenu();

        /**
         * 当关闭菜单时回调
         */
        void onCloseMenu();
    }

    /**
     * 添加菜单状态改变监听
     *
     * @param listener 监听器
     */
    public void addOnMenuStateChangeListener(OnMenuStateChangeListener listener) {
        mMenuStateChangeListener = listener;
    }

    /**
     * 是否打开了菜单
     */
    public boolean isOpenMenu() {
        return requireMenuManager().isOpenInstance(this);
    }

    /**
     * 管理器
     */
    public static class MenuManager {
        /**
         * 打开的布局
         */
        private SwipeMenuLayout mOpenInstance;

        /**
         * 是否可以滑动
         */
        public boolean isCanSwipe(SwipeMenuLayout layout) {
            //已经打开了，可以滑动
            if (isOpenInstance(layout)) {
                return true;
            }
            //一个都没打开，也可以滑动
            return mOpenInstance == null;
        }

        /**
         * 保存打开的实例
         */
        void holdOpenInstance(SwipeMenuLayout layout) {
            mOpenInstance = layout;
        }

        /**
         * 移除打开的实例
         */
        void removeOpenInstance() {
            mOpenInstance = null;
        }

        /**
         * 关闭当前打开中的条目
         */
        public void closeOpenInstance() {
            if (mOpenInstance != null) {
                mOpenInstance.closeMenu();
                mOpenInstance = null;
            }
        }

        /**
         * 判断布局是否打开中
         */
        public boolean isOpenInstance(SwipeMenuLayout layout) {
            return mOpenInstance == layout;
        }

        /**
         * 是否存在一个菜单在关闭
         */
        public boolean isOpenMenu() {
            return mOpenInstance != null;
        }
    }
}