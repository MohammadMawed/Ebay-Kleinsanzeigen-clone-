package com.mawed.firebaselogin;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUp extends AppCompatActivity {

    EditText EmailEditText;
    EditText PasswordEditText;
    EditText UsernameEditText;
    Button SignUpButton;
    TextView SignInText;
    TextView NewUserText;
    ImageView SignInTextContainer;
    String userID;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        EmailEditText = findViewById(R.id.editTextTextEmailAddress);
        PasswordEditText = findViewById(R.id.editTextTextPassword);
        UsernameEditText = findViewById(R.id.editTextTextUsername);
        SignInText = findViewById(R.id.button2);
        NewUserText = findViewById(R.id.NewUserTextView);
        SignInTextContainer = findViewById(R.id.imageView3);
        SignUpButton = findViewById(R.id.signUpButton);


        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        PasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    SignUpButton.setEnabled(false);
                } else {
                    SignUpButton.setEnabled(true);
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
                    SignUpButton.setEnabled(false);
                } else {
                    SignUpButton.setEnabled(true);
                }
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        UsernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    SignUpButton.setEnabled(false);
                } else {
                    SignUpButton.setEnabled(true);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void SingInMethod(View view) {
        SignNewUsersUp();
    }

    public void SignNewUsersUp() {

        String Username = UsernameEditText.getText().toString();
        String UserEmail = EmailEditText.getText().toString().trim();
        String UserPassword = PasswordEditText.getText().toString().trim();

        firebaseAuth = FirebaseAuth.getInstance();
        //Checking if the user is already exists
        firebaseAuth.fetchSignInMethodsForEmail(UserEmail)
                .addOnCompleteListener(task -> {
                    if (task.getResult().getSignInMethods().size() == 0){
                        // email not existed
                        firebaseAuth.createUserWithEmailAndPassword(UserEmail, UserPassword)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        firebaseAuth.getCurrentUser().sendEmailVerification()
                                                .addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()) {
                                                        userID = firebaseAuth.getCurrentUser().getUid();
                                                        Map<String, String> userData = new HashMap<>();
                                                        DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);
                                                        userData.put("username", Username);
                                                        documentReference.set(userData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d("TAG", "Successfully: User " + userID + " was created!");
                                                                Toast.makeText(SignUp.this, "Registered Successfully, "
                                                                                + "Please check your Email for verification",
                                                                        Toast.LENGTH_LONG).show();
                                                                EmailEditText.setText("");
                                                                PasswordEditText.setText("");
                                                                startActivity(new Intent(SignUp.this, MainActivity.class));
                                                            }
                                                        });

                                                    } else {
                                                        Toast.makeText(SignUp.this, task2.getException().getMessage(),
                                                                Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(SignUp.this, "Sign in error", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }else {
                        // email existed
                        Toast.makeText(SignUp.this, "This email is already taken", Toast.LENGTH_LONG).show();
                    }
                });

    }
}