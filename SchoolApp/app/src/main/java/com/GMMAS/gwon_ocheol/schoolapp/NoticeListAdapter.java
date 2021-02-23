package com.GMMAS.gwon_ocheol.schoolapp;

/**
 * Created by lghlo on 2017-07-25.
 */

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class NoticeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<NoticeDTO> NoticeDataSet = new ArrayList<>();
    View view;

    public NoticeListAdapter(ArrayList<NoticeDTO> mDataSet) {
        NoticeDataSet = mDataSet;
    }


    @Override
    public NoticeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(com.GMMAS.gwon_ocheol.schoolapp.R.layout.notice_card_layout, parent, false);

        return new NoticeViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        ((NoticeViewHolder) holder).mDate.setText(NoticeDataSet.get(position).getDate());
        ((NoticeViewHolder) holder).mTitle.setText(NoticeDataSet.get(position).getTitle());
        ((NoticeViewHolder) holder).mWriter.setText(NoticeDataSet.get(position).getWriter());
        ((NoticeViewHolder) holder).mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = NoticeDataSet.get(position).getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                ((NoticeViewHolder) holder).mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return NoticeDataSet.size();
    }

    class NoticeViewHolder extends RecyclerView.ViewHolder {
        final Context mContext;
        TextView mDate;
        TextView mTitle;
        TextView mWriter;
        Button mButton;

        public NoticeViewHolder(View view) {
            super(view);
            mContext = view.getContext();
            mDate = (TextView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.date_text);
            mTitle = (TextView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.notice_title_text);
            mWriter = (TextView) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.writer_text);
            mButton = (Button) view.findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.in_web_notice_btn);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}