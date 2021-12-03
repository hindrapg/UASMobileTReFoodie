package id.ac.umn.uas_aplikasi.category;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.ac.umn.uas_aplikasi.R;
import id.ac.umn.uas_aplikasi.adapter.ViewPagerCategoryAdapter;
import id.ac.umn.uas_aplikasi.Categories;
import id.ac.umn.uas_aplikasi.MainActivity;
import id.ac.umn.uas_aplikasi.detail.DetailActivity;
import id.ac.umn.uas_aplikasi.login.LogoutResetActivity;
import id.ac.umn.uas_aplikasi.overview.homeOverview;

public class CategoryActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);

        initActionBar();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.navigation_food_app); // change to whichever id should be default
        }
        setupBottomNavigation();
        initIntent();
        
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Log.i("CekIntent", "OK");
                        Intent homeIntent = new Intent(CategoryActivity.this, homeOverview.class);
                        startActivity(homeIntent);
                        finish();
                        return true;
                    case R.id.navigation_food_app:
                        Intent foodIntent = new Intent(CategoryActivity.this, MainActivity.class);
                        startActivity(foodIntent);
                        return true;
                    case R.id.navigation_settings:
                        Intent settingsIntent = new Intent(CategoryActivity.this, LogoutResetActivity.class);
                        startActivity(settingsIntent);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void initIntent() {
        Intent intent = getIntent();
        List<Categories.Category> categories =
                (List<Categories.Category>) intent.getSerializableExtra(MainActivity.EXTRA_CATEGORY);
        int position = intent.getIntExtra(MainActivity.EXTRA_POSITION, 0);
        
        ViewPagerCategoryAdapter adapter = new ViewPagerCategoryAdapter(
                getSupportFragmentManager(),
                categories);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(position, true);
        adapter.notifyDataSetChanged();
        
    }

    private void initActionBar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}
