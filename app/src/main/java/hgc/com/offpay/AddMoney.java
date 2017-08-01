package hgc.com.offpay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMoney extends AppCompatActivity {
private CardView AdMoney;
    EditText FieldAmount;
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseDatabase firebaseDatabase;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        firebaseDatabase=FirebaseDatabase.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();
        mDatabase=firebaseDatabase.getReference();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AdMoney=(CardView)findViewById(R.id.add_money);
        FieldAmount=(EditText)findViewById(R.id.field_add_money);
        AdMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String now_balance=getIntent().getExtras().getString("balance");
                if(FieldAmount.getText().equals(""))
                {
                    Toast.makeText(AddMoney.this, "Enter an amount first..", Toast.LENGTH_SHORT).show();
                }
                else {
                    int updated_balance = Integer.parseInt(now_balance) + Integer.parseInt(FieldAmount.getText().toString());
                    mDatabase.child(user.getUid()).child("balance").setValue("" + updated_balance);
                    Toast.makeText(AddMoney.this, "Money added successfully", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }
}
