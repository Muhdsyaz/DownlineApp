package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDownlineActivity extends AppCompatActivity implements DownlineRVAdapter.ItemClickListener{

    // creating variables for our recycler view,
    // array list, adapter, firebase firestore
    // and our progress bar.
    private RecyclerView rvDownlineList;
    private ArrayList<Downline> downlineArrayList;
    private DownlineRVAdapter downlineRVAdapter;
    private FirebaseFirestore db;
    ProgressBar loadingPB;

    private final static String TAG = "AdminDownlineActivty";

    String id, status;

    private EditText search;

    private TextView txtID, txtName, txtPhone, txtRegisterDate, txtAddress, txtUplineID, txtStatus, tvDeleteButton, tvEmptyDb, tvViewAgentTransaction;
    private ConstraintLayout layoutUserProfile;
    private Button btBack, btDisable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_downline);

        txtID = findViewById(R.id.txtID);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtRegisterDate = findViewById(R.id.txtRegisterDate);
        txtAddress = findViewById(R.id.txtAddress);
        txtUplineID = findViewById(R.id.txtUplineID);
        txtStatus = findViewById(R.id.txtStatus);
        tvDeleteButton = findViewById(R.id.tvDeleteButton);
        tvEmptyDb = findViewById(R.id.tvEmptyDb);
        tvViewAgentTransaction = findViewById(R.id.tvViewAgentTransaction);

        search = findViewById(R.id.search);

        layoutUserProfile = findViewById(R.id.layoutUserProfile);

        btBack = findViewById(R.id.btBack);
        btDisable = findViewById(R.id.btDisable);

        // initializing our variables.
        rvDownlineList = findViewById(R.id.rvDownlineAdmin);
        loadingPB = findViewById(R.id.idProgressBar);

        rvDownlineList.removeAllViewsInLayout();

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // creating our new array list
        downlineArrayList = new ArrayList<>();
        rvDownlineList.setHasFixedSize(true);
        rvDownlineList.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        downlineRVAdapter = new DownlineRVAdapter(downlineArrayList, this);

        downlineRVAdapter.setClickListener(this);

        // setting adapter to our recycler view.
        rvDownlineList.setAdapter(downlineRVAdapter);

        // below line is use to get the data from Firebase Firestore.
        // previously we were saving data on a reference of Courses
        // now we will be getting the data from the same reference.
        db.collection("users").whereEqualTo("status","Active").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // after getting the data we are calling on success method
                        // and inside this method we are checking if the received
                        // query snapshot is empty or not.
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // if the snapshot is not empty we are
                            // hiding our progress bar and adding
                            // our data in a list.
                            loadingPB.setVisibility(View.GONE);
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                // after getting this list we are passing
                                // that list to our object class.
                                Downline c = d.toObject(Downline.class);

                                // and we will pass this object class
                                // inside our arraylist which we have
                                // created for recycler view.
                                downlineArrayList.add(c);
                            }
                            // after adding the data to recycler view.
                            // we are calling recycler view notifuDataSetChanged
                            // method to notify that data has been changed in recycler view.
                            downlineRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                            tvEmptyDb.setVisibility(View.VISIBLE);
                            //Toast.makeText(AdminDownlineActivity.this, "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(AdminDownlineActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;

                float position = layoutUserProfile.getY();

                layoutUserProfile.animate().translationY(height).setDuration(500).start();
                layoutUserProfile.setY(position);
            }
        });

        btDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(AdminDownlineActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Disable User");
                dialog.setMessage("Are you sure you want to disable this user?" );
                dialog.setPositiveButton("Disable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "Delete".
                        disableAccount();
                    }
                })
                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".
                                layoutUserProfile.setVisibility(View.VISIBLE);
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            }
        });

        tvDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(AdminDownlineActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Delete User");
                dialog.setMessage("Are you sure you want to delete this user?" );
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "Delete".
                        deleteUser();
                    }
                })
                        .setNegativeButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //Action for "Cancel".
                                layoutUserProfile.setVisibility(View.VISIBLE);
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            }
        });

        // to filter recyclerview
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                ArrayList<Downline> searchItems = new ArrayList<>();
                for(Downline documentSnapshot : downlineArrayList){
                    if(documentSnapshot.getId().toString().toLowerCase().contains(s.toString().toLowerCase())){
                        searchItems.add(documentSnapshot);
                    }
                    else if(documentSnapshot.getName().toString().toLowerCase().contains(s.toString().toLowerCase())){
                        searchItems.add(documentSnapshot);
                    }
                }
                downlineRVAdapter = new DownlineRVAdapter(searchItems,AdminDownlineActivity.this);
                rvDownlineList.setLayoutManager(new LinearLayoutManager(AdminDownlineActivity.this, LinearLayoutManager.VERTICAL,false));
                downlineRVAdapter.setClickListener(AdminDownlineActivity.this);
                rvDownlineList.setAdapter(downlineRVAdapter);

            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {

        id = downlineRVAdapter.getItem(position).getId();
        status = downlineRVAdapter.getItem(position).getStatus();
        readUser();

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
                        txtUplineID.setText(document.getData().get("upline id").toString());
                        txtStatus.setText(document.getData().get("status").toString());

                        //TextView tvViewAgentTransaction = (TextView) view.findViewById(R.id.tvViewAgentTransaction);
                        SpannableString content = new SpannableString("View Agent Transaction");
                        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                        tvViewAgentTransaction.setText(content);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        float position = layoutUserProfile.getY();

        layoutUserProfile.setY(height);
        layoutUserProfile.animate().translationY(0).setDuration(500).start();

        layoutUserProfile.setVisibility(View.VISIBLE);
    }

    public void disableAccount(){
        DocumentReference docRef = db.collection("users").document(id);

        docRef
                .update("status","Disabled")
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

        Toast.makeText(AdminDownlineActivity.this, "Account has been disabled.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AdminDownlineActivity.class);
        startActivity(intent);

    }

    public void deleteUser(){
        db.collection("users").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

        Toast.makeText(AdminDownlineActivity.this, "The account has been deleted.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AdminDownlineActivity.class);
        startActivity(intent);

    }

    public void toAgentTransaction(View v){

        Intent intent = new Intent(this, AdminAgentTransaction.class);
        intent.putExtra("id",id);
        Log.e(TAG, "toAgentTransaction: " + id);
        startActivity(intent);

    }


    public void toAdminDashboard(View v){
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
    }

    public void toAdminAddAgent(View v){
        Intent intent = new Intent(this, AdminAddAgent.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}