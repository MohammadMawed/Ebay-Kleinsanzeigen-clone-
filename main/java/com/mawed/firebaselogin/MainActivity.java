package com.mawed.firebaselogin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText EmailEditText;
    EditText PasswordEditText;
    TextView SignInText;
    TextView NewUserText;
    ImageView SignInTextContainer;
    Button LoginButton;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EmailEditText = findViewById(R.id.editTextTextEmailAddress);
        PasswordEditText = findViewById(R.id.editTextTextPassword);
        SignInText = findViewById(R.id.button2);
        NewUserText = findViewById(R.id.NewUserTextView);
        SignInTextContainer = findViewById(R.id.imageView3);
        LoginButton = findViewById(R.id.loginButton);

        LoginButton.setEnabled(false);

        PasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    LoginButton.setEnabled(false);
                } else {
                    LoginButton.setEnabled(true);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        EmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    LoginButton.setEnabled(false);
                } else {
                    LoginButton.setEnabled(true);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void LoginMethod(View view) {
        LogInFirebase();
    }

    @SuppressLint({"SetTextI18n", "ResourceAsColor"})
    public void SingInMethod(View view) {
        Intent SignUpIntent = new Intent(MainActivity.this, SignUp.class);
        startActivity(SignUpIntent);
    }

    public void ResetPasswordMethod(View view) {
        ResetPassword();
    }

    public void LogInFirebase() {

        String email = EmailEditText.getText().toString().trim();
        String password = PasswordEditText.getText().toString().trim();

            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    user = firebaseAuth.getCurrentUser();
                    // Sign in success, Show the main UI with the signed-in user's information
                    Intent LogInUIIntent = new Intent(MainActivity.this, MainUI.class);
                    startActivity(LogInUIIntent);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            });

    }

    public void ResetPassword(){
        String email = EmailEditText.getText().toString().trim();
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isComplete()){
                Toast.makeText(MainActivity.this, "Password reset instructions sent to your email,"
                        + " Please check your mail box", Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

}