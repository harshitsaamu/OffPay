package hgc.com.offpay;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.net.URLEncoder;

public class sendMoney extends AppCompatActivity {
CardView generateCode;
    FirebaseUser user;
    EditText fieldAmount;
    ImageView QRimage;
    TextView qrtext;
    private String balance;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        super.onCreate(savedInstanceState);
        firebaseDatabase=FirebaseDatabase.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        mDatabase=firebaseDatabase.getReference();
        setContentView(R.layout.activity_send_money);
        generateCode=(CardView)findViewById(R.id.generate_send_code);
        fieldAmount=(EditText)findViewById(R.id.field_amount);
        qrtext=(TextView)findViewById(R.id.qr_text);
        QRimage=(ImageView)findViewById(R.id.QR_image);
        generateCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String money;
                money=fieldAmount.getText().toString();
                balance=getIntent().getExtras().getString("balance");
                if(money.equals(""))
                {
                    Toast.makeText(sendMoney.this, "Enter amount..", Toast.LENGTH_SHORT).show();
                }
               else if(Integer.parseInt(balance)<Integer.parseInt(money))
                {
                    Toast.makeText(sendMoney.this, "not enough money in account", Toast.LENGTH_SHORT).show();
                }
                else{
                MultiFormatWriter multiFormatWriter=new MultiFormatWriter();
                try{
                    BitMatrix bitMatrix=multiFormatWriter.encode(money, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder=new BarcodeEncoder();
                    Bitmap bitmap=barcodeEncoder.createBitmap(bitMatrix);
                    QRimage.setImageBitmap(bitmap);
                    qrtext.setVisibility(View.VISIBLE);
                    mDatabase.child(user.getUid()).child("balance").setValue(""+(Integer.parseInt(balance)-Integer.parseInt(money)));
                } catch (WriterException e) {
                    e.printStackTrace();
                    Toast.makeText(sendMoney.this, "An error occured", Toast.LENGTH_SHORT).show();
                }
            }
            }
        });
    }
}
