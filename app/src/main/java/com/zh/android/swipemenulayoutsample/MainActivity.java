package com.zh.android.swipemenulayoutsample;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.zh.android.swipemenulayoutsample.lazymode.LazyModeEntryHelper;
import com.zh.android.swipemenulayoutsample.lazymode.LazyModeEntryView;

/**
 * @author wally
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SwipeMenuLayout swipeMenuLayout = findViewById(R.id.swipe_menu_layout);
        swipeMenuLayout.setSwipeEnable(true);
        swipeMenuLayout.addOnMenuStateChangeListener(new SwipeMenuLayout.OnMenuStateChangeListener() {
            @Override
            public void onOpenMenu() {
                swipeMenuLayout.smoothClose();
                startActivity(new Intent(MainActivity.this, LazyModeActivity.class));
            }

            @Override
            public void onCloseMenu() {
            }
        });
        LazyModeEntryView lazyModeEntryView = findViewById(R.id.lazy_mode_entry_view);
        lazyModeEntryView.bindData(
                LazyModeEntryHelper.getEntryDataModelByType(LazyModeEntryHelper.Type.ENTER_2_LAZY)
        );
    }
}