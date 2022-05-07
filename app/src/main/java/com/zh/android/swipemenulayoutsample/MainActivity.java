package com.zh.android.swipemenulayoutsample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zh.android.swipemenulayoutsample.item.ListItemViewBinder;
import com.zh.android.swipemenulayoutsample.model.ListItemModel;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wally
 */
@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Items mListItems;
    private MultiTypeAdapter mListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mListItems = new Items();
        for (int i = 0; i < 100; i++) {
            mListItems.add(new ListItemModel("我是条目" + i));
        }
        mListAdapter = new MultiTypeAdapter(mListItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mListAdapter.register(ListItemModel.class, new ListItemViewBinder(new ListItemViewBinder.Callback() {
            @Override
            public void onClickContent(ListItemModel item) {
                int position = mListItems.indexOf(item);
                if (position == -1) {
                    return;
                }
                toast(position + "：点击内容");
                //关闭菜单
                SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                if (viewCache != null) {
                    viewCache.smoothClose();
                }
            }

            @Override
            public void onClickUnread(ListItemModel item) {
                int position = mListItems.indexOf(item);
                if (position == -1) {
                    return;
                }
                toast(position + "：设置未读");
                //关闭菜单
                SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
                if (viewCache != null) {
                    viewCache.smoothClose();
                }
            }

            @Override
            public void onClickDelete(ListItemModel item) {
                int position = mListItems.indexOf(item);
                if (position == -1) {
                    return;
                }
                mListItems.remove(position);
                mListAdapter.notifyItemRemoved(position);
                //通知其他条目的position改变
                if (position < mListAdapter.getItemCount()) {
                    mListAdapter.notifyItemRangeChanged(position, mListAdapter.getItemCount() - position);
                }
            }

            @Override
            public void onOpenMenu(ListItemModel item) {
                int position = mListItems.indexOf(item);
                if (position == -1) {
                    return;
                }
                Log.d(TAG, position + "：菜单打开");
            }

            @Override
            public void onCloseMenu(ListItemModel item) {
                int position = mListItems.indexOf(item);
                if (position == -1) {
                    return;
                }
                Log.d(TAG, position + "：菜单关闭");
            }
        }));
        recyclerView.setAdapter(mListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    SwipeMenuLayout swipeMenuLayout = SwipeMenuLayout.getViewCache();
                    if (swipeMenuLayout != null) {
                        swipeMenuLayout.smoothClose();
                    }
                }
                return false;
            }
        });
    }

    private void toast(String msg) {
        Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.option_close_menu) {
            //关闭当前已打开的菜单
            SwipeMenuLayout viewCache = SwipeMenuLayout.getViewCache();
            if (viewCache != null) {
                viewCache.smoothClose();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}