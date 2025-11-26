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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity2 extends AppCompatActivity {

    TextView textView1Title;
    EditText EtEmail2, EtPassword2;
    Button button21, button22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        init();
        button21.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogIn(view);
            }
        });
        button22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent tt = new Intent(MainActivity2.this, MainActivity.class);
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
            Intent tt = new Intent(MainActivity2.this, MainScreen.class);
            startActivity(tt);
        }
    }

    public void init()
    {
        textView1Title = findViewById(R.id.textView1Title);
        EtEmail2 = findViewById(R.id.EtEmail2);
        EtPassword2 = findViewById(R.id.EtPassword2);
        button21 = findViewById(R.id.button21);
        button22 = findViewById(R.id.button22);
    }
    public void LogIn(View view)
    {
        init();
        String email = EtEmail2.getText().toString();
        String pass = EtPassword2.getText().toString();
        if (email.isEmpty() || pass.isEmpty())
        {
            textView1Title.setText("Please fill all fields");
        }
        else
        {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Connecting");
            pd.setMessage("Logging in user...");
            pd.show();
            FBref.refAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    pd.dismiss();
                    if (task.isSuccessful()) {
                        Log.i("MainActivity", "createUserWithEmailAndPassword:success");
                        FirebaseUser user = FBref.refAuth.getCurrentUser();
                        Intent tt = new Intent(MainActivity2.this, MainScreen.class);
                        startActivity(tt);
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
                            textView1Title.setText("Invalid credentials.");
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