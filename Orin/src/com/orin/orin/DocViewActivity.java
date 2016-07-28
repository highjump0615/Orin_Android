/**
 * @author Ry
 * @date 2013.12.29
 * @filename DocViewActivity.java
 */

package com.orin.orin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.fragments.SettingsFragment;
import com.orin.orin.util.Config;

public class DocViewActivity extends Activity {

    private static final String TAG = DocViewActivity.class.getSimpleName();

    public static final String DOC_TYPE = "doc_type";

    private int mDocType = 0;

    private String[] titles = { "Privacy Policy", "Terms of Service", "FAQs" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mDocType = intent.getIntExtra(DOC_TYPE, 0);
        }

        setContentView(R.layout.activity_doc_view);
        initViews();
        initWebView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
    }

    private void initViews() {
        final Resources res = getResources();

        // TitleBar
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.layout_title_bar);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titleBar.getLayoutParams();
        layoutParams.height = (int) (res.getDimension(R.dimen.title_bar_height) * Config.mScaleFactor);
        titleBar.setLayoutParams(layoutParams);

        // Back Button
        Button btnBack = (Button) findViewById(R.id.btn_cancel);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnBack.setText("Back");
        float textSize = res.getDimension(R.dimen.title_bar_button_text_size) * Config.mFontScaleFactor;
        int padding = (int) (res.getDimension(R.dimen.title_bar_button_padding) * Config.mScaleFactor);
        btnBack.setTextSize(textSize);
        btnBack.setPadding(padding, 0, 0, 0);

        //
        Button button = (Button) findViewById(R.id.btn_done);
        button.setVisibility(View.INVISIBLE);
        button.setTextSize(textSize);
        button.setPadding(0, 0, padding, 0);

        // Title TextView
        TextView textTitle = (TextView) findViewById(R.id.text_title);
        textTitle.setText(titles[mDocType]);
        textSize = res.getDimension(R.dimen.title_bar_small_text_size) * Config.mFontScaleFactor;
        textTitle.setTextSize(textSize);
    }

    private void initWebView() {
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setDefaultFontSize((int) (getResources().getDimension(R.dimen.item_text_size) * Config.mFontScaleFactor));

        String url = null;

        switch (mDocType) {
            case SettingsFragment.PRIVACY_POLICY:
                url = "file:///android_asset/html/privacy_policy.html";
                break;

            case SettingsFragment.TERMS_OF_SERVICE:
                url = "file:///android_asset/html/term_of_service.html";
                break;

            case SettingsFragment.FAQS:
                url = "file:///android_asset/html/faqs.html";
                break;
        }

        if (!TextUtils.isEmpty(url)) {
            Log.i(TAG, "url = " + url);
            webView.loadUrl(url);
        } else {
            finish();
        }
    }

}
