
package id.ac.umn.uas_aplikasi.detail;

import static id.ac.umn.uas_aplikasi.MainActivity.EXTRA_DETAIL;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.ac.umn.uas_aplikasi.Categories;
import id.ac.umn.uas_aplikasi.MainActivity;
import id.ac.umn.uas_aplikasi.Meals;
import id.ac.umn.uas_aplikasi.R;
import id.ac.umn.uas_aplikasi.Utils;
import id.ac.umn.uas_aplikasi.login.LogoutResetActivity;
import id.ac.umn.uas_aplikasi.overview.homeOverview;

public class DetailActivity extends AppCompatActivity implements DetailView{ //TODO #11  implement DetailView

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @BindView(R.id.mealThumb)
    ImageView mealThumb;

    @BindView(R.id.category)
    TextView category;

    @BindView(R.id.country)
    TextView country;

    @BindView(R.id.instructions)
    TextView instructions;

    @BindView(R.id.ingredient)
    TextView ingredients;

    @BindView(R.id.measure)
    TextView measures;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.add_to_database)
    TextView add_to_database;

    @BindView(R.id.source)
    TextView source;

    @BindView(R.id.calorieCountFood)
            TextView calorieFood;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private DatePickerDialog datePickerDialog;
    private Button btDatePicker;
    private String currentDatePicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        setupActionBar();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.navigation_food_app); // change to whichever id should be default
        }
        setupBottomNavigation();
        Intent intent = getIntent();
        String mealName = intent.getStringExtra(EXTRA_DETAIL);
        DetailPresenter presenter= new DetailPresenter(this);
        presenter.getMealById(mealName);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDatePicked = formattedDate;
        btDatePicker = (Button) findViewById(R.id.pick_dates);
        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        btDatePicker.setText(currentDatePicked);
        //TODO #9 Get data from the intent

        //TODO #10 Declare the presenter (put the name of the meal name from the data intent to the presenter)

    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setContentScrimColor(getResources().getColor(R.color.colorWhite));
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimary));
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(R.color.colorWhite));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    void setupColorActionBarIcon(Drawable favoriteItemColor) {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if ((collapsingToolbarLayout.getHeight() + verticalOffset) < (2 * ViewCompat.getMinimumHeight(collapsingToolbarLayout))) {
                if (toolbar.getNavigationIcon() != null)
                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
                favoriteItemColor.mutate().setColorFilter(getResources().getColor(R.color.colorPrimary),
                        PorterDuff.Mode.SRC_ATOP);

            } else {
                if (toolbar.getNavigationIcon() != null)
                    toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                favoriteItemColor.mutate().setColorFilter(getResources().getColor(R.color.colorWhite),
                        PorterDuff.Mode.SRC_ATOP);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
//        MenuItem favoriteItem = menu.findItem(R.id.favorite);
//        Drawable favoriteItemColor = favoriteItem.getIcon();
//        setupColorActionBarIcon(favoriteItemColor);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home :
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        progressBar.setVisibility(View.INVISIBLE);

    }

    private void showDateDialog(){

        /**
         * Calendar untuk mendapatkan tanggal sekarang
         */
        Calendar newCalendar = Calendar.getInstance();

        /**
         * Initiate DatePicker dialog
         */
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                /**
                 * Method ini dipanggil saat kita selesai memilih tanggal di DatePicker
                 */

                /**
                 * Set Calendar untuk menampung tanggal yang dipilih
                 */
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);

                /**
                 * Update TextView dengan tanggal yang kita pilih
                 */
//                tvDateResult.setText("Tanggal dipilih : "+dateFormatter.format(newDate.getTime()));
                btDatePicker = (Button) findViewById(R.id.pick_dates);
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String formattedDate = df.format(newDate.getTime());
                currentDatePicked = formattedDate;
                btDatePicker.setText(formattedDate);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }

    @Override
    public void setMeal(Meals.Meal meal) {
        Picasso.get().load(meal.getStrMealThumb()).into(mealThumb);
        collapsingToolbarLayout.setTitle(meal.getStrMeal());
        category.setText(meal.getStrCategory());
        country.setText(meal.getStrArea());
        instructions.setText(meal.getStrInstructions());
        setupActionBar();

        //===

        if (meal.getStrIngredient1() != null && !meal.getStrIngredient1().isEmpty()) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient1());
        }
        if (meal.getStrIngredient2() != null && !meal.getStrIngredient2().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient2());
        }
        if ( meal.getStrIngredient3() != null && !meal.getStrIngredient3().isEmpty()) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient3());
        }
        if (meal.getStrIngredient4() != null && !meal.getStrIngredient4().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient4());
        }
        if (meal.getStrIngredient5() != null && !meal.getStrIngredient5().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient5());
        }
        if (meal.getStrIngredient6() != null && !meal.getStrIngredient6().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient6());
        }
        if (meal.getStrIngredient7() != null && !meal.getStrIngredient7().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient7());
        }
        if (meal.getStrIngredient8() != null && !meal.getStrIngredient8().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient8());
        }
        if (meal.getStrIngredient9() != null && !meal.getStrIngredient9().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient9());
        }
        if (meal.getStrIngredient10() != null && !meal.getStrIngredient10().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient10());
        }
        if (meal.getStrIngredient11() != null && !meal.getStrIngredient11().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient11());
        }
        if (meal.getStrIngredient12() != null && !meal.getStrIngredient12().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient12());
        }
        if (meal.getStrIngredient13() != null && !meal.getStrIngredient13().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient13());
        }
        if (meal.getStrIngredient14() != null && !meal.getStrIngredient14().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient14());
        }
        if (meal.getStrIngredient15() != null && !meal.getStrIngredient15().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient15());
        }
        if (meal.getStrIngredient16() != null && !meal.getStrIngredient16().isEmpty() ) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient16());
        }
        if ( meal.getStrIngredient17() != null && !meal.getStrIngredient17().isEmpty()) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient17());
        }
        if ( meal.getStrIngredient18() != null && !meal.getStrIngredient18().isEmpty()) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient18());
        }
        if ( meal.getStrIngredient19() != null && !meal.getStrIngredient19().isEmpty()) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient19());
        }
        if (meal.getStrIngredient20() != null && !meal.getStrIngredient20().isEmpty()) {
            ingredients.append("\n \u2022 " + meal.getStrIngredient20());
        }

        if (meal.getStrMeasure1() != null && !meal.getStrMeasure1().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure1().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure1());
        }
        if (meal.getStrMeasure2() != null && !meal.getStrMeasure2().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure2().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure2());
        }
        if (meal.getStrMeasure3() != null && !meal.getStrMeasure3().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure3().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure3());
        }
        if (meal.getStrMeasure4() != null && !meal.getStrMeasure4().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure4().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure4());
        }
        if (meal.getStrMeasure5() != null && !meal.getStrMeasure5().isEmpty()
                &&  !Character.isWhitespace(meal.getStrMeasure5().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure5());
        }
        if (meal.getStrMeasure6() != null && !meal.getStrMeasure6().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure6().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure6());
        }
        if (meal.getStrMeasure7() != null && !meal.getStrMeasure7().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure7().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure7());
        }
        if (meal.getStrMeasure8() != null && !meal.getStrMeasure8().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure8().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure8());
        }
        if (meal.getStrMeasure9() != null && !meal.getStrMeasure9().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure9().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure9());
        }
        if (meal.getStrMeasure10() != null && !meal.getStrMeasure10().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure10().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure10());
        }
        if (meal.getStrMeasure11() != null && !meal.getStrMeasure11().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure11().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure11());
        }
        if (meal.getStrMeasure12() != null && !meal.getStrMeasure12().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure12().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure12());
        }
        if (meal.getStrMeasure13() != null && !meal.getStrMeasure13().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure13().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure13());
        }
        if (meal.getStrMeasure14() != null && !meal.getStrMeasure14().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure14().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure14());
        }
        if (meal.getStrMeasure15() != null && !meal.getStrMeasure15().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure15().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure15());
        }
        if (meal.getStrMeasure16() != null && !meal.getStrMeasure16().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure16().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure16());
        }
        if ( meal.getStrMeasure17() != null && !meal.getStrMeasure17().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure17().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure17());
        }
        if (meal.getStrMeasure18() != null && !meal.getStrMeasure18().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure18().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure18());
        }
        if (meal.getStrMeasure19() != null && !meal.getStrMeasure19().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure19().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure19());
        }
        if ( meal.getStrMeasure20() != null && !meal.getStrMeasure20().isEmpty()
                && !Character.isWhitespace(meal.getStrMeasure20().charAt(0))) {
            measures.append("\n : " + meal.getStrMeasure20());
        }

        int calories = 500;
        calorieFood.setText(String.valueOf(calories));
        SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE); //untuk menyimpan Target Cal
        String Uid = prefs.getString("UserID", null);
        add_to_database.setOnClickListener(v -> {
            Calendar time = Calendar.getInstance();
            String hour = String.valueOf(time.get(Calendar.HOUR));
            String minute = String.valueOf(time.get(Calendar.MINUTE));
            String second = String.valueOf(time.get(Calendar.SECOND));

            String idData = String.format("%s-%s-%s", hour, minute, second);

            EatenHelper masukList = new EatenHelper(idData, meal.getStrMeal(), String.valueOf(calories));
            rootNode = FirebaseDatabase.getInstance("https://uasmobile-14a7b-default-rtdb.asia-southeast1.firebasedatabase.app");
            reference = rootNode.getReference("eatenMeal");
            //1 diganti dengan user
            reference.child(Uid).child(currentDatePicked).child(idData).setValue(masukList);
            Toast.makeText(this,"Makanan berhasil ditambahkan ke dalam List", Toast.LENGTH_LONG).show();
            Log.i("DetailActivity", meal.getStrMeal());
        });

        source.setOnClickListener(v -> {
            Intent intentSource = new Intent(Intent.ACTION_VIEW);
            intentSource.setData(Uri.parse(meal.getStrSource()));
            startActivity(intentSource);
        });
        Log.w("TAG name", meal.getStrMeal());
    }
    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Log.i("CekIntent", "OK");
                        Intent homeIntent = new Intent(DetailActivity.this, homeOverview.class);
                        startActivity(homeIntent);
                        finish();
                        return true;
                    case R.id.navigation_food_app:
                        Intent foodIntent = new Intent(DetailActivity.this, MainActivity.class);
                        startActivity(foodIntent);
                        return true;
                    case R.id.navigation_settings:
                        Intent settingsIntent = new Intent(DetailActivity.this, LogoutResetActivity.class);
                        startActivity(settingsIntent);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }
    @Override
    public void setCategory(List<Categories.Category> category) {

    }

    @Override
    public void onErrorLoading(String message) {
        Utils.showDialogMessage(this,"Error", message);
    }
}

