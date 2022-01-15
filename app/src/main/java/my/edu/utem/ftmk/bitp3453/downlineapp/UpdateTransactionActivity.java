package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateTransactionActivity extends AppCompatActivity {

    private EditText etItemCode, etQuantity, etCustomerName, etCustomerAddress, etCustomerPhone, etBasePrice, etSalePrice;
    private Button btnSubmit2;

    private double latitude, longitude;
    private Geocoder geocoder;
    List<Address> address;
    GeoPoint geoPoint;

    String id;
    boolean check = true;

    FirebaseFirestore db;

    Map<String, Object> transaction = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_transaction);

        etItemCode = findViewById(R.id.etItemCode);
        etCustomerName = findViewById(R.id.etCustomerName);
        etQuantity = findViewById(R.id.etQuantity);
        etCustomerAddress = findViewById(R.id.etCustomerAddress);
        etCustomerPhone = findViewById(R.id.etCustomerPhone);
        etBasePrice = findViewById(R.id.etBasePrice);
        etSalePrice = findViewById(R.id.etSalePrice);

        btnSubmit2 = findViewById(R.id.btnSubmit2);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        db = FirebaseFirestore.getInstance();
        geocoder = new Geocoder(this, Locale.getDefault());

        btnSubmit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etItemCode.getText().toString().isEmpty() || etCustomerName.getText().toString().isEmpty() ||
                        etQuantity.getText().toString().isEmpty() || etCustomerAddress.getText().toString().isEmpty() ||
                        etCustomerPhone.getText().toString().isEmpty() || etBasePrice.getText().toString().isEmpty() ||
                        etSalePrice.getText().toString().isEmpty())
                {
                    Toast.makeText(UpdateTransactionActivity.this, "Please make sure all fields are filled.", Toast.LENGTH_SHORT).show();
                }
                else if(check == false)
                {
                    Toast.makeText(UpdateTransactionActivity.this, "Please make sure to fill all the information as required.", Toast.LENGTH_SHORT).show();
                }
                else{
                    UpdateTransaction();
                }
            }
        });

        checkFieldCondition();
    }

    public void UpdateTransaction(){

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        double quantity, saleprice, baseprice, sales, profit;

        quantity = Double.parseDouble(etQuantity.getText().toString());
        baseprice = Double.parseDouble(etBasePrice.getText().toString());
        saleprice = Double.parseDouble(etSalePrice.getText().toString());

        // calculate sales
        sales = saleprice * quantity;
        // calculate profit
        profit = sales-(baseprice * quantity);

        transaction.put("id",id);
        transaction.put("itemcode",etItemCode.getText().toString());
        transaction.put("quantity",etQuantity.getText().toString());
        transaction.put("customername",etCustomerName.getText().toString());
        transaction.put("customeraddress",etCustomerAddress.getText().toString());

        try {

            address = geocoder.getFromLocationName(transaction.get("customeraddress").toString(), 5);

            Address location = address.get(0);

            //Map<Object, Object> custAddress = address.get(0);

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());

            Toast.makeText(UpdateTransactionActivity.this, "latitude: " + latitude +", longitude: " + longitude, Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            e.printStackTrace();
        }

        transaction.put("latlng", geoPoint);

        transaction.put("customerphone",etCustomerPhone.getText().toString());
        transaction.put("sales",String.format("%.2f",sales));
        transaction.put("profit",String.format("%.2f",profit));
        transaction.put("transactiondate",date);


        // Add a new document with a generated ID
        db.collection("transactions")
                .add(transaction)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(UpdateTransactionActivity.this, "Transaction with item code, "+ transaction.get("itemcode") + " is updated successfully" , Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error adding document", e);
                    }
                });

    }

    public void checkFieldCondition(){

        etItemCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etItemCode.getText().toString().isEmpty()) {
                    etItemCode.setError("Please fill in the item code.");

                    check = false;
                } else {
                    etItemCode.setError(null);

                    check = true;
                }

            }
        });

        etCustomerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etCustomerName.getText().toString().isEmpty()) {
                    etCustomerName.setError("Please fill in the customer name.");

                    check = false;
                } else {
                    etCustomerName.setError(null);

                    check = true;
                }

            }
        });

        etCustomerAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etCustomerAddress.getText().toString().isEmpty()) {
                    etCustomerAddress.setError("Please fill in the customer address.");

                    check = false;
                } else {
                    etCustomerAddress.setError(null);

                    check = true;
                }

            }
        });

        etCustomerPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etCustomerPhone.getText().toString().isEmpty()) {
                    etCustomerPhone.setError("Please fill in the customer phone.");

                    check = false;
                } else {
                    etCustomerPhone.setError(null);

                    check = true;
                }

            }
        });

        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etQuantity.getText().toString().isEmpty()) {
                    etQuantity.setError("Please fill in the quantity.");

                    check = false;
                } else {
                    etQuantity.setError(null);

                    check = true;
                }

            }
        });

        etBasePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etBasePrice.getText().toString().isEmpty()) {
                    etBasePrice.setError("Please fill in the base price.");

                    check = false;
                } else {
                    etBasePrice.setError(null);

                    check = true;
                }

            }
        });

        etSalePrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etSalePrice.getText().toString().isEmpty()) {
                    etSalePrice.setError("Please fill in the sale price.");

                    check = false;
                } else {
                    etSalePrice.setError(null);

                    check = true;
                }

            }
        });

    }

    public void toDashboard(View v){
        Intent intent = new Intent(this, Dashboard1.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void toTransactionHistory(View v){
        Intent intent = new Intent(this, TransactionHistoryActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void toUserProfile(View v){
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}