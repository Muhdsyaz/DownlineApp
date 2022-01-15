package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText etID, etPassword;
    private Button btnLogin;

    private FirebaseAuth mAuth;

    //private static final String TAG = "MainActivity";

    boolean ada = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Map<String, String> user = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etID = findViewById(R.id.etID);
       etPassword = findViewById(R.id.etDLPassword);
       btnLogin = findViewById(R.id.btnLogin);

        mAuth = FirebaseAuth.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Login();

            }
        });
    }

    public void Login(){

        ada = false;

        // read data from here
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Log.d(TAG, document.getId() + " => " + document.getData());

                                Map<String, Object> user1 = document.getData();

                                if(user1.get("id").toString().equals(etID.getText().toString())  &&
                                        user1.get("password").toString().equals(etPassword.getText().toString())){

                                    String id = etID.getText().toString();
                                    String sub = id.substring(0,2);

                                    mAuth.signInAnonymously();
                                    SharedPreferences.Editor editor = getSharedPreferences("UserPreferences", MODE_PRIVATE).edit();
                                    editor.putString("id", id);
                                    editor.apply();


                                    if(sub.equals("T0") && user1.get("status").toString().equals("Active")){

                                        Intent intent = new Intent(MainActivity.this, Dashboard1.class);
                                        intent.putExtra("id", id);
                                        startActivity(intent);


                                        ada = true;

                                        break;

                                    }
                                    else if(sub.equals("AD")) {
                                        Intent intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
                                        intent.putExtra("id", id);
                                        startActivity(intent);

                                        ada = true;

                                        break;

                                    }
                                    else{
                                        Toast.makeText(MainActivity.this, "Invalid id or password", Toast.LENGTH_SHORT).show();
                                        ada = false;

                                        break;
                                    }
                                }
                            }

                            if(!ada){
                                Toast.makeText(MainActivity.this, "Invalid id or password", Toast.LENGTH_SHORT).show();
                                ada = false;
                            }

                        } else {
                            //Log.w(TAG, "Error getting documents.", task.getException());
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