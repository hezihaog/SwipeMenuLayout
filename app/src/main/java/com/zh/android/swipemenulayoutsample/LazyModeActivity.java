package com.zh.android.swipemenulayoutsample;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zh.android.swipemenulayoutsample.lazymode.LazyModeEntryHelper;
import com.zh.android.swipemenulayoutsample.lazymode.LazyModeEntryView;

/**
 * 懒人模式
 */
public class LazyModeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lazy_mode);
        final SwipeMenuLayout swipeMenuLayout = findViewById(R.id.swipe_menu_layout);
        swipeMenuLayout.addOnMenuStateChangeListener(new SwipeMenuLayout.OnMenuStateChangeListener() {
            @Override
            public void onOpenMenu() {
                swipeMenuLayout.smoothClose();
                finish();
            }

            @Override
            public void onCloseMenu() {
            }
        });
        LazyModeEntryView lazyModeEntryView = findViewById(R.id.lazy_mode_entry_view);
        lazyModeEntryView.bindData(
                LazyModeEntryHelper.getEntryDataModelByType(LazyModeEntryHelper.Type.ENTER_2_CUSTOM)
        );
    }
}
