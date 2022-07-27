package com.zh.android.swipemenulayoutsample.lazymode;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zh.android.swipemenulayoutsample.R;
import com.zh.android.swipemenulayoutsample.widget.CoVerticalTextView;


/**
 * 懒人模式入口View
 */
public class LazyModeEntryView extends FrameLayout {
    private ImageView vBg;
    private ImageView vIcon;
    private CoVerticalTextView vEntryText;

    private DataModel mDataModel;

    public LazyModeEntryView(@NonNull Context context) {
        this(context, null);
    }

    public LazyModeEntryView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LazyModeEntryView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(context).inflate(R.layout.layout_lazy_mode_entry_view, this, true);
        findView(this);
    }

    private void findView(View view) {
        vBg = view.findViewById(R.id.bg);
        vIcon = view.findViewById(R.id.icon);
        vEntryText = view.findViewById(R.id.entry_text);
    }

    private void render() {
        if (mDataModel != null) {
            vBg.setImageResource(mDataModel.bgResId);
            vIcon.setImageResource(mDataModel.iconResId);
            vEntryText.setText(mDataModel.entryText);
        }
    }

    public static class DataModel {
        /**
         * 背景图片的资源Id
         */
        int bgResId;
        /**
         * 图标图片的资源Id
         */
        int iconResId;
        /**
         * 入口文字
         */
        String entryText;

        public DataModel(int bgResId, int iconResId, String entryText) {
            this.bgResId = bgResId;
            this.iconResId = iconResId;
            this.entryText = entryText;
        }
    }

    /**
     * 绑定数据
     */
    public void bindData(DataModel dataModel) {
        this.mDataModel = dataModel;
        render();
    }
}