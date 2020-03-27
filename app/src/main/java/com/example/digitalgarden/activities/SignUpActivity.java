package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalgarden.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUpActivity extends AppCompatActivity {
    private EditText emailText, passwordText;
    private Button signUpButton;
    private TextView signInTextView;
    private FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailEditText);
        passwordText = findViewById(R.id.passwordEditText);
        signInTextView = findViewById(R.id.signInTextView);
        signUpButton = findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                if(email.isEmpty()){
                    emailText.setError("Please enter email ID");
                    emailText.requestFocus();
                } else if (password.isEmpty()){
                    passwordText.setError("Please enter a password");
                    passwordText.requestFocus();
                }else if(!(email.isEmpty() && password.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "SignUp unsuccessful", Toast.LENGTH_SHORT).show();
                            }else{
                                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                SignUpActivity.this.finish();
                            }
                        }
                    });
                }else {
                    Toast.makeText(SignUpActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
