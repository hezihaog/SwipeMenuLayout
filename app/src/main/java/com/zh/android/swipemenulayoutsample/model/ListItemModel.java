package com.zh.android.swipemenulayoutsample.model;

import java.io.Serializable;

/**
 * <b>Package:</b> com.zh.android.swipemenulayoutsample.model <br>
 * <b>Create Date:</b> 2020/2/27  10:53 AM <br>
 * <b>@author:</b> zihe <br>
 * <b>Description:</b> 列表条目模型 <br>
 */
public class ListItemModel implements Serializable {
    /**
     * 内容
     */
    private String content;
    /**
     * 菜单是否打开
     */
    private boolean isMenuOpen;

    public ListItemModel(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public ListItemModel setContent(String content) {
        this.content = content;
        return this;
    }

    public boolean isMenuOpen() {
        return isMenuOpen;
    }

    public ListItemModel setMenuOpen(boolean menuOpen) {
        isMenuOpen = menuOpen;
        return this;
    }
}