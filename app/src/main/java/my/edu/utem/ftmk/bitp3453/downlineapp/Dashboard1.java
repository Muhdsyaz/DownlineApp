package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Dashboard1 extends AppCompatActivity {

    private static final String TAG = "Dashboard1";
    private ImageView homeIcon, updateTransIcon, transHistoryIcon, userProfileIcon;
    private TextView tvName, tvId, tvTotalSales, tvTotalProfit, tvTotalQuantity, tvTotalTransaction;
    private String id, today;
    private double sales, profit;
    private double totalsales, totalprofit;
    private int quantity = 0, salesTrans = 0, totalQuantity;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard1);

        tvName = findViewById(R.id.tvName);
        tvId = findViewById(R.id.tvId);
        tvTotalSales = findViewById(R.id.tvTotalSales);
        tvTotalProfit = findViewById(R.id.tvTotalProfit);
        //tvTotalQuantity = findViewById(R.id.tvTotalQuantity);
        tvTotalTransaction = findViewById(R.id.tvTotalTransaction);

        db = FirebaseFirestore.getInstance();

        Intent intent = getIntent();

        id = intent.getStringExtra("id");

        tvId.setText(id);

        displayName();
        displayDailyTransaction();

    }

    public void displayName() {

        DocumentReference docRef = db.collection("users").document(id);
        Log.e("Test id: ",id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        tvName.setText(document.getData().get("name").toString());

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void displayDailyTransaction() {

        db.collection("transactions")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date today = new Date();
                                String todayDate = (formatter.format(today)).substring(0,10);

                                Date date = ((Timestamp)document.getData().get("transactiondate")).toDate();
                                String trans = formatter.format(date);
                                String transdate = trans.substring(0,10);

                                if(todayDate.equals(transdate)){

                                //sales = Integer.parseInt(document.getData().get("sales").toString());
                                sales = Double.parseDouble(document.getData().get("sales").toString());
                                totalsales += sales;

                                profit = Double.parseDouble(document.getData().get("profit").toString());
                                totalprofit += profit;

                                quantity = Integer.parseInt(document.getData().get("quantity").toString());
                                totalQuantity += quantity;

                                salesTrans += 1;
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        tvTotalSales.setText("" + String.format("%.2f",totalsales));
                        tvTotalProfit.setText("RM" + String.format("%.2f",totalprofit));
//                        tvTotalQuantity.setText(String.valueOf(totalQuantity));
                        tvTotalTransaction.setText(String.valueOf(salesTrans));


                    }
                });


    }


    public void toMap(View v){
        Intent intent = new Intent(this, GoogleMapActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void toDownlineList(View view){
        Intent intent = new Intent(this, DownlineListActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void toTransactionHistory(View v){
        Intent intent = new Intent(this, TransactionHistoryActivity.class);
        intent.putExtra("id",id);
        startActivity(intent);
    }

    public void toUpdateTransaction(View v){
        Intent intent = new Intent(this, UpdateTransactionActivity.class);
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