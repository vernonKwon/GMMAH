package com.GMMAS.gwon_ocheol.schoolapp;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Dev_information extends AppCompatActivity {
    private TextView email_Textview, bug;
    private Intent email;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.GMMAS.gwon_ocheol.schoolapp.R.layout.activity_dev_information);

        email_Textview = (TextView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.email);
        bug = (TextView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.bugReporting);

        uri = Uri.parse("kwondroid@daum.net");
        email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        email.putExtra(Intent.EXTRA_EMAIL, "kwondroid@daum.net");
        email.putExtra(Intent.EXTRA_SUBJECT, "GMMAS 이메일 보냅니다.");
        email.putExtra(Intent.EXTRA_TEXT, "수신 이메일은 kwondroid@daum.net 입니다. \n 안드로이드 자체의 버그때문에 gmail 앱에선 받는사람 칸이 비어있을 수 있습니다. \n 이 글을 지우고 내용을 써주세요");

        email_Textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(email, "의견 보내기"));
            }
        });

        bug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(Intent.createChooser(email, "버그 리포트"));
            }
        });
    }
}
