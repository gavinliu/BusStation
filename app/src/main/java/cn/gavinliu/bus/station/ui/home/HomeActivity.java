package cn.gavinliu.bus.station.ui.home;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import cn.gavinliu.bus.station.ui.BaseActivity;
import cn.gavinliu.zhuhai.station.R;

public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    
    private FragmentManager mFragmentManager;

    private HistoryFragment mHistoryFragment;
    private SearchFragment mSearchFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            mSearchFragment = SearchFragment.newInstance();
            mHistoryFragment = HistoryFragment.newInstance();

            mFragmentManager.beginTransaction()
                    .add(R.id.container, mHistoryFragment)
                    .commitAllowingStateLoss();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.action_home, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("请输入站名");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)) {
                    mSearchFragment.setKeyword(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mFragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .remove(mHistoryFragment)
                        .replace(R.id.container, mSearchFragment)
                        .commitAllowingStateLoss();

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mFragmentManager.popBackStack();
                return true;
            }
        });

        return true;
    }

}
