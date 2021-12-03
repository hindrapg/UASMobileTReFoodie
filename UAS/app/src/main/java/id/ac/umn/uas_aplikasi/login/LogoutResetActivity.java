package id.ac.umn.uas_aplikasi.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import id.ac.umn.uas_aplikasi.CalorieCalc.GetStart;
import id.ac.umn.uas_aplikasi.MainActivity;
import id.ac.umn.uas_aplikasi.R;
import id.ac.umn.uas_aplikasi.overview.homeOverview;

public class LogoutResetActivity extends AppCompatActivity {

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String Uid = null;

    int targetWeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_reset);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Uid = user.getUid();
            Log.d("UserID", "User ID adalah : " + Uid);
        }
        rootNode = FirebaseDatabase.getInstance("https://uasmobile-14a7b-default-rtdb.asia-southeast1.firebasedatabase.app");
        reference = rootNode.getReference("userID");
        DatabaseReference reference2 = reference.child(Uid).child("UID");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserHelper value = dataSnapshot.getValue(UserHelper.class);
                targetWeight = value.getWeightTarget();
                Log.d("Nilai", "Value is: " + targetWeight);
                setTVWeight();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });


        if (savedInstanceState == null) {
            bottomNav.setSelectedItemId(R.id.navigation_settings); // change to whichever id should be default
        }
        setupBottomNavigation();
        
        Button btnReset = findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE); //untuk flag
                prefs.edit().putInt("flagStart", 0).apply();
                startActivity(new Intent(LogoutResetActivity.this, GetStart.class));
                finish();
            }
        });

        Button btnLogout = findViewById(R.id.btnLogout);

        mAuth= FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        String email= user.getEmail();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(LogoutResetActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void setTVWeight(){

        TextView TVtargetCal = findViewById(R.id.TVtargetCal);
        Log.i("TargetWeight", String.valueOf(targetWeight));

        TVtargetCal.setText("Your target Weight is " + targetWeight + "kg");
    }


    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        Log.i("CekIntent", "OK");
                        Intent homeIntent = new Intent(LogoutResetActivity.this, homeOverview.class);
                        startActivity(homeIntent);
                        finish();
                        return true;
                    case R.id.navigation_food_app:
                        Intent foodIntent = new Intent(LogoutResetActivity.this, MainActivity.class);
                        startActivity(foodIntent);
                        return true;
                    case R.id.navigation_settings:
                        Intent settingsIntent = new Intent(LogoutResetActivity.this, LogoutResetActivity.class);
                        startActivity(settingsIntent);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }
}