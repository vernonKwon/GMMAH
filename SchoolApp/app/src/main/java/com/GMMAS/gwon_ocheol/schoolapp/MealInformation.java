package com.GMMAS.gwon_ocheol.schoolapp;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MealInformation extends AppCompatActivity {
    final static String TAG = "오류"; // for log

    //RatingBar ratingBar;

    private Date date = new Date();
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat month;
    private SimpleDateFormat day;

    private String meal = null;

    private int select;

    private TextView whenmeal, showdate, showmeal;

    private Calendar cal = Calendar.getInstance();
    private int maximumday = cal.getMaximum(Calendar.DAY_OF_MONTH); //한달의 day 수

    private int month_int, day_int;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private AdView mAdView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.GMMAS.gwon_ocheol.schoolapp.R.layout.activity_mealinfromation);
        MobileAds.initialize(this, "pub-3069912788772819");

        mAdView = (AdView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        /*mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId("ca-app-pub-3069912788772819/4552403979");*/

        dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        month = new SimpleDateFormat("MM");
        day = new SimpleDateFormat("d");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //ratingBar = (RatingBar) findViewById(R.id.star);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); // 네트워크 제한 풀기

        month_int = Integer.parseInt(month.format(date));
        day_int = Integer.parseInt(day.format(date));

        select = day_int;// int
        /*button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MealGood mealGood = new MealGood();
                mealGood.meal = meal;
                mDatabase.child("meal").child(String.valueOf(dateFormat.format(date))).setValue(mealGood);
            }
        });*/

        showdate = (TextView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.showdate);
        showmeal = (TextView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.showmeal);
        whenmeal = (TextView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.meal);//Textview

        //login();
        getmeal();

        //setstar = (Button) findViewById(R.id.setstar);
        /*ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                aDouble = Double.valueOf(rating);
            }
        });
        setstar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MealGood mealGood = new MealGood();
                mealGood.meal = "test";
                mDatabase.child("meal").child(String.valueOf(dateFormat.format(date))).setValue(mealGood);

                onStarClicked(mDatabase.child("meal").child(String.valueOf(dateFormat.format(date))).push());
            }
        });
*/

        Typeface typeFace = Typeface.createFromAsset(getAssets(), "font/KBIZ한마음고딕.ttf");
        whenmeal.setTypeface(typeFace);

        showdate.setText(String.valueOf(month_int + "월 " + day_int + "일"));

        /*mDatabase.child("meal").child(String.valueOf(dateFormat.format(date))).child("meal").addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MealGood mealGood;
                try{
                    mealGood = dataSnapshot.getValue(MealGood.class);
                } catch (DatabaseException e){
                    mealGood = new MealGood();
                    mealGood.starCount = 0.0;
                    mealGood.meal = "null";
                    mDatabase.child("meal").child(String.valueOf(dateFormat.format(date))).setValue(mealGood); // 코드를 이따위로 더럽게 짜야하나...
                }


                if(mealGood != null){
                    showmeal.setText(mealGood.meal);
                } else {
                    mealGood = new MealGood();
                    mealGood.meal = meal;
                    //mealGood.starCount = mealGood.starCount + ratingBar.getRating();
                    //mealGood.stars.put(mAuth.getCurrentUser().getUid(), true);
                    mDatabase.child("meal").child(String.valueOf(dateFormat.format(date))).setValue(mealGood);
                }
                Log.e(TAG, "onDataChange: success");

                showmeal.setText(meal);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getmeal();
                showmeal.setText(meal);
                Log.e(TAG, "onCancelled: ");
                MealGood mealGood = new MealGood();
                mealGood.meal = meal;
                mealGood.starCount = mealGood.starCount + ratingBar.getRating();
                mealGood.stars.put(mAuth.getCurrentUser().getUid(), true);
                mDatabase.child("meal").child(String.valueOf(dateFormat.format(date))).setValue(mealGood);

            }
        });*/


    }

    /*private void onStarClicked(DatabaseReference postRef) {

        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                MealGood mealGood = mutableData.getValue(MealGood.class);
                if (mealGood == null) {
                    return Transaction.success(mutableData);
                }

                if (mealGood.stars.containsKey(mAuth.getCurrentUser().getUid())) {
                    // Unstar the post and remove self from stars
                    *//*mealGood.starCount = mealGood.starCount - 1;
                    mealGood.stars.remove(mAuth.getCurrentUser().getUid());*//*
                } else {
                    // Star the post and add self to stars
                    mealGood.starCount = mealGood.starCount + ratingBar.getRating();
                    mealGood.stars.put(mAuth.getCurrentUser().getUid(), true);
                }

                // Set value and report transaction success
                mutableData.setValue(mealGood);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                // Transaction completed
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });
    }*/

    public void getmeal() {
        Document document = null;
        Elements todaymeal = null;
        try {
            document = Jsoup.connect("http://www.gmma.hs.kr/wah/main/schoolmeal/calendar.htm?menuCode=102").get(); // 학교 급식 사이트 파싱
            todaymeal = document.select(".Contents_schoolmeal_ToDay"); // 오늘의 급식 메뉴
            if (todaymeal.text().length() > 0) { // Stringindexoutofboundsexception 대비를 위함

                meal = todaymeal.text().substring(2).trim();
                showmeal.setText(meal);
                Log.e(TAG, meal);
            }

            Log.i(TAG, "급식 정보 파싱 완료");


        } catch (IOException e) {
            showmeal.setText("급식 정보가 없습니다");
            Log.e(TAG, "Exception");
            e.printStackTrace();

        }
    }

    /*private void login() {
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {// 로그인 하기
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    System.out.println("Authentication success");
                } else {
                    // If sign in fails, display a message to the user.
                    System.out.println("Authentication failed");
                }
            }

        });
        Log.i(TAG, "login: 완료");
    }*/
}