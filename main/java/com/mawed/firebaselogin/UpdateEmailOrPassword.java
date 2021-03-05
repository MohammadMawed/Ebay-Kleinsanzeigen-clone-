package com.mawed.firebaselogin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.PasswordAuthentication;
import java.util.Objects;

public class UpdateEmailOrPassword extends AppCompatActivity {

    Button saveButton;
    EditText EmailEditText;
    FirebaseAuth firebaseAuth;
    TextView Title;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email_or_password);

        Title = findViewById(R.id.textView2);
        EmailEditText = findViewById(R.id.editTextTextEmailAddress2);

        user = FirebaseAuth.getInstance().getCurrentUser();
        saveButton = findViewById(R.id.button7);

        saveButton.setEnabled(false);

        if(getIntent().hasExtra("password")){
            EmailEditText.setHint("Password");
            EmailEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
            Title.setText("Update your Password");
        }else {
            Title.setText("Update your Email");
        }


        EmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().equals("")) {
                    saveButton.setEnabled(false);
                } else {
                    saveButton.setEnabled(true);
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

    public void SaveChanges(View view) {
        if (getIntent().hasExtra("password")){
            updatePassword();
        }else {
            updateEmail();
        }
    }

    public void updatePassword() {
        // [START update_password]

        String newPassword;
        newPassword = EmailEditText.getText().toString();
        user = FirebaseAuth.getInstance().getCurrentUser();

        user.updatePassword(newPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(UpdateEmailOrPassword.this, "Password updated successfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(UpdateEmailOrPassword.this, MainUI.class));
                        finish();
                    }
                });
        // [END update_password]
    }


    public void updateEmail() {
        // [START update_email]
        String newEmail = EmailEditText.getText().toString();
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //Checking if the user is already exists
        firebaseAuth.fetchSignInMethodsForEmail(newEmail)
                .addOnCompleteListener(task -> {
                    if (Objects.requireNonNull(task.getResult()).getSignInMethods().size() == 0) {
                        // email not existed
                        user.updateEmail(newEmail)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(UpdateEmailOrPassword.this, "Email updated successfully", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(UpdateEmailOrPassword.this, MainUI.class));
                                        finish();
                                    }
                                });

                        // [END update_email]
                    }else {
                        // email existed
                        Toast.makeText(UpdateEmailOrPassword.this, "This email is already taken", Toast.LENGTH_LONG).show();
                    }
                });

    }

}