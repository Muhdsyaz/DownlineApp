package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddAgentActivity extends AppCompatActivity {

    private static final String TAG = "AddAgentActivity";

    private ImageView btnRtnBack;
    private TextView txtToDownlineList;
    private EditText etName, etPhone, etAddress, etUpId, etPassword;
    private Button btnAddAgent;
    private TextView tvDLId;

    private double latitude, longitude;
    private Geocoder geocoder;
    List<Address> address;
    GeoPoint geoPoint;

    private FirebaseAuth mAuth;

    String DoId;
    String id;
    boolean check = true;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Map<String, Object> user = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_agent);

        btnRtnBack = findViewById(R.id.btnRtnBack);
        txtToDownlineList = findViewById(R.id.txtToDowlineList);

        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etAddress = findViewById(R.id.etAddress);
        etUpId = findViewById(R.id.etUpId);
        etPassword = findViewById(R.id.etDLPassword);
        tvDLId = findViewById(R.id.tvDLId);

        btnAddAgent = findViewById(R.id.btnAddAgent);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        etUpId.setText(id);

        // ...
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        geocoder = new Geocoder(this, Locale.getDefault());

        checkFieldCondition();

        btnAddAgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addAgent();

            }
        });

    }


    public void toDownlineList(View v){
        Intent intent = new Intent(AddAgentActivity.this, DownlineListActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void addAgent(){

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        Random rand = new Random();
        int randomID = rand.nextInt(9999)+1;

        DoId = "T0"+randomID;
        //String[] doId = {};

        String status = "Not active";

        user.put("id", DoId);
        user.put("name",etName.getText().toString());
        user.put("phone",etPhone.getText().toString());
        user.put("address",etAddress.getText().toString());
        user.put("upline id",etUpId.getText().toString());
        //user.put("downline id",FieldValue.arrayUnion(" "));
        user.put("password",etPassword.getText().toString());
        user.put("status",status);
        //user.put("date",date.toString());
        user.put("date",date);

        try {

            address = geocoder.getFromLocationName(user.get("address").toString(), 5);

            Address location = address.get(0);

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());

            Toast.makeText(AddAgentActivity.this, "latitude: " + latitude +", longitude: " + longitude, Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            e.printStackTrace();
        }

        user.put("latlng", geoPoint);

        if(user.get("name").toString().isEmpty() || user.get("phone").toString().isEmpty() || user.get("address").toString().isEmpty() ||
                user.get("password").toString().isEmpty())
        {
            Toast.makeText(AddAgentActivity.this, "Please make sure all fields are filled.", Toast.LENGTH_SHORT).show();
        }
        else if(check == false)
        {
            Toast.makeText(AddAgentActivity.this, "Please make sure to fill your information as required.", Toast.LENGTH_SHORT).show();
        }
        else {
            checkExistingUser();
            tvDLId.setText("Downline ID: " + DoId);
            tvDLId.setVisibility(View.VISIBLE);
        }


    }

    public void checkExistingUser(){

        db.collection("users")
                .whereEqualTo("id", DoId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String user = document.getString("id");

                                if(user.equals(DoId)){
                                    Log.d(TAG, "User Exists");
                                    Toast.makeText(AddAgentActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(AddAgentActivity.this, AdminAddAgent.class);
                                    startActivity(intent);

                                }
                            }
                        }
                        if(task.getResult().size() == 0 ) {
                            Log.d(TAG, "User not Exists");

                            db.collection("users").document(DoId)
                                    .set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(AddAgentActivity.this, "Your member id: " + user.get("id"), Toast.LENGTH_LONG).show();
                                    tvDLId.setText("Downline ID: " + DoId);
                                    tvDLId.setVisibility(View.VISIBLE);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AddAgentActivity.this, "New agent registration failed!", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                });


    }

    public void checkFieldCondition(){

        etName.addTextChangedListener(new TextWatcher()  {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s)  {
                if (etName.getText().toString().isEmpty()) {
                    etName.setError("Please fill in your name.");

                    check = false;
                }else if(!etName.getText().toString().matches("^[a-zA-Z\\s]+$")){
                    etName.setError("Name cannot contain number or symbol.");

                    check = false;

                } else {
                    etName.setError(null);

                    check = true;
                }
            }
        });

        etPhone.addTextChangedListener(new TextWatcher()  {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s)  {
                if (etPhone.getText().toString().isEmpty()) {
                    etPhone.setError("Please fill in your phone number.");

                    check = false;

                }else if(etPhone.getText().toString().length()<10 || etPhone.getText().length()>11) {

                    etPhone.setError("Invalid phone number range.");

                    check = false;

                }else if(!etPhone.getText().toString().substring(0,1).equals("0")) {

                    etPhone.setError("Invalid phone number.");

                    check = false;

                }  else {
                    etPhone.setError(null);

                    check = true;
                }
            }
        });

        etAddress.addTextChangedListener(new TextWatcher()  {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s)  {
                if (etAddress.getText().toString().isEmpty()) {
                    etAddress.setError("Please fill in your address.");

                    check = false;

                } else {
                    etAddress.setError(null);

                    check = true;
                }
            }
        });

        etUpId.addTextChangedListener(new TextWatcher()  {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s)  {
                if (etUpId.getText().toString().isEmpty() || !etUpId.getText().toString().equals(id)) {
                    etUpId.setError("Please do not change the upline id.");

                    check = false;

                } else {
                    etUpId.setError(null);

                    check = true;
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher()  {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s)  {
                if (etPassword.getText().toString().isEmpty()) {
                    etPassword.setError("Please fill in your password.");

                    check = false;
                }
                else if(etPassword.getText().toString().length() < 6){
                    etPassword.setError("Password too short, minimum characters at least 6.");

                    check = false;
                }
                else {
                    etPassword.setError(null);

                    check = true;
                }
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}