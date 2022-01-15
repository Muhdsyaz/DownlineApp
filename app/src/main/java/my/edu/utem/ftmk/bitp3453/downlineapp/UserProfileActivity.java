package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    private TextView  etEditButton, txtID, txtName, txtPhone, txtAddress, txtRegisterDate, txtUplineID, etEditName, etEditPhone, etEditAddress, etPassword, etConfirmPassword;
    private ConstraintLayout layoutUserProfile, layoutEditProfile;
    private Button btnCancel, btnDone;
    private TextView tvLogout;
    private ImageView mvLogoutBtn;
    String id;

    private boolean check = true;

    private double latitude, longitude;
    private Geocoder geocoder;
    List<Address> address;
    GeoPoint geoPoint;

    private static final String TAG = "UserProfileActivity";

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    //Map<String, String> user = new HashMap<>();
    Map<String, Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        tvLogout = findViewById(R.id.tvLogout);
        mvLogoutBtn = findViewById(R.id.mvLogoutBtn);

        etEditButton = findViewById(R.id.tvEditButton);
        txtID = findViewById(R.id.txtID);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtAddress = findViewById(R.id.txtAddress);
        txtRegisterDate = findViewById(R.id.etDLPassword);
        txtUplineID = findViewById(R.id.txtUplineID);

        etEditName = findViewById(R.id.etEditName);
        etEditPhone = findViewById(R.id.etEditPhone);
        etEditAddress = findViewById(R.id.etEditAddress);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        layoutUserProfile = findViewById(R.id.layoutUserProfile);
        layoutEditProfile = findViewById(R.id.layoutEditProfile);

        btnCancel = findViewById(R.id.btnCancel);
        btnDone = findViewById(R.id.btnDone);

        geocoder = new Geocoder(this, Locale.getDefault());

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        readUser();
        checkFieldCondition();

        etEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEditProfile.setVisibility(View.VISIBLE);
                showUserDataToBeUpdated();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEditProfile.setVisibility(View.INVISIBLE);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(etEditName.getText().toString().isEmpty() || etEditPhone.getText().toString().isEmpty() || etEditAddress.getText().toString().isEmpty() ||
                        etPassword.getText().toString().isEmpty() || etConfirmPassword.getText().toString().isEmpty())
                {

                    Toast.makeText(UserProfileActivity.this, "Please make sure all fields are filled.", Toast.LENGTH_SHORT).show();

                }
                else if(check == false)
                {
                    Toast.makeText(UserProfileActivity.this, "Please make sure to fill all the information as required.", Toast.LENGTH_SHORT).show();
                }
                else
                    {
                    EditUserProfile();
                }
            }
        });

        mvLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(UserProfileActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Logout Confirmation");
                dialog.setMessage("Are you sure you want to logout?" );
                dialog.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "Logout".
                        Logout();

                    }
                })
                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".

                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
            }
        });
    }


    public void readUser() {

        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        txtID.setText(id);
                        txtName.setText(document.getData().get("name").toString());
                        txtPhone.setText(document.getData().get("phone").toString());
                        txtAddress.setText(document.getData().get("address").toString());

                        Timestamp timestamp = (Timestamp)document.getData().get("date");
                        Date date = timestamp.toDate();
                        txtRegisterDate.setText(date.toString());
                        //txtRegisterDate.setText(document.getData().get("date").toString());
                        txtUplineID.setText(document.getData().get("upline id").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void showUserDataToBeUpdated(){
        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        etEditName.setText(document.getData().get("name").toString());
                        etEditPhone.setText(document.getData().get("phone").toString());
                        etEditAddress.setText(document.getData().get("address").toString());
                        etPassword.setText(document.getData().get("password").toString());
                        etConfirmPassword.setText(document.getData().get("password").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void EditUserProfile(){


        user.put("name",etEditName.getText().toString());
        user.put("phone",etEditPhone.getText().toString());
        user.put("address",etEditAddress.getText().toString());

        try {

            address = geocoder.getFromLocationName(user.get("address").toString(), 5);

            Address location = address.get(0);

            latitude = location.getLatitude();
            longitude = location.getLongitude();

            geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());

            //Toast.makeText(UserProfileActivity.this, "latitude: " + latitude +", longitude: " + longitude, Toast.LENGTH_SHORT).show();

        } catch (Exception e){
            e.printStackTrace();
        }

        user.put("latlng", geoPoint);

        if(etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {

            user.put("password",etConfirmPassword.getText().toString());

            DocumentReference docRef = db.collection("users").document(id);

            docRef
                    .update("name", user.get("name"), "phone",user.get("phone"),"address",user.get("address"),"password",user.get("password"),"latlng",user.get("latlng"))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

            Toast.makeText(UserProfileActivity.this, "Your profile has been updated.", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(UserProfileActivity.this, UserProfileActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);

            layoutEditProfile.setVisibility(View.INVISIBLE);
        }
        else {
            Toast.makeText(UserProfileActivity.this, "Password is not match.", Toast.LENGTH_SHORT).show();
            showUserDataToBeUpdated();
        }
    }

    public void checkFieldCondition(){

        etEditName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etEditName.getText().toString().isEmpty()) {
                    etEditName.setError("Name cannot be a blank space.");

                    check = false;
                }
                else {
                    etEditName.setError(null);

                    check = true;
                }
            }
        });

        etEditPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etEditPhone.getText().toString().isEmpty()) {
                    etEditPhone.setError("Phone number cannot be a blank space.");

                    check = false;
                }
                else {
                    etEditPhone.setError(null);

                    check = true;
                }
            }
        });

        etEditAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etEditAddress.getText().toString().isEmpty()) {
                    etEditAddress.setError("Address cannot be a blank space.");

                    check = false;
                }
                else {
                    etEditAddress.setError(null);

                    check = true;
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etPassword.getText().toString().isEmpty()) {
                    etPassword.setError("Password cannot be a blank space.");

                    check = false;
                }
                else {
                    etPassword.setError(null);

                    check = true;
                }
            }
        });

        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())){
                    etConfirmPassword.setError("Password is not match.");

                    check = false;
                } else {
                    etConfirmPassword.setError(null);

                    check = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (etConfirmPassword.getText().toString().isEmpty()) {
                    etConfirmPassword.setError("Password cannot be a blank space.");

                    check = false;
                }
                else if(!etConfirmPassword.getText().toString().equals(etPassword.getText().toString())){
                    etConfirmPassword.setError("Password is not match.");

                    check = false;
                }
                else {
                    etConfirmPassword.setError(null);

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

    public void toUpdateTransaction(View v) {
        Intent intent = new Intent(this, UpdateTransactionActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void Logout(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear().commit();

        FirebaseAuth.getInstance().signOut();
        user.delete();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}