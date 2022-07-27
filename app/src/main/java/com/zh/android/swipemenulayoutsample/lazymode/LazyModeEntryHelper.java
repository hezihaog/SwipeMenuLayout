package com.zh.android.swipemenulayoutsample.lazymode;


import com.zh.android.swipemenulayoutsample.R;

/**
 * 懒人模式入口帮助类，用于统一配置入口的资源
 */
public class LazyModeEntryHelper {
    public enum Type {
        /**
         * 进入懒人模式
         */
        ENTER_2_LAZY,
        /**
         * 进入自选模式
         */
        ENTER_2_CUSTOM
    }

    private LazyModeEntryHelper() {
    }

    /**
     * 按类型，绑定入口View的资源信息
     */
    public static LazyModeEntryView.DataModel getEntryDataModelByType(Type type) {
        LazyModeEntryView.DataModel dataModel;
        if (Type.ENTER_2_LAZY == type) {
            dataModel = new LazyModeEntryView.DataModel(
                    R.mipmap.bg_lazy_mode_entry_left,
                    R.mipmap.icon_lazy_mode_entry_icon1,
                    "进入懒人模式"
            );
        } else if (Type.ENTER_2_CUSTOM == type) {
            dataModel = new LazyModeEntryView.DataModel(
                    R.mipmap.bg_lazy_mode_entry_right,
                    R.mipmap.icon_lazy_mode_entry_icon2,
                    "进入自选模式"
            );
        } else {
            dataModel = null;
        }
        return dataModel;
    }
}