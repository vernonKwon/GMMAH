package com.GMMAS.gwon_ocheol.schoolapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Notice extends AppCompatActivity {
    private final String NOTICE_DEFAULT_URL = "http://www.gmma.hs.kr/wah/main/mobile/bbs/list.htm?menuCode=69&scale=10&searchField=&searchKeyword=&pageNo=";
    public static ArrayList<NoticeDTO> mNoticeDataset = new ArrayList<>();
    private boolean net_status;
    private boolean NoticeOn;
    private boolean show = false;
    private CreateAnimator mAnimator;

    protected boolean NoticeOk;

    RecyclerView noticeRecyclerView;
    ProgressBar progressBar;
    Button notice_page_fab, notice_browser_btn, notice_refresh_btn, prev_notice_btn, next_notice_btn;
    LinearLayout notice_arc;

    private int page = 1;

    NoticeDataParser noticeDataParser;
    NoticeListAdapter noticeListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.GMMAS.gwon_ocheol.schoolapp.R.layout.activity_notice);
        //setContentView(R.layout.notice_fragment);

        mAnimator = new CreateAnimator();

        noticeRecyclerView = (RecyclerView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.notice_card_view);
        noticeRecyclerView.setHasFixedSize(true);

        progressBar = (ProgressBar) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.notice_loading);

        notice_page_fab = (Button) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.notice_page_fab);
        notice_browser_btn = (Button) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.notice_browser_btn);
        notice_refresh_btn = (Button) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.notice_refresh_btn);
        prev_notice_btn = (Button) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.prev_notice_btn);
        next_notice_btn = (Button) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.next_notice_btn);

        notice_arc = (LinearLayout) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.notice_arc);
        notice_page_fab.setText(String.valueOf(page));

        if (show) {
            notice_arc.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.GONE);


        notice_page_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFabClick(v);
            }
        });

        next_notice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next_notice_btn.setEnabled(true);
                prev_notice_btn.setEnabled(true);
                ++page;
                RefreshNotice(page);

            }
        });

        prev_notice_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page > 1) {
                    next_notice_btn.setEnabled(true);
                    prev_notice_btn.setEnabled(true);
                    --page;
                    Toast(String.valueOf(page));
                    RefreshNotice(page);
                } else if (page <= 1) {
                    page = 1;
                    prev_notice_btn.setEnabled(false);
                    Toast(String.valueOf(page));
                    RefreshNotice(page);
                }
            }
        });

        notice_browser_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                Uri u = Uri.parse("http://www.gmma.hs.kr/wah/main/mobile/bbs/list.htm?menuCode=69&scale=10&searchField=&searchKeyword=&pageNo=" + page);
                i.setData(u);
                startActivity(i);
            }
        });

        noticeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        noticeListAdapter = new NoticeListAdapter(mNoticeDataset);
        noticeRecyclerView.setAdapter(noticeListAdapter);

        noticeDataParser = new NoticeDataParser(this, "http://www.gmma.hs.kr/wah/main/mobile/bbs/list.htm?menuCode=69&scale=10&searchField=&searchKeyword=&pageNo=", 1);

        /*SharedPreferences mPref = getSharedPreferences("Setting", Context.MODE_PRIVATE);
        NoticeOn = mPref.getBoolean("SAVEMODE_SET_NOTICE", false);
        NoticeOk = false;*/

        /*net_status = getWhatKindOfNetwork(this);
        if (net_status) {
            if (!NoticeOn) {
                new NoticeDataParser(this, NOTICE_DEFAULT_URL, 1);
            } else {
                for (int i = 0; i < 10; i++) {
                    mNoticeDataset.add(new NoticeListItem("0000. 0. 00", "표시할 데이터가 없습니다.", "---", NOTICE_DEFAULT_URL + 1));
                }
                NoticeOk = true;
            }
        }*/
    }

    private void onFabClick(View v) {
        if (show) {
            hideMenu();
        } else {
            showMenu();
        }
        show = !show;
    }

    private void showMenu() {
        notice_arc.setVisibility(View.VISIBLE);
        List<Animator> animList = new ArrayList<>();

        for (int i = 0, len = notice_arc.getChildCount(); i < len; i++) {
            animList.add(mAnimator.createShowAnimator(notice_arc.getChildAt(i), notice_page_fab, 5, CreateAnimator.Direction.HORIZONTAL));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(300);
        animSet.setInterpolator(new OvershootInterpolator());
        animSet.playTogether(animList);
        animSet.start();
    }

    @SuppressWarnings("NewApi")
    private void hideMenu() {
        List<Animator> animList = new ArrayList<>();

        for (int i = notice_arc.getChildCount() - 1; i >= 0; i--) {
            animList.add(mAnimator.createHideAnimator(notice_arc.getChildAt(i), notice_page_fab, 5, CreateAnimator.Direction.HORIZONTAL));
        }

        AnimatorSet animSet = new AnimatorSet();
        animSet.setDuration(400);
        animSet.setInterpolator(new AnticipateInterpolator());
        animSet.playTogether(animList);
        animSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                notice_arc.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
            }
        });
        animSet.start();
    }

    /*public static boolean getWhatKindOfNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return true;
        }
        return false;
    }*/

    public void RefreshNotice(int index) {

        new NoticeDataParser(this, "http://www.gmma.hs.kr/wah/main/mobile/bbs/list.htm?menuCode=69&scale=10&searchField=&searchKeyword=&pageNo=", index);
        notice_page_fab.setText(String.valueOf(page));
        progressBar.setVisibility(View.VISIBLE);
    }

    public void Toast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    class NoticeDataParser extends AsyncTask<String, String, Boolean> {
        private ArrayList<String> writer_list = new ArrayList<>();
        private ArrayList<String> url_list = new ArrayList<>();
        private ArrayList<String> date_list = new ArrayList<>();
        private ArrayList<String> title_list = new ArrayList<>();
        private Notice inActivity;
        int date_count, title_count, writer_count, url_count;
        //int page = 1;
        boolean Init = true;

        public NoticeDataParser(Notice activity, String url, int n) {
            Init = true;
            inActivity = activity;
            page = n;
            this.execute(url);
        }


        public Boolean doInBackground(String... url) {

            date_list.clear();
            url_list.clear();
            date_list.clear();
            title_list.clear();
            mNoticeDataset.clear();

            date_count = title_count = writer_count = url_count = 10;
            boolean result = false;
            Document doc = null;
            Elements writer = null;
            Elements post = null;
            try {
                doc = Jsoup.connect(url[0] + page).timeout(5000).get();
                writer = doc.select(".writer"); // 작성자, 게시물 날짜
                post = doc.select(".boardList_cont1 a"); // 본문 Url, 제목

                String postUrl = post.toString().replaceAll("<a href=\".", "");
                postUrl = postUrl.replaceAll("&amp;", "&");

                String writerNdate;
                writerNdate = writer.toString().replaceAll("<p class=\"writer\">", "");
                writerNdate = writerNdate.replaceAll("<span class=\"date\">", "");
                writerNdate = writerNdate.replaceAll("&nbsp", "");
                writerNdate = writerNdate.replaceAll("</span></p>", ";");
                writerNdate = writerNdate.replaceAll("\\s+", "");

                StringTokenizer writerNdate_tokens = new StringTokenizer(writerNdate, ";");
                StringTokenizer url_tokens = new StringTokenizer(postUrl, "\n");
                StringTokenizer title_tokens = new StringTokenizer(post.select(".boardList_tit").toString(), "</strong>");

                while (title_tokens.hasMoreTokens()) {
                    String str = title_tokens.nextToken();
                    if (str.charAt(0) == '"') {
                        title_count--;
                        title_list.add(title_tokens.nextToken());
                    }
                }

                while (url_tokens.hasMoreTokens()) {
                    String str = url_tokens.nextToken();
                    str = str.substring(0, str.indexOf('"'));
                    if (str.charAt(0) == '/') {
                        url_count--;
                        url_list.add("http://www.gmma.hs.kr/wah/main/mobile/bbs/" + str);
                    }
                }

                while (writerNdate_tokens.hasMoreTokens()) {
                    String str = writerNdate_tokens.nextToken();
                    if (str.charAt(0) == '[') {
                        date_count--;
                        date_list.add(str.substring(1, str.indexOf(']')));
                    } else {
                        writer_count--;
                        writer_list.add(str);
                    }
                }
                result = true;
            } catch (IOException e) {
                Log.e("ERROR", "In data parse progress (Notice) : IOException");
            } catch (Exception e) {
                Log.e("ERROR", "In data parse progress (Notice) : Exception");

            } finally {
                for (int i = 0; i < date_count; i++) {
                    date_list.add("0000. 0. 00");
                }

                for (int i = 0; i < title_count; i++) {
                    title_list.add("데이터를 표시할 수 없습니다. 본문보기를 눌러주십시오.");
                }

                for (int i = 0; i < writer_count; i++) {
                    writer_list.add("---");
                }

                for (int i = 0; i < url_count; i++) {
                    url_list.add(url[0] + page);
                }
            }
            for (int i = 0; i < date_list.size(); i++) {
                mNoticeDataset.add(new NoticeDTO(date_list.get(i), title_list.get(i), writer_list.get(i), url_list.get(i)));
            }

            if (inActivity != null) {
                inActivity.NoticeOk = true;
            }
            return result;
        }

        protected void onPostExecute(Boolean status) {
        /*if(Init){

            inActivity.LoadFinish();
        } else {
            fr.ReloadFragment();
        }*/
            noticeListAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.GONE);
        }
    }

}
