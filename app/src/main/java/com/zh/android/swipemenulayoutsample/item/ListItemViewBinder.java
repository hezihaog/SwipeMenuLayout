package com.zh.android.swipemenulayoutsample.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zh.android.swipemenulayoutsample.R;
import com.zh.android.swipemenulayoutsample.SwipeMenuLayout;
import com.zh.android.swipemenulayoutsample.model.ListItemModel;

import me.drakeet.multitype.ItemViewBinder;

/**
 * <b>Package:</b> com.zh.android.swipemenulayoutsample.item <br>
 * <b>Create Date:</b> 2020/2/26  5:16 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 条目类 <br>
 */
public class ListItemViewBinder extends ItemViewBinder<ListItemModel, ListItemViewBinder.ViewHolder> {
    /**
     * 回调
     */
    private Callback mCallback;
    /**
     * 渲染菜单状态回调
     */
    private OnRenderMenuStateCallback mRenderMenuStateCallback;

    public interface Callback {
        /**
         * 点击了标记为未读
         */
        void onClickUnread(int position);

        /**
         * 点击了删除
         */
        void onClickDelete(int position);

        /**
         * 菜单开启
         */
        void onOpenMenu(int position);

        /**
         * 菜单关闭
         */
        void onCloseMenu(int position);
    }

    public interface OnRenderMenuStateCallback {
        /**
         * 菜单是否是打开的
         *
         * @param position 条目位置
         */
        boolean isMenuOpen(int position);
    }

    public ListItemViewBinder(Callback callback, OnRenderMenuStateCallback renderMenuStateCallback) {
        mCallback = callback;
        mRenderMenuStateCallback = renderMenuStateCallback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, @NonNull ListItemModel item) {
        holder.vContent.setText(item.getContent());
        //恢复开、关状态
        if (mRenderMenuStateCallback.isMenuOpen(getPosition(holder))) {
            holder.vSwipeMenuLayout.setMenuOpen(false);
        } else {
            holder.vSwipeMenuLayout.setMenuClose(false);
        }
        //滑动菜单开、关监听
        holder.vSwipeMenuLayout.addOnMenuStateChangeListener(new SwipeMenuLayout.OnMenuStateChangeListener() {
            @Override
            public void onOpenMenu() {
                if (mCallback != null) {
                    final int position = getPosition(holder);
                    mCallback.onOpenMenu(position);
                }
            }

            @Override
            public void onCloseMenu() {
                if (mCallback != null) {
                    final int position = getPosition(holder);
                    mCallback.onCloseMenu(position);
                }
            }
        });
        //未读
        holder.vUnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    final int position = getPosition(holder);
                    mCallback.onClickUnread(position);
                }
            }
        });
        //删除
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    final int position = getPosition(holder);
                    mCallback.onClickDelete(position);
                }
            }
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final SwipeMenuLayout vSwipeMenuLayout;
        private final TextView vContent;
        private final TextView vUnread;
        private final View vDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            vSwipeMenuLayout = itemView.findViewById(R.id.swipe_menu_layout);
            vContent = itemView.findViewById(R.id.content);
            vUnread = itemView.findViewById(R.id.unread);
            vDelete = itemView.findViewById(R.id.delete);
        }
    }
}