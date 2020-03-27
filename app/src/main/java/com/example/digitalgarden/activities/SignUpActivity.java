package com.example.digitalgarden.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.digitalgarden.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class SignUpActivity extends AppCompatActivity {
    private EditText emailText, passwordText , nameText;
    private Button signUpButton;
    private TextView signInTextView;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mUser;
    private DocumentReference mDocRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirebaseAuth = FirebaseAuth.getInstance();
        emailText = findViewById(R.id.emailEditText);
        passwordText = findViewById(R.id.passwordEditText);
        signInTextView = findViewById(R.id.signInTextView);
        signUpButton = findViewById(R.id.signUpButton);
        nameText = findViewById(R.id.nameEditText);
        mDocRef = FirebaseFirestore.getInstance().collection("Users").document("info");

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                String name = nameText.getText().toString();
                if(email.isEmpty()){
                    emailText.setError("Please enter email ID");
                    emailText.requestFocus();
                } else if (password.isEmpty()){
                    passwordText.setError("Please enter a password");
                    passwordText.requestFocus();
                }else if(!(email.isEmpty() && password.isEmpty() && name.isEmpty())) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUpActivity.this, "SignUp unsuccessful", Toast.LENGTH_SHORT).show();
                            }else{
                                //Next two lines is to get User ID
                                mUser = FirebaseAuth.getInstance().getCurrentUser();
                                mDocRef = FirebaseFirestore.getInstance().collection("Users").document(mUser.getUid());
                                String userUID = mUser.getUid();

                                //Setting the data to be sent to the firestore Database.
                                Map<String,Object> dataToSave = new HashMap<String,Object>();
                                dataToSave.put("Name",nameText.getText().toString());
                                dataToSave.put("Email",email);
                                dataToSave.put("UID",userUID);

                                //Saving the data to the Database
                                mDocRef.set(dataToSave).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i("Success,","Yayyy");
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                        Log.w("Oh no!","The data has not been saved");
                                    }
                                });

                                //Starting the main activity
                                startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                                SignUpActivity.this.finish();
                            }
                        }
                    });
                }else if(name.isEmpty()){
                    nameText.setError("Please enter your name");
                    nameText.requestFocus();
                }
                else {
                    Toast.makeText(SignUpActivity.this, "Error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
