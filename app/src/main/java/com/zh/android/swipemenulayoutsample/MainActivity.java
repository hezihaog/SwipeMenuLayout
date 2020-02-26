package com.zh.android.swipemenulayoutsample;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zh.android.swipemenulayoutsample.item.ListItemViewBinder;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;

/**
 * @author wally
 */
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
        for (int i = 0; i < 20; i++) {
            mListItems.add("我是条目" + i);
        }
        mListAdapter = new MultiTypeAdapter(mListItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mListAdapter.register(String.class, new ListItemViewBinder(new ListItemViewBinder.Callback() {
            @Override
            public void onClickUnread(int position) {
                toast(position + "：设置未读");
            }

            @Override
            public void onClickDelete(int position) {
                mListItems.remove(position);
                mListAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onOpenMenu(int position) {
                Log.d(TAG, position + "：菜单打开");
            }

            @Override
            public void onCloseMenu(int position) {
                Log.d(TAG, position + "：菜单关闭");
            }
        }));
        recyclerView.setAdapter(mListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, RecyclerView.VERTICAL));
    }

    private void toast(String msg) {
        Toast.makeText(this.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}