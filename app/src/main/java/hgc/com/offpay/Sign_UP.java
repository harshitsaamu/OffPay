package hgc.com.offpay;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign_UP extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CardView button_sign_up;
    private ProgressDialog pdLoading;
    private TextView text_username, text_password, text_orsignin, text_forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent i = new Intent(Sign_UP.this, MainActivity.class);
                    finish();
                    startActivity(i);
                }
            }
        };
        pdLoading = new ProgressDialog(this);
        pdLoading.setMessage("Signing In...");
        button_sign_up = (CardView) findViewById(R.id.bsignup);
        text_password = (TextView) findViewById(R.id.sfield_password);
        text_username = (TextView) findViewById(R.id.sfield_username);
        text_orsignin = (TextView) findViewById(R.id.orsignin);
        text_forgot_password = (TextView) findViewById(R.id.sfield_forgpass);

        button_sign_up.setOnClickListener(this);
        text_forgot_password.setOnClickListener(this);
        text_orsignin.setOnClickListener(this);
    }

    private void Signup() {
        pdLoading.show();
        final String email, password;
        email = text_username.getText().toString().trim();
        password = text_password.getText().toString().trim();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(Sign_UP.this, "Empty Fields", Toast.LENGTH_SHORT).show();
            pdLoading.dismiss();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Sign_UP.this, "Account Updated", Toast.LENGTH_SHORT).show();
                        pdLoading.dismiss();
                        finish();
                        Intent i = new Intent(Sign_UP.this, MainActivity.class);
                        startActivity(i);
                    } else {
                        pdLoading.dismiss();
                        Toast.makeText(Sign_UP.this, "An error occurred..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == button_sign_up) {
            Signup();
        } else if (v == text_forgot_password) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter your email");
            final EditText editmail = new EditText(this);
            builder.setView(editmail);
            builder.setPositiveButton("Send Mail", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String mailid = editmail.getText().toString().trim();
                    mAuth.sendPasswordResetEmail(mailid).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Sign_UP.this, "Email Sent...", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Sign_UP.this, "An error occurred...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.show();
        } else if (v == text_orsignin) {

            Intent i = new Intent(Sign_UP.this,MainActivity.class);
            finish();
            startActivity(i);
        }
    }
}
