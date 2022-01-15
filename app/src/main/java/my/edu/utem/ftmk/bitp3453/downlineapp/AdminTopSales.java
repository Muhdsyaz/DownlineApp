package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AdminTopSales extends AppCompatActivity {

    private static final String TAG = "AdminTopSales";
    private TextView tvID, tvTotalSales, tvTotalProfit, tvTotalQuantity, tvTotalTransaction, tvEmptyDb;

    private String id;
    private double sales, profit;
    private double totalsales, totalprofit;
    private int quantity = 0, salesTrans = 0, totalQuantity;

    private RecyclerView rvTodayTransaction;
    private ArrayList<Transaction> transactionArrayList;
    private TransactionRVAdapter transactionRVAdapter;

    private LinearLayout layoutTopSales;

    private Button btSendNotification;

    private PieChart pieChart;

    float pcProfit = 0, pcTotalProfit, pcTotalSales, pcSales = 0;
    private final int REQUEST_READ_PHONE_STATE=1;

    Date currentTime = Calendar.getInstance().getTime();

    ProgressBar loadingPB;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_top_sales);

        tvID = findViewById(R.id.tvID);
        tvTotalSales = findViewById(R.id.tvTotalSales);
        tvTotalProfit = findViewById(R.id.tvTotalProfit);
        tvTotalQuantity = findViewById(R.id.tvTotalQuantity);
        tvTotalTransaction = findViewById(R.id.tvTotalTransaction);
        tvEmptyDb = findViewById(R.id.tvEmptyDb);

        btSendNotification = findViewById(R.id.btSendNotification);

        layoutTopSales = findViewById(R.id.layoutTopSales);

        pieChart = findViewById(R.id.activity_main_piechart);

        db = FirebaseFirestore.getInstance();

        FirebaseMessaging.getInstance().subscribeToTopic("all");

        Intent intent = getIntent();

        id = intent.getStringExtra("id");

        Log.e(TAG, "Check id: " + id);

        if(!id.equals(" ")) {

            displayDailyTransaction();

            // initializing our variables.

            //tvEmptyDb = findViewById(R.id.tvEmptyDb);

            rvTodayTransaction = findViewById(R.id.rvTodayTransaction);
            loadingPB = findViewById(R.id.idProgressBar);

            // initializing our variable for firebase
            // firestore and getting its instance.
            db = FirebaseFirestore.getInstance();

            // creating our new array list
            transactionArrayList = new ArrayList<>();
            rvTodayTransaction.setHasFixedSize(true);
            rvTodayTransaction.setLayoutManager(new LinearLayoutManager(this));

            // adding our array list to our recycler view adapter class.
            transactionRVAdapter = new TransactionRVAdapter(transactionArrayList, this);

            // setting adapter to our recycler view.
            rvTodayTransaction.setAdapter(transactionRVAdapter);

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

                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                    Date today = new Date();
                                    String todayDate = (formatter.format(today)).substring(0, 10);

                                    Date date = ((Timestamp) d.getData().get("transactiondate")).toDate();
                                    String trans = formatter.format(date);
                                    String transdate = trans.substring(0, 10);

                                    if (todayDate.equals(transdate)) {

                                        // after getting this list we are passing
                                        // that list to our object class.
                                        Transaction c = d.toObject(Transaction.class);

                                        // and we will pass this object class
                                        // inside our arraylist which we have
                                        // created for recycler view.
                                        transactionArrayList.add(c);
                                    }
                                }
                                // after adding the data to recycler view.
                                // we are calling recycler view notifuDataSetChanged
                                // method to notify that data has been changed in recycler view.
                                transactionRVAdapter.notifyDataSetChanged();
                            } else {
                                // if the snapshot is empty we are displaying a toast message.
                                loadingPB.setVisibility(View.GONE);
                                //Toast.makeText(TransactionHistoryActivity.this, "You have not made any transactions yet.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // if we do not get any data or any error we are displaying
                    // a toast message that we do not get any data
                    Toast.makeText(AdminTopSales.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
                }
            });

            setupPieChart();
            loadPieChartData();

            btSendNotification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendNotification();

                    // Check condition
                    int permissionCheck = ContextCompat.checkSelfPermission(AdminTopSales.this, Manifest.permission.READ_PHONE_STATE);

                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(AdminTopSales.this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
                        // When permission is granted
                        // Created method
                        sendMessage();
                    }
                    else{
                        // When permission is not granted
                        // Request permission
                        ActivityCompat.requestPermissions(AdminTopSales.this
                                ,new String[]{Manifest.permission.SEND_SMS}
                                ,100);
                    }

//                    if (ContextCompat.checkSelfPermission(AdminTopSales.this,Manifest.permission.SEND_SMS) ==
//                            PackageManager.PERMISSION_GRANTED){
//                        // When permission is granted
//                        // Created method
//                        sendMessage();
//                    }
//                    else{
//                        // When permission is not granted
//                        // Request permission
//                        ActivityCompat.requestPermissions(AdminTopSales.this
//                                ,new String[]{Manifest.permission.SEND_SMS}
//                                ,100);
//                    }

                }
            });

        }
        else{

            layoutTopSales.setVisibility(View.INVISIBLE);
            tvEmptyDb.setVisibility(View.VISIBLE);

        }

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
                                //Log.d(TAG, document.getId() + " => " + document.getData());

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
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        tvID.setText("ID: " + id);
                        tvTotalSales.setText("Total Sales: RM" + String.format("%.2f",totalsales));
                        tvTotalProfit.setText("Total Profit: RM" + String.format("%.2f",totalprofit));
                        tvTotalQuantity.setText("Total Quantity: " + String.valueOf(totalQuantity));
                        tvTotalTransaction.setText("Total Transaction: " + String.valueOf(salesTrans));


                    }
                });
    }

    public void setupPieChart() {
        pieChart.setDrawHoleEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
//        pieChart.setCenterText("Spending by Category");
//        pieChart.setCenterTextSize(12);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextColor(Color.WHITE);
        l.setTextSize(9);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();

        db.collection("transactions")
                .whereEqualTo("id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date today = new Date();
                                String todayDate = (formatter.format(today)).substring(0,10);

                                Date date = ((Timestamp)document.getData().get("transactiondate")).toDate();
                                String trans = formatter.format(date);
                                String transdate = trans.substring(0,10);

                                if(todayDate.equals(transdate)) {
                                    pcProfit = Float.parseFloat(document.getData().get("profit").toString());
                                    pcTotalProfit += pcProfit;

                                    pcSales = Float.parseFloat(document.getData().get("sales").toString());
                                    pcTotalSales += pcSales;

                                    Log.e(TAG, "loadPieChartData: Profit: " + pcTotalProfit + ", Sales: " + pcTotalSales);
                                }
                            }

                            entries.add(new PieEntry(pcTotalProfit, "Profit"));
                            entries.add(new PieEntry(pcTotalSales, "Sales"));

                            ArrayList<Integer> colors = new ArrayList<>();
                            for (int color: ColorTemplate.MATERIAL_COLORS) {
                                colors.add(color);
                            }

                            for (int color: ColorTemplate.VORDIPLOM_COLORS) {
                                colors.add(color);
                            }

                            PieDataSet dataSet = new PieDataSet(entries, "Transaction Category");
                            dataSet.setColors(colors);

                            PieData data = new PieData(dataSet);
                            data.setDrawValues(true);
                            data.setValueFormatter(new PercentFormatter(pieChart));
                            data.setValueTextSize(9);
                            data.setValueTextColor(Color.BLACK);

                            pieChart.setData(data);
                            pieChart.invalidate();

                            pieChart.animateY(1400, Easing.EaseInOutQuad);

                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void sendNotification(){


        Log.e(TAG, "Max id: " + id );

        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        String name = document.getData().get("name").toString();

//                        if(currentTime.getHours() == 23 && currentTime.getMinutes() < 10 ){

                            Log.e(TAG, "onCreate: " + currentTime.getHours() + " " + currentTime.getMinutes());

                            FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender("/topics/all","Notification",
                                    "Congratulations to " + name + ", " + id + "\nfor being the top sales agent today.\nKeep it up.",
                                    getApplicationContext(), AdminTopSales.this);

                            fcmNotificationsSender.SendNotifications();

                            Toast.makeText(getApplicationContext(), "Your notification is sent.", Toast.LENGTH_SHORT).show();


//                        }


                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    private void sendMessage() {

        DocumentReference docRef = db.collection("users").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        String name = document.getData().get("name").toString();
                        String phone = "+6" + document.getData().get("phone").toString();

                        String message = "Congratulations to " + name + ", " + id + "\nfor being the top sales agent today.\nKeep it up.";
                        Log.e(TAG, "Phone number: " + phone + "\tMessage: " + message);

                        // Check permission
                        if (!phone.equals("") && !message.equals("")){
                            // When both edit text value not equal to blank
                            // Initialize sms manager
                            SmsManager smsManager = SmsManager.getDefault();
                            // Send text message
                            smsManager.sendTextMessage(phone,null,message,null,null);

                            // Display toast
                            Toast.makeText(getApplicationContext(),"SMS sent successfully",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            // When edit text value is blank
                            // Display toast
                            Toast.makeText(getApplicationContext(),"There is not phone number or message to be sent.",Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Check condition
        if(requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            // When permission is granted
            // Call method
            sendMessage();
        }
        else{
            // When permission is denied
            // Display toast
            Toast.makeText(getApplicationContext(),"Permission Denied!", Toast.LENGTH_SHORT).show();
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case REQUEST_READ_PHONE_STATE:
//                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                    sendMessage();
//                }
//                break;
//
//            default:
//                break;
//        }
//    }

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