package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class AdminDashboardActivity extends AppCompatActivity {

    private static final String TAG = "AdminDashboardActivity";
    private TextView tvTotalSalesAll, tvTotalProfitAll, tvTotalQuantityAll, tvTotalTransactionAll, tvAgentRequest, tvDoId, tvBcDate;
    private MaterialIconView icon;
    private ScrollView svScroll;
    private ConstraintLayout layoutTopBar;

    private BarChart bcBarChart;

    private double sales, totalsales, profit, totalprofit;
    private int quantity = 0, salesTrans = 0, totalQuantity;
    private int request, y = 0;
    private double max = 0;

    String maxPerson = " ";

    FirebaseFirestore db;
    QuerySnapshot objectTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tvTotalSalesAll = findViewById(R.id.tvTotalSalesAll);
        tvTotalProfitAll = findViewById(R.id.tvTotalProfitAll);
//        tvTotalQuantityAll = findViewById(R.id.tvTotalQuantityAll);
        tvTotalTransactionAll = findViewById(R.id.tvTotalTransactionAll);
        tvAgentRequest = findViewById(R.id.tvAgentRequest);
        tvDoId = findViewById(R.id.tvDoId);
        tvBcDate = findViewById(R.id.tvBcDate);
        layoutTopBar = findViewById(R.id.layoutTopBar);

        svScroll = findViewById(R.id.svScroll);

        bcBarChart = findViewById(R.id.bcBarChart);

        icon = findViewById(R.id.icon);

        db = FirebaseFirestore.getInstance();

        displayRequest();
        //displayDailyTransaction();
        realTimeData();

        initializeBarChart();
        calculateTotalSale();


//        svScroll.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged() {
//                int scrollY = svScroll.getScrollY(); // For ScrollView
//
//                if(y < scrollY){
//                    if(layoutTopBar.getY() > -200){
//                        layoutTopBar.setY(layoutTopBar.getY()-5);
//                    }
//                    y = scrollY;
//                }
//                else{
//                    if(layoutTopBar.getY() < 0){
//                        layoutTopBar.setY(layoutTopBar.getY()+5);
//                    }
//                    y = scrollY;
//                }
//                if(scrollY == 0){
//                    layoutTopBar.setY(0);
//                    y = scrollY;
//                }
//            }
//        });

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(AdminDashboardActivity.this);
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

    public void displayDailyTransaction() {

        db.collection("transactions")
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

                        tvTotalSalesAll.setText("" + String.format("%.2f",totalsales));
                        tvTotalProfitAll.setText("RM " + String.format("%.2f",totalprofit));
//                        tvTotalQuantityAll.setText(String.valueOf(totalQuantity));
                        tvTotalTransactionAll.setText(String.valueOf(salesTrans));


                    }
                });
    }

    public void displayRequest(){

        db.collection("users")
                .whereEqualTo("status", "Not active")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                                request += 1;
                            }
                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                        tvAgentRequest.setText("You have " + request +" account registration request.");
                    }
                });

    }

    public void calculateTotalSale(){

        ArrayList value = new ArrayList();
        ArrayList xAxis = new ArrayList();

        ArrayList<String> severityStringList = new ArrayList<>();

        Map<String, Map<String,Object>> user = new HashMap<>();

        db.collection("transactions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {


                            for (QueryDocumentSnapshot document : task.getResult()) {

                                List<String> id = new ArrayList<>(user.keySet());

                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date today = new Date();
                                String todayDate = (formatter.format(today)).substring(0,10);

                                Date date = ((Timestamp)document.getData().get("transactiondate")).toDate();
                                String trans = formatter.format(date);
                                String transdate = trans.substring(0,10);

                                if(todayDate.equals(transdate)) {

                                    if (id.contains(document.getData().get("id"))) {


                                        double sales = Double.parseDouble(user.get(document.getData().get("id").toString()).get("sales").toString());
                                        sales += Double.parseDouble(document.getData().get("sales").toString());

                                        user.get(document.getData().get("id").toString()).put("sales", sales);

                                    } else {

                                        Map<String, Object> sale = new HashMap<>();
                                        sale.put("sales", Double.parseDouble(document.getData().get("sales").toString()));
                                        user.put(document.getData().get("id").toString(), sale);

                                    }
                                }
                            }

                            List<String> agent = new ArrayList<>(user.keySet());

                            //double max = 0;
                            int i = 0;

                            for (String id : agent) {

                                BarEntry barEntry = new BarEntry(i++, Float.parseFloat(user.get(id).get("sales").toString()));

                                max = Math.max(max, Double.parseDouble(user.get(id).get("sales").toString()));
                                if (max == Double.parseDouble(user.get(id).get("sales").toString())) {

                                    maxPerson = id;

                                }

                                xAxis.add(id);

                            }

                            if(!maxPerson.equals(" ")) {

                                DocumentReference docRef = db.collection("users").document(maxPerson);
                                Log.e("Test id: ",maxPerson);
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();

                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                tvDoId.setText(document.getData().get("name").toString() + "\n" + maxPerson + "\n" +
                                                        " (RM " + String.format("%.2f", max) + ")");

                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });

                            } else {
                                tvDoId.setText("\nNone\n");
                            }

                            db.collection("transactions").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                            objectTransaction = task.getResult();

                                            LocalDate currentdate = LocalDate.now();
                                            Month currentMonth = currentdate.getMonth();
                                            
                                            int dayOfMonth = currentMonth.length(true);

                                            for(int j = 1; j <= dayOfMonth; j++){

                                                String date = j + "/" + currentMonth.getValue() + "/" + currentdate.getYear();


                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                                Date parsedDate = null;

                                                try {
                                                    parsedDate = dateFormat.parse(date);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                                Timestamp timestamp = new Timestamp(parsedDate);

                                                float sales = 0;

                                                for(DocumentSnapshot documentSnapshot : objectTransaction){

                                                    Timestamp timestampDB = (Timestamp)documentSnapshot.getData().get("transactiondate");
                                                    Date dateFromDB = timestampDB.toDate();

                                                    LocalDate localDate = dateFromDB.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                                    int year  = localDate.getYear();
                                                    int month = localDate.getMonthValue();
                                                    int day = localDate.getDayOfMonth();

                                                    LocalDate localDate1 = parsedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                                    int year1  = localDate1.getYear();
                                                    int month1 = localDate1.getMonthValue();
                                                    int day1 = localDate1.getDayOfMonth();

                                                    if(j == day){

                                                        if(month1 == month){

                                                            if(year1 == year){

                                                                sales += Float.parseFloat(documentSnapshot.getData().get("sales").toString());

                                                            }

                                                        }
                                                    }
                                                }

                                                severityStringList.add(String.valueOf(j));

                                                BarEntry barEntry = new BarEntry(j, sales);
                                                value.add(barEntry);

                                            }

                                            BarDataSet barDataSet = new BarDataSet(value, "Total sales");

                                            ArrayList<IBarDataSet> dataset = new ArrayList();
                                            dataset.add((IBarDataSet) barDataSet);
                                            BarData barData = new BarData(dataset);



                                            tvBcDate.setText(LocalDate.now().getMonth().toString() + " " + LocalDate.now().getYear());

                                            bcBarChart.setData(barData);

                                            bcBarChart.setVisibleXRangeMaximum(5);

                                            XAxis xAxis = bcBarChart.getXAxis();
                                            xAxis.setGranularity(1f);
                                            xAxis.setGranularityEnabled(true);

                                            bcBarChart.invalidate();
                                        }
                                    });

                        } else {
                            //Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void initializeBarChart() {
        bcBarChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        //bcBarChart.setMaxVisibleValueCount(4);
        bcBarChart.getXAxis().setDrawGridLines(false);
        // scaling can now only be done on x- and y-axis separately
        bcBarChart.setPinchZoom(false);

        bcBarChart.setDrawBarShadow(false);
        bcBarChart.setDrawGridBackground(false);

        XAxis xAxis = bcBarChart.getXAxis();
        xAxis.setDrawGridLines(false);

        bcBarChart.getAxisLeft().setDrawGridLines(false);
        bcBarChart.getAxisRight().setDrawGridLines(false);
        bcBarChart.getAxisRight().setEnabled(false);
        bcBarChart.getAxisLeft().setEnabled(true);
        bcBarChart.getXAxis().setDrawGridLines(false);
        // add a nice and smooth animation
        bcBarChart.animateY(1500);

        bcBarChart.getLegend().setEnabled(true);

        bcBarChart.getAxisRight().setDrawLabels(true);
        bcBarChart.getAxisLeft().setDrawLabels(true);
        bcBarChart.setTouchEnabled(true);
        bcBarChart.setDoubleTapToZoomEnabled(false);
        bcBarChart.getXAxis().setEnabled(true);
        bcBarChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        bcBarChart.invalidate();

    }

    public void realTimeData(){

        db.collection("transactions").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                totalsales = 0;
                totalprofit = 0;
                totalQuantity = 0;
                salesTrans = 0;

                    for (DocumentSnapshot document : value) {
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

                tvTotalSalesAll.setText("" + String.format("%.2f",totalsales));
                tvTotalProfitAll.setText("RM " + String.format("%.2f",totalprofit));
//                tvTotalQuantityAll.setText(String.valueOf(totalQuantity));
                tvTotalTransactionAll.setText(String.valueOf(salesTrans));

            }
        });
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

    public void toRequest(View v){
        Intent intent = new Intent(this, RequestActivity.class);
        startActivity(intent);
    }

    public void toTopSales(View v){
        Intent intent = new Intent(this, AdminTopSales.class);
        intent.putExtra("id", maxPerson);
        startActivity(intent);
    }

    public void toAdminDownline(View v){
        Intent intent = new Intent(this, AdminDownlineActivity.class);
        startActivity(intent);
    }

    public void toDisabledAgent(View v){
        Intent intent = new Intent(this, AdminDisabledAgent.class);
        startActivity(intent);
    }

    public void toAdminTransaction(View v){
        Intent intent = new Intent(this, AdminTransactionHistory.class);
        startActivity(intent);
    }

    public void toCustomerDistribution(View v){
        Intent intent = new Intent(this, AdminCustomerDistribution.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true);
    }

}
