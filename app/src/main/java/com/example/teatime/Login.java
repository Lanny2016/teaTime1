package com.example.teatime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class Login extends AppCompatActivity {
    // get token for fireBase
    private static final String TAG = "MainActivity";
    private EditText userEmail;
    private EditText user_password;
    private Button login_button;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // referencing the edit texts
        userEmail = (EditText) findViewById(R.id.email_id);
        user_password = (EditText) findViewById(R.id.password_id);
        login_button = (Button) findViewById(R.id.login_button_id);
        progressBar = (ProgressBar) findViewById(R.id.progress_circular_id);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call the login method
                userLogin();
                // call SharedPreference method
                SharedPreference();
            }
        });

        //getting token method
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
//To do//
                            return;
                        }

// Get the Instance ID token//
                        String token = task.getResult().getToken();
                        String msg = getString(R.string.fcm_token, token);
                        Log.d(TAG, msg);

                    }
                });

    }
    public void SharedPreference(){

        //get FirebaseDatabase userId
        FirebaseUser user = mAuth.getCurrentUser();
        String user_mail = user.getEmail().split("@")[0];
        // sharedPreference object
        SharedPreferences sharedPref = getSharedPreferences("inputInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("userName",user_mail);
        editor.apply();

    }

    // create userLogin method
    private void userLogin() {
        // getting value from editText
        String email = userEmail.getText().toString().trim();
        String password = user_password.getText().toString().trim();
        // validation for editText input
        if (email.isEmpty()) {
            userEmail.setError("Email is required");
            userEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            userEmail.setError("Please enter a valid email");
            userEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            user_password.setError("Password is required");
            user_password.requestFocus();
            return;
        }
        // validate minimum length of the password
        if (password.length() < 6) {
            user_password.setError("Minimum length of  password  should be 6");
            user_password.requestFocus();
            return;

        }
        progressBar.setVisibility(View.VISIBLE);
        // login method
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(Login.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            progressBar = (ProgressBar)findViewById(R.id.progress_circular_id);
                            progressBar.setVisibility(View.GONE);
                        } else {
                            // If sign in fails, display a message to the user.

                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

}