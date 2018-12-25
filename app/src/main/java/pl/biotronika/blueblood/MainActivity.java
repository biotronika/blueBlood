package pl.biotronika.blueblood;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import pl.biotronika.blueblood.fragment.BluetoothFragment_;
import pl.biotronika.blueblood.fragment.CardContentFragment;
import pl.biotronika.blueblood.fragment.ListContentFragment;
import pl.biotronika.blueblood.fragment.RelodableFragment;
import pl.biotronika.blueblood.json.TerapieApi;
import pl.biotronika.blueblood.locale.LocaleHelper;
import pl.biotronika.blueblood.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private DrawerLayout mDrawerLayout;

    RelodableFragment contentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocaleHelper.setLocale(MainActivity.this, "pl");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BiotronikaApplication.getContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Setting ViewPager for each Tabs
        setupViewPager();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            VectorDrawableCompat indicator
                    = VectorDrawableCompat.create(getResources(), R.drawable.ic_menu, getTheme());
            indicator.setTint(ResourcesCompat.getColor(getResources(), R.color.colorIcon, getTheme()));
            actionBar.setHomeAsUpIndicator(indicator);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);

                        int id = menuItem.getItemId();

                        if (id == R.id.menu_settings) {
                            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                            startActivity(intent);
                        } else if (id == R.id.menu_close) {
                            finish();
                            System.exit(0);
                        }

                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }




    private void setupViewPager() {

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BiotronikaApplication.getContext());
        String adapterType =   sharedPreferences.getString("key_list_type","1");

        Log.i("MAIN", "onSharedPreferenceChangedXX " + adapterType);

        if (adapterType.equals("1")) {
            Log.i("MAIN", "createList " + adapterType);
            contentFragment = null;
            contentFragment = new ListContentFragment();
        } else {
            Log.i("MAIN", "createCard " + adapterType);
            contentFragment = null;
            contentFragment = new CardContentFragment();
        }

        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new BluetoothFragment_(), getString(R.string.tab_device));
        adapter.addFragment(contentFragment, getString(R.string.tab_therapies));
        viewPager.setAdapter(adapter);

        int selectedtab = sharedPreferences.getInt("selectedtab", 0);
        viewPager.setCurrentItem(selectedtab);

        // Set Tabs inside Toolbar
        TabLayout tabs;

        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putInt("selectedtab", position).apply();
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }

    class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                TerapieApi.setFilter(query);
                TerapieApi.resetList();
                contentFragment.reload();
                contentFragment.setUserVisibleHint(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                TerapieApi.setFilter(query);
                TerapieApi.resetList();
                contentFragment.reload();
                contentFragment.setUserVisibleHint(true);
                return true;
            }
        });


        MenuItem menuItem = menu.findItem(R.id.search);

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener()
            {

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item)
                {
                    // Do something when collapsed
                    Log.i("MAIN", "onMenuItemActionCollapse " + item.getItemId());
                    clearFilter();
                    return true; // Return true to collapse action view
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item)
                {
                    // TODO Auto-generated method stub
                    Log.i("MAIN", "onMenuItemActionExpand " + item.getItemId());
                    return true;
                }
            });
        } else
        {
            // do something for phones running an SDK before froyo
            searchView.setOnCloseListener(new SearchView.OnCloseListener()
            {

                @Override
                public boolean onClose()
                {
                    Log.i("MAIN", "mSearchView on close ");
                    clearFilter();
                    return false;
                }
            });
        }

        return true;
    }


    public void clearFilter() {
        TerapieApi.clearFilter();
        TerapieApi.resetList();
        contentFragment.reload();
        contentFragment.setUserVisibleHint(true);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (id == R.id.sort_name_asc) {
            TerapieApi.sortByNameAsc();
        } else if (id == R.id.sort_name_desc) {
            TerapieApi.sortByNameDesc();
        } else if (id == R.id.sort_id_asc) {
            TerapieApi.sortByIdAsc();
        } else if (id == R.id.sort_id_desc) {
            TerapieApi.sortByIdDesc();
        }
//        else if (id == R.id.clear_filter) {
//            clearFilter();
//            return super.onOptionsItemSelected(item);
//        }

        contentFragment.reload();
        contentFragment.setUserVisibleHint(true);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("key_list_type")) {
            TerapieApi.resetList();
            setupViewPager();
        }
    }

}
