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

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class signin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,View.OnClickListener{
    private ProgressDialog progressdialog;
    private static final int RC_SIGN_IN = 61;
    private FirebaseAuth mAuth;
    SignInButton googlebut;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private CardView button_sign_in;
    private TextView text_username,text_password,text_orsignup,text_forgot_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {//need to update
                    finish();
                    startActivity(new Intent(signin.this, MainActivity.class));
                }
            }
        };
        setContentView(R.layout.activity_signin);

        progressdialog = new ProgressDialog(signin.this);

        mAuth = FirebaseAuth.getInstance();
        button_sign_in = (CardView) findViewById(R.id.bsignin);
        text_password = (TextView) findViewById(R.id.field_password);
        text_username = (TextView) findViewById(R.id.field_username);
        text_orsignup=(TextView)findViewById(R.id.orsignup);
        text_forgot_password=(TextView)findViewById(R.id.forgotpass);

        googlebut=(SignInButton)findViewById(R.id.gsign);
        googlebut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(signin.this).enableAutoManage(signin.this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(signin.this, "Can't Connect", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();
        button_sign_in.setOnClickListener(this);
        text_orsignup.setOnClickListener(this);
        text_forgot_password.setOnClickListener(this);

    }

    private void sign_in_simple()
    {   String email,password;
        progressdialog.setMessage("Signing In...");
        progressdialog.show();
        email = text_username.getText().toString().trim();
        password = text_password.getText().toString().trim();
        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(password)) {
            progressdialog.dismiss();
            Toast.makeText(signin.this, "Empty Fields", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressdialog.dismiss();
                    if (task.isSuccessful()) {
                        Intent i = new Intent(signin.this, MainActivity.class);
                        finish();
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(signin.this, "Authentication Failed! ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        progressdialog.setMessage("Please Wait...");
        progressdialog.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(this, "Sigin In Failed", Toast.LENGTH_SHORT).show();
                progressdialog.dismiss();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        progressdialog.setMessage("Please Wait...");
        progressdialog.show();

        final Intent intent = new Intent(signin.this, MainActivity.class);
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(signin.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        finish();
                        startActivity(intent);


                        if (!task.isSuccessful()) {

                            Toast.makeText(signin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                        progressdialog.dismiss();
                    }
                });
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        progressdialog.dismiss();
        Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if(v==button_sign_in)
        {
            sign_in_simple();
        }
        else if(v==text_orsignup)
        {
            Intent i=new Intent(signin.this,Sign_UP.class);
            finish();
            startActivity(i);
        }
        else if(v==text_forgot_password)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("Enter your email");
            final EditText editmail=new EditText(this);
            builder.setView(editmail);
            builder.setPositiveButton("Send Mail", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String mailid=editmail.getText().toString().trim();
                    mAuth.sendPasswordResetEmail(mailid).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(signin.this, "Email Sent...", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(signin.this, "An error occurred...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
            builder.show();
        }
    }
}
