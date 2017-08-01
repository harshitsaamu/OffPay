package hgc.com.offpay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Toolbar toolbar;
    TextView CardBalance,CardId;
    FirebaseAuth.AuthStateListener mAuthListener;
    CardView send,recieve,AddIt;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    static boolean calledAlready = false;
    String balance;
    boolean network=false;
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id
                .coordinatorLayout);
        //check for connection
     checkNetwork();
        //find Id
        toolbar=(Toolbar)findViewById(R.id.toolbar);
        CardId= (TextView) findViewById(R.id.card_id);
        CardBalance=(TextView)findViewById(R.id.card_balance);
        firebaseAuth = FirebaseAuth.getInstance();
        send=(CardView)findViewById(R.id.send_money);
        recieve=(CardView)findViewById(R.id.receive);
        AddIt=(CardView)findViewById(R.id.money_addd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.mipmap.ic_launc);
        //save data offline
        if (!calledAlready)
        {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            calledAlready = true;
        }


        //DataBase Root
        firebaseDatabase=FirebaseDatabase.getInstance();
        mDatabase= firebaseDatabase.getReference();



        //Check for user if null
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(MainActivity.this, signin.class));

        } else {
            user = FirebaseAuth.getInstance().getCurrentUser();
            CardId.setText(user.getEmail());



            //Update Balance in the card
        firebaseDatabase.getReference(user.getUid()).child("balance").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                balance=dataSnapshot.getValue(String.class);
                CardBalance.setText("₹"+balance);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, ""+databaseError, Toast.LENGTH_SHORT).show();
            }

        });



            //If new user add its Uid in database
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(user.getUid())) {

                    }
                    else {
                        mDatabase.child(user.getUid()).child("balance").setValue("0");
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this, ""+databaseError, Toast.LENGTH_SHORT).show();
                }

            });
        }


     //Send Money
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,sendMoney.class);
                i.putExtra("balance",balance);
                startActivity(i);
            }
        });

        //Add Money
        AddIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this,AddMoney.class);
                i.putExtra("balance",balance);
                startActivity(i);
            }
        });


        //Receive Money
        recieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator=new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan QR code generated by sender");
                integrator.setBeepEnabled(true);
                integrator.setCameraId(0);
                integrator.setOrientationLocked(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();

            }
        });

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    finish();
                    startActivity(new Intent(MainActivity.this, signin.class));
                }
            }
        };
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //offline
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    //Scan Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result=IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(result!=null)
        {
            if(result.getContents()==null)
            {
                Toast.makeText(this, "You have cancelled the scanning", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, ""+result.getContents(), Toast.LENGTH_SHORT).show();
                int old_total=Integer.parseInt(balance);
                int add_total=Integer.parseInt(result.getContents());
                int total=old_total+add_total;
                        mDatabase.child(user.getUid()).child("balance").setValue(""+total);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sign_out) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(MainActivity.this, signin.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void checkNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if(isConnected)
        {

        }
        else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        checkNetwork();
                        }
                    });

// Changing message text color
            snackbar.setActionTextColor(Color.RED);

// Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}