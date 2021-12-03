package id.ac.umn.uas_aplikasi.overview;

import static id.ac.umn.uas_aplikasi.MainActivity.EXTRA_DETAIL;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import id.ac.umn.uas_aplikasi.MainActivity;
import id.ac.umn.uas_aplikasi.R;
import id.ac.umn.uas_aplikasi.adapter.RecyclerViewOverviewListAdapter;
import id.ac.umn.uas_aplikasi.detail.DetailActivity;
import id.ac.umn.uas_aplikasi.detail.EatenHelper;
import id.ac.umn.uas_aplikasi.login.LogoutResetActivity;

public class homeOverview extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    private DatePickerDialog datePickerDialog;
    private Button btDatePicker;
    private String currentDatePicked;
    private int caloriesEaten = 0;
    private int targetCal = 0;
    String Uid = null;


//    @BindView(R.id.textUsedCalories)
//    TextView usedCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_overview);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.navigation_home); // change to whichever id should be default
        }
        setupBottomNavigation();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        currentDatePicked = formattedDate;

        SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE); //untuk menyimpan Target Cal
        targetCal = prefs.getInt("targetCal", 0);
        Uid = prefs.getString("UserID", null);

        getDataFirebase();


        btDatePicker = (Button) findViewById(R.id.pick_dates);
        btDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });
        btDatePicker.setText(currentDatePicked);
//        SharedPreferences prefs = getSharedPreferences("TargetCal", Context.MODE_PRIVATE);
//        Log.i("Isi Shared", String.valueOf(prefs.getInt("targetCal", 0)));
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
                getDataFirebase();
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        /**
         * Tampilkan DatePicker dialog
         */
        datePickerDialog.show();
    }
    
    private void getDataFirebase(){

        rootNode = FirebaseDatabase.getInstance("https://uasmobile-14a7b-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = rootNode.getReference("eatenMeal");
        DatabaseReference reference2 = reference.child(Uid).child(currentDatePicked);

        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                List<EatenHelper> eatenList = new ArrayList<EatenHelper>();
                caloriesEaten = 0;
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    EatenHelper value = postSnapshot.getValue(EatenHelper.class);
                    eatenList.add(value);
                    int cal = Integer.parseInt(value.getFoodCal());
                    caloriesEaten += cal;
                    Log.d("DataChange", "Value is: " + value.getFoodName());
                }
                RecyclerViewOverviewListAdapter adapter = new RecyclerViewOverviewListAdapter(homeOverview.this, R.layout.overview_list_layout
                        , eatenList);
                ListView mealListView = (ListView) findViewById(R.id.eatenListView);
                TextView usedCalories = findViewById(R.id.textUsedCalories);
                TextView remainCalories = findViewById(R.id.textRemainingCalories);
                int remaining = targetCal - caloriesEaten;
                remainCalories.setText(String.valueOf(remaining));
                usedCalories.setText(String.valueOf(caloriesEaten));
//                remainCalories.setText(String.valueOf(targetCal - caloriesEaten));
                mealListView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Failed", "Failed to read value.", error.toException());
            }
        });

    }

    public void delete(View v){
        EatenHelper deleteItem = (EatenHelper) v.getTag();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.delete_confirm);
        alert.setMessage("Are you sure you want to remove " + deleteItem.getFoodName() + " from your eaten list ?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                Log.i("Nama", deleteItem.getFoodName());
                rootNode = FirebaseDatabase.getInstance("https://uasmobile-14a7b-default-rtdb.asia-southeast1.firebasedatabase.app");
                reference = rootNode.getReference("eatenMeal");
                DatabaseReference reference2 = reference.child(Uid).child(currentDatePicked).child(deleteItem.getIdUser());
                reference2.removeValue();
                Toast.makeText(homeOverview.this,deleteItem.getFoodName() + " berhasil dihapus dari List", Toast.LENGTH_LONG).show();
            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void openDetail(View v){
        EatenHelper opening = (EatenHelper) v.getTag();
        Log.i("Nama", opening.getFoodName());
        Intent intent = new Intent(homeOverview.this, DetailActivity.class);
        intent.putExtra(EXTRA_DETAIL, opening.getFoodName());
        startActivity(intent);
    }


    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
//                            Intent homeIntent = new Intent(homeOverview.this, homeOverview.class);
//                            startActivity(homeIntent);
                        return true;
                    case R.id.navigation_food_app:
                        Intent foodIntent = new Intent(homeOverview.this, MainActivity.class);
                        startActivity(foodIntent);
//                        finish();
                        return true;
                    case R.id.navigation_settings:
                        Intent settingsIntent = new Intent(homeOverview.this, LogoutResetActivity.class);
                        startActivity(settingsIntent);
//                        finish();
                        return true;
                }
                return false;
            }
        });
    }

}