package com.zh.android.swipemenulayoutsample.item;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zh.android.swipemenulayoutsample.R;
import com.zh.android.swipemenulayoutsample.SwipeMenuLayout;

import me.drakeet.multitype.ItemViewBinder;

/**
 * <b>Package:</b> com.zh.android.swipemenulayoutsample.item <br>
 * <b>Create Date:</b> 2020/2/26  5:16 PM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 条目类 <br>
 */
public class ListItemViewBinder extends ItemViewBinder<String, ListItemViewBinder.ViewHolder> {
    /**
     * 回调
     */
    private Callback mCallback;

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

    public ListItemViewBinder(Callback callback) {
        mCallback = callback;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.list_item, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull String item) {
        final int position = getPosition(holder);
        holder.vContent.setText(item);
        //未读
        holder.vUnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onClickUnread(position);
                }
            }
        });
        //删除
        holder.vDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onClickDelete(position);
                }
            }
        });
        //滑动菜单开、关监听
        holder.vSwipeMenuLayout.setOnSwipeMenuChangeListener(new SwipeMenuLayout.OnSwipeMenuChangeListener() {
            @Override
            public void onOpenMenu() {
                if (mCallback != null) {
                    mCallback.onOpenMenu(position);
                }
            }

            @Override
            public void onCloseMenu() {
                if (mCallback != null) {
                    mCallback.onCloseMenu(position);
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