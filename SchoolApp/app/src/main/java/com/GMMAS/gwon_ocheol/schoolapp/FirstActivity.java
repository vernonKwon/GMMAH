package com.GMMAS.gwon_ocheol.schoolapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class FirstActivity extends AppCompatActivity {

    private Firstfragment firstfragment;
    private Intent intent1, intent2, intent3, intent4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.GMMAS.gwon_ocheol.schoolapp.R.layout.activity_first);

        findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.dev_inform).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Dev_information.class));
            }
        });


        firstfragment = (Firstfragment) getSupportFragmentManager().findFragmentById(com.GMMAS.gwon_ocheol.schoolapp.R.id.Firstfragement);
        intent1 = new Intent(getApplicationContext(), MealInformation.class);
        intent2 = new Intent(getApplicationContext(), FacebookOnWebView.class);
        intent3 = new Intent(getApplicationContext(), AnonymousBoard.class);
        intent4 = new Intent(getApplicationContext(), Notice.class);

    }

    public void intent(int input) {
        switch (input) {
            case 0:
                startActivity(intent1);
                break;

            case 1:
                startActivity(intent2);
                break;

            case 2:
                startActivity(intent3);
                break;

            case 3:
                startActivity(intent4);
                break;

        }
    }
}
