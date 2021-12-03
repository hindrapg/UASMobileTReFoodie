package id.ac.umn.uas_aplikasi.CalorieCalc;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import id.ac.umn.uas_aplikasi.MainActivity;
import id.ac.umn.uas_aplikasi.R;
import id.ac.umn.uas_aplikasi.login.UserHelper;
import id.ac.umn.uas_aplikasi.overview.homeOverview;

public class GetStart extends AppCompatActivity {
    EditText tartgetWeight_et;
    EditText currentWeight_et;
    TextView calorieTarget;
    TextView calorieCurrent;
    Button calculateDayCalories;
    Button getStarted;
    static double parameter;
    int currentWeight;
    int targetWeight = 0;
    int currentCalories;
    int targetCalories = 0;

    FirebaseDatabase rootNode;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String Uid = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE); //untuk menyimpan Target Cal
        int flag = prefs.getInt("flagStart", 1);
        if(flag != 0){
            checkGas();
        }
        setContentView(R.layout.activity_get_start);

        tartgetWeight_et = findViewById(R.id.targetWeight);
        currentWeight_et = findViewById(R.id.currentWeight);
        calorieTarget = findViewById(R.id.calorieTarget);
        calorieCurrent = findViewById(R.id.calorieCurrent);
        calculateDayCalories = findViewById(R.id.calculateDayCal);
//        SharedPreferences prefStart = getSharedPreferences("TargetCal", Context.MODE_PRIVATE); //untuk menyimpan Target Cal
//        prefStart.edit().putInt("started", 0).commit();
//        int flagStart = prefStart.getInt("TargetCal", 0);


        Log.d("Nilai", "Nilai kalori: " + targetCalories);

    }

    public void checkDB(){
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
                if(dataSnapshot.exists()){
                    UserHelper value = dataSnapshot.getValue(UserHelper.class);
                    targetCalories = value.getTargetCal();
                    Log.d("Nilai", "Value is: " + targetCalories);
                    SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE); //untuk menyimpan Target Cal
                    prefs.edit().putInt("targetCal", targetCalories).apply();
                    prefs.edit().putString("UserID", Uid).apply();
                    prefs.edit().putInt("flagStart", 1);
                    checkGas();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Error", "Failed to read value.", error.toException());
            }
        });
    }
    public void checkGas(){
//        SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE); //untuk menyimpan Target Cal
//        int flag = prefs.getInt("flagStart", 0);
        if(targetCalories != 0){
            Log.w("Test", "ok");
            Intent intent = new Intent(GetStart.this, homeOverview.class);
            startActivity(intent);
            finish();
        }else{
            checkDB();
        }
    }

    //man : kg * 1 * 24
    //woman : kg * 0.9 * 24



    public void getStarted(View view) {
        if(targetCalories != 0) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            String Uid = null;
            if (user != null) {
                Uid = user.getUid();
                Log.d("UserID", "User ID adalah : " + Uid);
            }
//            prefs.edit().putString("UserID", Uid).apply();
            SharedPreferences prefs = getSharedPreferences("user", Context.MODE_PRIVATE); //untuk menyimpan Target Cal
            prefs.edit().putInt("targetCal", targetCalories).apply();
            prefs.edit().putString("UserID", Uid).apply();
            prefs.edit().putInt("flagStart", 1).apply();
            rootNode = FirebaseDatabase.getInstance("https://uasmobile-14a7b-default-rtdb.asia-southeast1.firebasedatabase.app");
            reference = rootNode.getReference("userID");
            UserHelper dataUser = new UserHelper(Uid, targetCalories, targetWeight);
            reference.child(Uid).child("UID").setValue(dataUser);

            Intent intent = new Intent(GetStart.this, MainActivity.class);
            startActivity(intent);
        }else{
            Toast toast = Toast.makeText(GetStart.this, "Tolong isi data", Toast.LENGTH_SHORT);
            toast.show();
        }

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        Button button = findViewById(R.id.calculateDayCal);


        // Check which radio button was clicked
        switch(view.getId()) {

            case R.id.radio_female:
                if (checked)
                    parameter = 0.9;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentWeight = Integer.valueOf(String.valueOf(currentWeight_et.getText()));
                        currentCalories = (int)(currentWeight* parameter * 24);
                        calorieCurrent.setText(String.valueOf((int) currentCalories));

                        targetWeight = Integer.valueOf(String.valueOf(tartgetWeight_et.getText()));
                        targetCalories =  (int)(targetWeight* parameter * 24);
                        calorieTarget.setText(String.valueOf((targetCalories)));


                    }
                });
                    break;
            case R.id.radio_male:
                if (checked)
                    parameter = 1.0;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currentWeight = Integer.valueOf(String.valueOf(currentWeight_et.getText()));
                        currentCalories = (int)(currentWeight* parameter * 24);
                        calorieCurrent.setText(String.valueOf((int) currentCalories));

                        targetWeight = Integer.valueOf(String.valueOf(tartgetWeight_et.getText()));
                        targetCalories =  (int)(targetWeight* parameter * 24);
                        calorieTarget.setText(String.valueOf((targetCalories)));

                    }
                });
                    break;

        }
    }
}
