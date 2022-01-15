package my.edu.utem.ftmk.bitp3453.downlineapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class IntroActivity extends AppCompatActivity {

    ImageView splashImg;
    LottieAnimationView lottieAnimationView;
    Intent intent;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        splashImg = findViewById(R.id.splashImg);
        lottieAnimationView = findViewById(R.id.lottie);

        splashImg.animate().translationY(-1600).setDuration(1000).setStartDelay(2000);
        lottieAnimationView.animate().translationY(1600).setDuration(1000).setStartDelay(2000);

        intent = new Intent(this, MainActivity.class);

        Thread thread = new Thread(){
            public void run(){

                try{
                    Thread.sleep(3000);
                }
                catch(Exception e){
                }

                // Initialize Firebase Auth
                mAuth = FirebaseAuth.getInstance();
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){

                    SharedPreferences prefs = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                    String id = prefs.getString("id", "T07057");//"No name defined" is the default value.

                    if(id.substring(0,2).equals("T0")){

                        intent = new Intent(IntroActivity.this, Dashboard1.class);
                    }
                    else{

                        intent = new Intent(IntroActivity.this, AdminDashboardActivity.class);
                    }

                    Log.e("Test id: ", id);

                    intent.putExtra("id", id);
                }
                startActivity(intent);

            }};
        thread.start();

    }
}