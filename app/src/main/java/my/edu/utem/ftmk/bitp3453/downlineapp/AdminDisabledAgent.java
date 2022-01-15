package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.List;

public class AdminDisabledAgent extends AppCompatActivity implements DownlineRVAdapter.ItemClickListener{

    private static final String TAG = "AdminDisabledAgent";

    // creating variables for our recycler view,
    // array list, adapter, firebase firestore
    // and our progress bar.
    private RecyclerView rvRequest;
    private ArrayList<Downline> downlineArrayList;
    private DownlineRVAdapter downlineRVAdapter;
    private LinearLayout layoutConfirmRequest, layoutfake;
    private TextView tvSetId, tvSetName, tvEmptyDb;
    private MaterialIconView closeIcon;
    private Button btEnable, btDelete;

    private FirebaseFirestore db;
    ProgressBar loadingPB;

    String id, name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_disabled_agent);

        // initializing our variables.
        rvRequest = findViewById(R.id.rvRequest);
        loadingPB = findViewById(R.id.idProgressBar);
        layoutConfirmRequest = findViewById(R.id.layoutConfirmRequest);

        tvSetId = findViewById(R.id.tvSetId);
        tvSetName = findViewById(R.id.tvSetName);
        layoutfake = findViewById(R.id.layoutfake);
        closeIcon = findViewById(R.id.closeIcon);
        btEnable = findViewById(R.id.btEnable);
        btDelete = findViewById(R.id.btDelete);
        tvEmptyDb = findViewById(R.id.tvEmptyDb);


        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // creating our new array list
        downlineArrayList = new ArrayList<>();
        rvRequest.setHasFixedSize(true);
        rvRequest.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        downlineRVAdapter = new DownlineRVAdapter(downlineArrayList, this);

        downlineRVAdapter.setClickListener(this);

        // setting adapter to our recycler view.
        rvRequest.setAdapter(downlineRVAdapter);

        // below line is use to get the data from Firebase Firestore.
        // previously we were saving data on a reference of Courses
        // now we will be getting the data from the same reference.
        db.collection("users").whereEqualTo("status", "Disabled").get()
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
                            //Toast.makeText(RequestActivity.this, "There is no pending request right now.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(AdminDisabledAgent.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        });

        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutfake.setVisibility(View.INVISIBLE);
                layoutConfirmRequest.setVisibility(View.INVISIBLE);
            }
        });


        btEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                updateStatus();
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(AdminDisabledAgent.this);
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
                            }
                        });

                final AlertDialog alert = dialog.create();
                alert.show();

                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(android.R.color.holo_red_light));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));

            }
        });
    }

    @Override
    public void onItemClick(View view, int position)
    {
        id = downlineRVAdapter.getItem(position).getId();
        name = downlineRVAdapter.getItem(position).getName();

        tvSetId.setText("ID: " + id);
        tvSetName.setText("Name: " + name);

        layoutConfirmRequest.setVisibility(View.VISIBLE);
        layoutfake.setVisibility(View.VISIBLE);
    }

    public void updateStatus(){
        DocumentReference docRef = db.collection("users").document(id);

        docRef
                .update("status","Active")
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

        Toast.makeText(AdminDisabledAgent.this, "Account has been enabled.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AdminDisabledAgent.class);
        startActivity(intent);

        layoutConfirmRequest.setVisibility(View.INVISIBLE);
        layoutfake.setVisibility(View.INVISIBLE);
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

        Toast.makeText(AdminDisabledAgent.this, "The request has been rejected.", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, AdminDisabledAgent.class);
        startActivity(intent);

        layoutConfirmRequest.setVisibility(View.INVISIBLE);
        layoutfake.setVisibility(View.INVISIBLE);

    }

    public void toAdminDashboard(View v){
        Intent intent = new Intent(this, AdminDashboardActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }
}