package com.example.fitsheild;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.inappmessaging.model.Button;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    EditText SignupName, SignupEmail, SignupUsername, SignupPassword;
    TextView LoginRedirectText;
    View SignupButton;
    FirebaseDatabase database;
    DatabaseReference reference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        SignupName = findViewById(R.id.signup_name);
        SignupEmail = findViewById(R.id.signup_email);
        SignupUsername = findViewById(R.id.signup_username);
        SignupPassword = findViewById(R.id.signup_password);
        SignupButton = findViewById(R.id.signup_button);
        LoginRedirectText = findViewById(R.id.loginRedirectText);

        SignupButton.setOnClickListener(view -> {
            if (!validateInput()) {
                return; // If validation fails, do not proceed
            }
            database = FirebaseDatabase.getInstance();
            reference = database.getReference("users");

            String name = SignupName.getText().toString();
            String email = SignupEmail.getText().toString();
            String username = SignupUsername.getText().toString();
            String password = SignupPassword.getText().toString();

            HelperClass helperClass = new HelperClass(name, email, username, password);
            reference.child(username).setValue(helperClass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Signup details saved successfully.");
                } else {
                    Log.d("Firebase", "Failed to save signup details: " + Objects.requireNonNull(task.getException()).getMessage());
                }
            });


            Toast.makeText(SignupActivity.this, "You have successfully signed up", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            startActivity(intent);
            finish();


        });

        LoginRedirectText.setOnClickListener(view -> {
            Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private boolean validateInput() {
        String name = SignupName.getText().toString().trim();
        String email = SignupEmail.getText().toString().trim();
        String username = SignupUsername.getText().toString().trim();
        String password = SignupPassword.getText().toString().trim();

        if (name.isEmpty()) {
            SignupName.setError("Name cannot be empty");
            SignupName.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            SignupEmail.setError("Email cannot be empty");
            SignupEmail.requestFocus();
            return false;
        }
        if (username.isEmpty()) {
            SignupUsername.setError("Username cannot be empty");
            SignupUsername.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            SignupPassword.setError("Password cannot be empty");
            SignupPassword.requestFocus();
            return false;
        }

        return true; // All inputs are valid
    }
}

