package name.caiyao.microreader.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.itHome.ItHomeRequest;
import name.caiyao.microreader.bean.itHome.ItHomeArticle;
import name.caiyao.microreader.bean.itHome.ItHomeItem;
import name.caiyao.microreader.utils.ItHomeUtil;
import name.caiyao.microreader.utils.WebUtil;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/3/25 0025.
 */
public class ItHomeActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.wv_weixin)
    WebView wvWeixin;

    private ItHomeItem itHomeItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ithome);
        ButterKnife.bind(this);
        itHomeItem = getIntent().getParcelableExtra("item");
        toolbar.setTitle(itHomeItem.getTitle());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setToolBar(toolbar, true, true, false);
        getIthomeArticle();
    }

    private void getIthomeArticle() {
        ItHomeRequest.getItHomeApi().getItHomeArticle(ItHomeUtil.getSplitNewsId(itHomeItem.getNewsid()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ItHomeArticle>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ItHomeArticle itHomeArticle) {
                        if (TextUtils.isEmpty(itHomeArticle.getDetail())) {
                            WebSettings settings = wvWeixin.getSettings();
                            settings.setJavaScriptEnabled(true);
                            settings.setBuiltInZoomControls(true);
                            settings.setDomStorageEnabled(true);
                            settings.setAppCacheEnabled(true);
                            wvWeixin.setWebChromeClient(new WebChromeClient());
                            wvWeixin.loadUrl(itHomeItem.getUrl());
                        } else {
                            wvWeixin.loadDataWithBaseURL(WebUtil.BASE_URL, itHomeArticle.getDetail(), WebUtil.MIME_TYPE, WebUtil.ENCODING, itHomeItem.getUrl());
                        }
                    }
                });
    }
}
