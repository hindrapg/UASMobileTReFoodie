package id.ac.umn.uas_aplikasi;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import id.ac.umn.uas_aplikasi.R;
import id.ac.umn.uas_aplikasi.adapter.RecyclerViewHomeAdapter;
import id.ac.umn.uas_aplikasi.adapter.RecyclerViewMealByCategory;
import id.ac.umn.uas_aplikasi.adapter.RecyclerViewMealBySearch;
import id.ac.umn.uas_aplikasi.adapter.ViewPagerHeaderAdapter;
import id.ac.umn.uas_aplikasi.category.CategoryActivity;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.ac.umn.uas_aplikasi.detail.DetailActivity;
import id.ac.umn.uas_aplikasi.login.LogoutResetActivity;
import id.ac.umn.uas_aplikasi.overview.homeOverview;

public class MainActivity extends AppCompatActivity implements HomeView {

    public static final String EXTRA_CATEGORY = "category";
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_DETAIL = "detail";


    @BindView(R.id.viewPagerHeader)
    ViewPager viewPagerMeal;
    @BindView(R.id.recyclerCategory)
    RecyclerView recyclerViewCategory;

    @BindView(R.id.Dicari)
    EditText Dicari;
    @BindView(R.id.titleCategory)
    TextView TitleCat;

    HomePresenter presenter;

    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.navigation_food_app); // change to whichever id should be default
        }
        presenter = new HomePresenter(this);
        presenter.getMeals();
        presenter.getCategories();
//        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        setupBottomNavigation();

        Dicari.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if(Dicari.getText().toString().length() != 0){
                        presenter.getSearch(Dicari.getText().toString());
                        TitleCat.setText("Search Result");
                    }else{
                        TitleCat.setText("Meal Categories");
                        presenter.getCategories();
                    }
                    return true;
            }
                return false;
        }});
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Log.i("CekIntent", "OK");
                        Intent homeIntent = new Intent(MainActivity.this, homeOverview.class);
                        startActivity(homeIntent);
                        finish();
                        return true;
                    case R.id.navigation_food_app:
                        Intent foodIntent = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(foodIntent);
                        finish();
                        return true;
                    case R.id.navigation_settings:
                        Intent settingsIntent = new Intent(MainActivity.this, LogoutResetActivity.class);
                        startActivity(settingsIntent);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }
    @Override
    public void showLoading() {
        findViewById(R.id.shimmerMeal).setVisibility(View.VISIBLE);
        findViewById(R.id.shimmerCategory).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        findViewById(R.id.shimmerMeal).setVisibility(View.GONE);
        findViewById(R.id.shimmerCategory).setVisibility(View.GONE);
    }

    @Override
    public void setMeal(List<Meals.Meal> meal) {
        ViewPagerHeaderAdapter headerAdapter = new ViewPagerHeaderAdapter(meal, this);
        viewPagerMeal.setAdapter(headerAdapter);
        viewPagerMeal.setPadding(20, 0, 150, 0);
        headerAdapter.notifyDataSetChanged();

        headerAdapter.setOnItemClickListener((view, position) -> {
            TextView mealName = view.findViewById(R.id.mealName);
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(EXTRA_DETAIL, mealName.getText().toString());
            startActivity(intent);
            //TODO #8.1 make an intent to DetailActivity (get the name of the meal from the edit text view, then send the name of the meal to DetailActivity)
        });
    }

    @Override
    public void setSearch(List<Meals.Meal> meals) {
        if(meals.isEmpty()){
            Log.i("Testtt", "error ksoong");
        }
//        if(flag == 0){
//            meals.clear();
//        }
        RecyclerViewMealBySearch adapter =
                new RecyclerViewMealBySearch(this, meals);
//        Log.i("Meal Searched", String.valueOf(Gsoncon));

        recyclerViewCategory.setAdapter(adapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2,
                GridLayoutManager.VERTICAL, false);
        recyclerViewCategory.setLayoutManager(layoutManager);
        recyclerViewCategory.setNestedScrollingEnabled(true);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener((view, position) -> {
            TextView mealName = view.findViewById(R.id.mealName);
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(EXTRA_DETAIL, mealName.getText().toString());
            startActivity(intent);
            //TODO #8.2 make an intent to DetailActivity (get the name of the meal from the edit text view, then send the name of the meal to DetailActivity)
        });
    }

    @Override
    public void setCategory(List<Categories.Category> category) {
//        if(flag != 0){
//            category.clear();
//        }
//        Log.i("Category Total", category.toString());
        RecyclerViewHomeAdapter homeAdapter = new RecyclerViewHomeAdapter(category, this);
        recyclerViewCategory.setAdapter(homeAdapter);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        recyclerViewCategory.setLayoutManager(layoutManager);
        recyclerViewCategory.setNestedScrollingEnabled(true);
        homeAdapter.notifyDataSetChanged();

        homeAdapter.setOnItemClickListener((view, position) -> {
            Intent intent = new Intent(this, CategoryActivity.class);
            intent.putExtra(EXTRA_CATEGORY, (Serializable) category);
            intent.putExtra(EXTRA_POSITION, position);
            startActivity(intent);
        });
    }

    @Override
    public void onErrorLoading(String message) {
        Utils.showDialogMessage(this, "Title", message);
    }
}
