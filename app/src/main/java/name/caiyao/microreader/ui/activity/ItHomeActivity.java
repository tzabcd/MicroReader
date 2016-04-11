package name.caiyao.microreader.ui.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.apkfuns.logutils.LogUtils;

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
        setToolBar(toolbar, true, true, null);
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
                            settings.setBuiltInZoomControls(true);
                            settings.setDomStorageEnabled(true);
                            settings.setAppCacheEnabled(true);
                            settings.setJavaScriptEnabled(true);
                            settings.setPluginState(WebSettings.PluginState.ON);
                            settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                            settings.setUseWideViewPort(true);
                            settings.setDomStorageEnabled(true);
                            settings.setDatabaseEnabled(true);
                            settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");
                            settings.setAppCacheEnabled(true);
                            settings.setLoadWithOverviewMode(true);
                            wvWeixin.setWebChromeClient(new WebChromeClient());
                            wvWeixin.loadUrl(itHomeItem.getUrl());
                        } else {
                            String data = WebUtil.BuildHtmlWithCss(itHomeArticle.getDetail(), new String[]{"news.css"}, false);
                            wvWeixin.loadDataWithBaseURL(WebUtil.BASE_URL, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, itHomeItem.getUrl());
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, itHomeItem.getTitle() + " http://ithome.com" + itHomeItem.getUrl() + getString(R.string.share_tail));
            shareIntent.setType("text/plain");
            //设置分享列表的标题，并且每次都显示分享列表
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
