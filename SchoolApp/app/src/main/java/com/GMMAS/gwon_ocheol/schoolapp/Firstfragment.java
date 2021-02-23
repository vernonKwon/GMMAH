package com.GMMAS.gwon_ocheol.schoolapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by gwon-ocheol on 2017. 11. 20..
 */

public class Firstfragment extends Fragment {
    FirstActivity firstActivity;

    Integer[] button_ids = {com.GMMAS.gwon_ocheol.schoolapp.R.id.mealinfromation , com.GMMAS.gwon_ocheol.schoolapp.R.id.FacebookPage, com.GMMAS.gwon_ocheol.schoolapp.R.id.anaymous_board, com.GMMAS.gwon_ocheol.schoolapp.R.id.Notice};

    ImageButton[] button = new ImageButton[4];

    ViewGroup viewGroup;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(com.GMMAS.gwon_ocheol.schoolapp.R.layout.firstfragment, container, false);
        firstActivity = (FirstActivity) getActivity();

        for(int i=0;i<button.length;i++){
            button[i] = (ImageButton) viewGroup.findViewById(button_ids[i]);
        }

        button[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstActivity.intent(0);
            }
        });
        button[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstActivity.intent(1);
            }
        });
        button[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstActivity.intent(2);
            }
        });
        button[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstActivity.intent(3);
            }
        });

        return viewGroup;
    }
}
