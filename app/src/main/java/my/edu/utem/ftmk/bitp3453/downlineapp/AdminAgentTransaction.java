package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminAgentTransaction extends AppCompatActivity {

    private RecyclerView rvTransactionHistory;
    private ArrayList<Transaction> transactionArrayList;
    private TransactionRVAdapter transactionRVAdapter;
    private FirebaseFirestore db;
    ProgressBar loadingPB;

    private TextView tvEmptyDb, tvAgentTransaction;
    private EditText search;

    private ImageView homeImg;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_agent_transaction);

        search = findViewById(R.id.search);
        tvEmptyDb = findViewById(R.id.tvEmptyDb);
        tvAgentTransaction = findViewById(R.id.tvAgentTransaction);

        homeImg = findViewById(R.id.home_icon);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        tvAgentTransaction.setText("Id: " + id);

        // initializing our variables.
        rvTransactionHistory = findViewById(R.id.rvAdminTransactionHistory);
        loadingPB = findViewById(R.id.idProgressBar);

        // initializing our variable for firebase
        // firestore and getting its instance.
        db = FirebaseFirestore.getInstance();

        // creating our new array list
        transactionArrayList = new ArrayList<>();
        rvTransactionHistory.setHasFixedSize(true);
        rvTransactionHistory.setLayoutManager(new LinearLayoutManager(this));

        // adding our array list to our recycler view adapter class.
        transactionRVAdapter = new TransactionRVAdapter(transactionArrayList, this);

        // setting adapter to our recycler view.
        rvTransactionHistory.setAdapter(transactionRVAdapter);

        // below line is use to get the data from Firebase Firestore.
        // previously we were saving data on a reference of Courses
        // now we will be getting the data from the same reference.
        db.collection("transactions").whereEqualTo("id", id).orderBy("transactiondate", Query.Direction.DESCENDING).get()
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
                                Transaction c = d.toObject(Transaction.class);

                                // and we will pass this object class
                                // inside our arraylist which we have
                                // created for recycler view.
                                transactionArrayList.add(c);
                            }
                            // after adding the data to recycler view.
                            // we are calling recycler view notifuDataSetChanged
                            // method to notify that data has been changed in recycler view.
                            transactionRVAdapter.notifyDataSetChanged();
                        } else {
                            // if the snapshot is empty we are displaying a toast message.
                            loadingPB.setVisibility(View.GONE);
                            tvEmptyDb.setVisibility(View.VISIBLE);
                            //Toast.makeText(TransactionHistoryActivity.this, "You have not made any transactions yet.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // if we do not get any data or any error we are displaying
                // a toast message that we do not get any data
                Toast.makeText(AdminAgentTransaction.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
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

                ArrayList<Transaction> searchItems = new ArrayList<>();
                for(Transaction documentSnapshot : transactionArrayList){
                    if(documentSnapshot.getCustomername().toString().toLowerCase().contains(s.toString().toLowerCase())){
                        searchItems.add(documentSnapshot);
                    }
                    else if(documentSnapshot.getItemcode().toString().toLowerCase().contains(s.toString().toLowerCase())){
                        searchItems.add(documentSnapshot);
                    }
                }
                transactionRVAdapter = new TransactionRVAdapter(searchItems,AdminAgentTransaction.this);
                rvTransactionHistory.setLayoutManager(new LinearLayoutManager(AdminAgentTransaction.this, LinearLayoutManager.VERTICAL,false));
                rvTransactionHistory.setAdapter(transactionRVAdapter);

            }
        });

    }

    public void toAdminDownline(View v){

        Intent intent = new Intent(this, AdminDownlineActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}