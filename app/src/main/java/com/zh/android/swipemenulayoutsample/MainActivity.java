package com.zh.android.swipemenulayoutsample;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zh.android.swipemenulayoutsample.item.ListItemViewBinder;
import com.zh.android.swipemenulayoutsample.model.ListItemModel;

import java.util.HashMap;
import java.util.Map;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wally
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private Items mListItems;
    private MultiTypeAdapter mListAdapter;
    /**
     * 位置对应菜单打开状态的映射
     */
    private Map<Integer, Boolean> mMenuOpenStateMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        mListItems = new Items();
        for (int i = 0; i < 20; i++) {
            mListItems.add(new ListItemModel("我是条目" + i));
        }
        mListAdapter = new MultiTypeAdapter(mListItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mListAdapter.register(ListItemModel.class, new ListItemViewBinder(new ListItemViewBinder.Callback() {
            @Override
            public void onClickUnread(int position) {
                toast(position + "：设置未读");
            }

            @Override
            public void onClickDelete(int position) {
                mMenuOpenStateMap.remove(position);
                mListItems.remove(position);
                mListAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onOpenMenu(int position) {
                Log.d(TAG, position + "：菜单打开");
                mMenuOpenStateMap.put(position, true);
            }

            @Override
            public void onCloseMenu(int position) {
                Log.d(TAG, position + "：菜单关闭");
                mMenuOpenStateMap.put(position, false);
            }
        }, new ListItemViewBinder.OnRenderMenuStateCallback() {
            @Override
            public boolean isMenuOpen(int position) {
                Boolean state = mMenuOpenStateMap.get(position);
                return state != null && state;
            }
        }));
        recyclerView.setAdapter(mListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
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
            SwipeMenuLayout.Manager.get().closeOpenInstance(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}