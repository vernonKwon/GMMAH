package com.GMMAS.gwon_ocheol.schoolapp;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class PostSetData extends AppCompatActivity {

    //private DatabaseReference mDatabase;

    private final static String TAG = "오류";

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    private AES256Util aes256; // key는 16자 이상

    private TextView headline;
    private Typeface typeFace;
    private ImageButton upload_post;

    private Date date = new Date();
    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private TimeZone tz;

    private EditText[] objects = new EditText[4];
    private int[] edittext = {com.GMMAS.gwon_ocheol.schoolapp.R.id.subject, com.GMMAS.gwon_ocheol.schoolapp.R.id.content, com.GMMAS.gwon_ocheol.schoolapp.R.id.nickname, com.GMMAS.gwon_ocheol.schoolapp.R.id.password};

    private PostDTO uploaddata;
    private String day;

    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.GMMAS.gwon_ocheol.schoolapp.R.layout.activity_upload_post);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        headline = (TextView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.Headline_Upload_Post);
        typeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/KBIZ한마음고딕.ttf");
        headline.setTypeface(typeFace); // 글 올리기 글씨체

        mAdView = (AdView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.adView);

        MobileAds.initialize(this, "pub-3069912788772819");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        tz = TimeZone.getTimeZone("Asia/Seoul");
        df.setTimeZone(tz);

        for (int i = 0; i < edittext.length; i++) {
            objects[i] = (EditText) findViewById(edittext[i]);
        }

        upload_post = (ImageButton) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.Upload_post);
        upload_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(objects[0].getText().toString()) | TextUtils.isEmpty(objects[1].getText().toString()) |
                        TextUtils.isEmpty(objects[2].getText().toString()) | TextUtils.isEmpty(objects[3].getText().toString())) {

                    Toast.makeText(getApplicationContext(), "입력을 다시 확인해주세요", Toast.LENGTH_SHORT).show();

                } else {
                    uploaddata = new PostDTO();
                    try {
                        aes256 = new AES256Util(mAuth.getCurrentUser().getUid()); // key는 16자 이상
                        uploaddata.setPassword(aes256.aesEncode(objects[3].getText().toString())); // 비밀번호
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "객체 생성 실패", Toast.LENGTH_SHORT).show();
                    }
                    uploaddata.setSubject(objects[0].getText().toString()); // 제목
                    uploaddata.setContent(objects[1].getText().toString()); // 내용
                    uploaddata.setNickname(objects[2].getText().toString()); // 닉네임
                    day = df.format(date);
                    uploaddata.setDate(day); // 오늘 날짜

                    database.getReference().child("post").push().setValue(uploaddata);

                    finish();
                }
            }
        });
    }

}
