package id.ac.umn.uas_aplikasi.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import id.ac.umn.uas_aplikasi.CalorieCalc.GetStart;
import id.ac.umn.uas_aplikasi.R;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";
    TextView createnewAccount,forgotPassword;
    EditText inputEmail, inputPassword ;
    Button btnLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\\\.+[a-z]+";
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createnewAccount = findViewById(R.id.createNewAccount);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        forgotPassword=findViewById(R.id.forgotPassword);
        createnewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                perforLogin();
            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });
    }

    private void perforLogin() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();


        if (email.matches(emailPattern)) {
            inputEmail.setError("Enter Correct Email");
        } else if (password.isEmpty() || password.length() < 6) {
            inputPassword.setError("Enter Proper Password");

        } else {
            progressDialog.setMessage("Logging in...");
            progressDialog.setTitle("Loading");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if(user!=null){
                            String Uid = user.getUid();
                            Log.d(TAG, "User ID adalah : " + Uid);
//                            rootNode = FirebaseDatabase.getInstance("https://login-95925-default-rtdb.firebaseio.com/");
//                            reference = rootNode.getReference("userID");
//                            reference.child("akun").child("UID").setValue(Uid);
                        }else {
                            // User is signed out
                            Log.d(TAG, "onAuthStateChanged:signed_out");
                        }

                        progressDialog.dismiss();
                        sendUserToNextActivity();

                        Toast.makeText(LoginActivity.this,"Login succesfull",Toast.LENGTH_SHORT).show();
                    }else
                    {
                        progressDialog.dismiss();

                        Toast.makeText(LoginActivity.this,"Email/password Salah",Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(LoginActivity.this, GetStart.class));
            finish();
        }
    }
    private void sendUserToNextActivity() {

        Intent intent=new Intent(LoginActivity.this, GetStart.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}