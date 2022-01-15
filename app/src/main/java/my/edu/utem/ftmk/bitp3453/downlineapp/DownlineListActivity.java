package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DownlineListActivity extends AppCompatActivity implements DownlineRVAdapter.ItemClickListener{

    private ImageView btnReturn;
    private TextView txtDownlineList, txtToDashboard, txtToAddAgent,tvEmptyDb;
    private ConstraintLayout layoutListBg;

    // creating variables for our recycler view,
    // array list, adapter, firebase firestore
    // and our progress bar.
    private RecyclerView rvDownlineList;
    private ArrayList<Downline> downlineArrayList;
    private DownlineRVAdapter downlineRVAdapter;
    private FirebaseFirestore db;
    ProgressBar loadingPB;

    private TextView txtID, txtName, txtPhone, txtRegisterDate, txtAddress, txtUplineID, txtStatus;
    private EditText search;

    private ConstraintLayout layoutAgentProfile;

    private Button btBack;

    String id, agentid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downline_list);

        btnReturn = findViewById(R.id.btnRtnBack);

        txtID = findViewById(R.id.txtID);
        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtRegisterDate = findViewById(R.id.txtRegisterDate);
        txtAddress = findViewById(R.id.txtAddress);
        txtUplineID = findViewById(R.id.txtUplineID);
        txtStatus = findViewById(R.id.txtStatus);
        search = findViewById(R.id.search);
        tvEmptyDb = findViewById(R.id.tvEmptyDb);
        btBack = findViewById(R.id.btBack);

        layoutListBg = findViewById(R.id.layoutListBg);
        layoutAgentProfile = findViewById(R.id.layoutAgentProfile);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        // initializing our variables.
        rvDownlineList = findViewById(R.id.rvDownlineList);
        loadingPB = findViewById(R.id.idProgressBar);

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
        db.collection("users").whereEqualTo("upline id", id).get()
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
                            //Toast.makeText(DownlineListActivity.this, "You have no downline yet.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(DownlineListActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;

                float position = layoutAgentProfile.getY();

                layoutAgentProfile.animate().translationY(height).setDuration(500).start();
                layoutAgentProfile.setY(position);
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
                downlineRVAdapter = new DownlineRVAdapter(searchItems,DownlineListActivity.this);
                rvDownlineList.setLayoutManager(new LinearLayoutManager(DownlineListActivity.this, LinearLayoutManager.VERTICAL,false));
                downlineRVAdapter.setClickListener(DownlineListActivity.this);
                rvDownlineList.setAdapter(downlineRVAdapter);

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

        agentid = downlineRVAdapter.getItem(position).getId();
        readUser();

    }

    public void readUser() {

        DocumentReference docRef = db.collection("users").document(agentid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        //Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        txtID.setText(agentid);
                        txtName.setText(document.getData().get("name").toString());
                        txtPhone.setText(document.getData().get("phone").toString());
                        txtAddress.setText(document.getData().get("address").toString());

                        Timestamp timestamp = (Timestamp)document.getData().get("date");
                        Date date = timestamp.toDate();
                        txtRegisterDate.setText(date.toString());

                        txtUplineID.setText(document.getData().get("upline id").toString());
                        txtStatus.setText(document.getData().get("status").toString());

                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        float position = layoutAgentProfile.getY();

        layoutAgentProfile.setY(height);
        layoutAgentProfile.animate().translationY(0).setDuration(500).start();

        layoutAgentProfile.setVisibility(View.VISIBLE);
    }

    public void toDashboard(View v){
        Intent intent = new Intent(this, Dashboard1.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void toAddAgent(View v){
        Intent intent = new Intent(this, AddAgentActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}