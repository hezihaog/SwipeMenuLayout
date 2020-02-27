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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

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
     * 打开菜单的操作
     */
    private List<Action> mMenuOpenActions = new CopyOnWriteArrayList<>();
    /**
     * 关闭菜单的操作
     */
    private List<Action> mMenuCloseActions = new CopyOnWriteArrayList<>();
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
                //处理开、关菜单回调
                int contentViewLeft = vContentView.getLeft();
                int menuViewWidth = vMenuView.getWidth();
                //由于这个方法移动一下，就回调一次，所以会重复，必须加上标志位
                if ((contentViewLeft == -menuViewWidth) && !isOpenMenu()) {
                    //菜单开
                    for (Action action : mMenuOpenActions) {
                        action.onActionFinish();
                    }
                } else if (contentViewLeft == 0 && isOpenMenu()) {
                    //菜单关
                    for (Action action : mMenuCloseActions) {
                        action.onActionFinish();
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
                    openMenu(true);
                } else {
                    //其他情况，关闭菜单
                    closeMenu(true);
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
        if (!Manager.get().isCanSwipe(this)) {
            return true;
        }
        //将onInterceptTouchEvent委托给ViewDragHelper
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Manager manager = Manager.get();
        //当触摸时，如果不是当前自己并没有打开菜单，则关闭上一个菜单先（只能一个打开）
        if (!manager.isOpenInstance(SwipeMenuLayout.this)) {
            manager.closeOpenInstance(true);
        }
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
    public void openMenu(boolean hasAnimation) {
        //添加一个操作，当滚动动画结束时回调，再进行统一的处理
        mMenuOpenActions.add(new Action() {
            @Override
            public void onActionFinish() {
                mMenuOpenActions.remove(this);
                onMenuOpenFinish();
            }
        });
        setMenuOpen(hasAnimation);
    }

    /**
     * 设置菜单开，不通知回调
     *
     * @param hasAnimation 是否有动画
     */
    public void setMenuOpen(boolean hasAnimation) {
        //让内容区域移动，向左一个菜单的距离
        int finalLeft = -vMenuView.getWidth();
        int menuWidth = vMenuView.getWidth();
        if (hasAnimation) {
            mViewDragHelper.smoothSlideViewTo(vContentView, finalLeft, vContentView.getTop());
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            //没有动画，直接让layout移动
            vContentView.layout(finalLeft, getTop(), getRight(), getBottom());
            vMenuView.layout(getRight() - menuWidth, getTop(), getRight(), getBottom());
            //没有动画，马上结束
            for (Action action : mMenuOpenActions) {
                action.onActionFinish();
            }
        }
    }

    /**
     * 向右移动，关闭菜单
     *
     * @param hasAnimation 是否有动画
     */
    public void closeMenu(boolean hasAnimation) {
        //添加一个操作，当滚动动画结束时回调，再进行统一的处理
        mMenuCloseActions.add(new Action() {
            @Override
            public void onActionFinish() {
                mMenuCloseActions.remove(this);
                onMenuCloseFinish();
            }
        });
        setMenuClose(hasAnimation);
    }

    /**
     * 设置菜单关，不通知回调
     *
     * @param hasAnimation 是否有动画
     */
    public void setMenuClose(boolean hasAnimation) {
        if (hasAnimation) {
            //直接将内容区域移动到最左侧即可
            mViewDragHelper.smoothSlideViewTo(vContentView, 0, vContentView.getTop());
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            //没有动画，直接让layout移动
            vContentView.layout(getLeft(), getTop(), getRight(), getBottom());
            vMenuView.layout(getRight(), getTop(), getRight() + vMenuView.getWidth(), getBottom());
            //没有动画，马上结束
            for (Action action : mMenuCloseActions) {
                action.onActionFinish();
            }
        }
    }

    /**
     * 当菜单打开完成时调用
     */
    private void onMenuOpenFinish() {
        Manager.get().holdOpenInstance(SwipeMenuLayout.this);
        if (mMenuStateChangeListener != null) {
            mMenuStateChangeListener.onOpenMenu();
        }
    }

    /**
     * 当菜单关闭完成时调用
     */
    private void onMenuCloseFinish() {
        Manager.get().removeOpenInstance();
        if (mMenuStateChangeListener != null) {
            mMenuStateChangeListener.onCloseMenu();
        }
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
     * 一个操作
     */
    public interface Action {
        /**
         * 操作结束时回调
         */
        void onActionFinish();
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
        return Manager.get().isOpenInstance(this);
    }

    /**
     * 管理器
     */
    public static class Manager {
        /**
         * 打开的布局
         */
        private SwipeMenuLayout mOpenInstance;

        private Manager() {
        }

        private static final class SingleHolder {
            private static final Manager INSTANCE = new Manager();
        }

        public static Manager get() {
            return SingleHolder.INSTANCE;
        }

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
         *
         * @param hasAnimation 是否要有动画
         */
        public void closeOpenInstance(boolean hasAnimation) {
            if (mOpenInstance != null) {
                mOpenInstance.closeMenu(hasAnimation);
                mOpenInstance = null;
            }
        }

        /**
         * 判断布局是否打开中
         */
        public boolean isOpenInstance(SwipeMenuLayout layout) {
            return mOpenInstance == layout;
        }
    }
}