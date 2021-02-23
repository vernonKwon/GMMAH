package com.GMMAS.gwon_ocheol.schoolapp;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class FacebookOnWebView extends AppCompatActivity {

    private Button[] site = new Button[4];

    private String[] link = {"https://www.facebook.com/gmmahs/", //학생회
            "https://www.facebook.com/%EA%B4%91%EB%AA%85%EA%B2%BD%EC%98%81%ED%9A%8C%EA%B3%84%EA%B3%A0-%EC%8A%A4%EB%8B%88%EC%BB%A4%EC%A6%88-391604287690123/", //스니커즈
            "https://www.facebook.com/%EA%B4%91%EB%AA%85%EA%B2%BD%EC%98%81%ED%9A%8C%EA%B3%84%EA%B3%A0-%EC%82%AC%ED%9A%8C%EC%A0%81%ED%98%91%EB%8F%99%EC%A1%B0%ED%95%A9-%EB%A7%A4%EC%A0%90-%EB%8B%A4%EB%9D%BD%E5%A4%9A%EF%A4%94-119513295378403/", // 다락
            "https://www.facebook.com/schoolgbs/"//방송부
    };

    private Integer[] button_id = {com.GMMAS.gwon_ocheol.schoolapp.R.id.gmmahs, com.GMMAS.gwon_ocheol.schoolapp.R.id.sneakers, com.GMMAS.gwon_ocheol.schoolapp.R.id.store, com.GMMAS.gwon_ocheol.schoolapp.R.id.gbs};

    private WebView webView;

    private AdView mAdView;
    private WebViewClient webViewClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.GMMAS.gwon_ocheol.schoolapp.R.layout.facebookonwebview);

        mAdView = (AdView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.adView);

        MobileAds.initialize(this, "pub-3069912788772819");
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        webViewClient = new WebViewClient();

        webView = (WebView) findViewById(com.GMMAS.gwon_ocheol.schoolapp.R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        for (int i = 0; i < site.length; i++) {
            final int index = i;
            site[i] = (Button) findViewById(button_id[i]);
            site[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    webView.loadUrl(link[index]);
                    webView.setWebViewClient(webViewClient);
                }
            });
        }


    }
}
