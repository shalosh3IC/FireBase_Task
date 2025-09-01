package com.example.firebase_task;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApiNotAvailableException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView textView1Title;
    EditText EtEmail, EtPassword;
    Button button, button2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUp(view);
                FirebaseUser user = FBref.refAuth.getCurrentUser();
                if(user != null)
                {
                    Intent tt = new Intent(MainActivity.this, MainScreen.class);
                    startActivity(tt);
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tt = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(tt);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FBref.refAuth.getCurrentUser();
        if(user != null)
        {
            Intent tt = new Intent(MainActivity.this, MainScreen.class);
            startActivity(tt);
        }
    }

    public void init()
    {
        textView1Title = findViewById(R.id.textView1Title);
        EtEmail = findViewById(R.id.EtEmail);
        EtPassword = findViewById(R.id.EtPassword);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
    }
    public void SignUp(View view)
    {
        init();
        String email = EtEmail.getText().toString();
        String pass = EtPassword.getText().toString();
        if (email.isEmpty() || pass.isEmpty())
        {
            textView1Title.setText("Please fill all fields");
        }
        else
        {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Connecting");
            pd.setMessage("Creating user...");
            pd.show();
            FBref.refAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Log.i("MainActivity", "createUserWithEmailAndPassword:success");
                        FirebaseUser user = FBref.refAuth.getCurrentUser();
                        textView1Title.setText("User created successfully\nUid: "+user.getUid());
                    }
                    else
                    {
                        Exception exp = task.getException();
                        if (exp instanceof FirebaseAuthInvalidUserException)
                        {
                            textView1Title.setText("Invalid email address.");
                        }
                        else if (exp instanceof FirebaseAuthWeakPasswordException)
                        {
                            textView1Title.setText("Password too weak.");
                        }
                        else if (exp instanceof FirebaseAuthUserCollisionException)
                        {
                            textView1Title.setText("User already exists.");
                        }
                        else if (exp instanceof FirebaseAuthInvalidCredentialsException)
                        {
                            textView1Title.setText("General authentication failure.");
                        }
                        else if (exp instanceof FirebaseNetworkException)
                        {
                            textView1Title.setText("Network error. Please check your connection and try again.");
                        }
                        else
                        {
                            textView1Title.setText("An error occurred. Please try again later.");
                        }
                    }
                }
            });
        }
    }
}