package name.caiyao.microreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.guokr.GuokrRequest;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.guokr.GuokrArticle;
import name.caiyao.microreader.bean.zhihu.ZhihuStory;
import name.caiyao.microreader.utils.SharePreferenceUtil;
import name.caiyao.microreader.utils.WebUtil;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ZhihuStoryActivity extends BaseActivity {

    public static final int TYPE_ZHIHU = 1;
    public static final int TYPE_GUOKR = 2;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.wv_zhihu)
    WebView wvZhihu;
    @Bind(R.id.iv_zhihu_story)
    ImageView ivZhihuStory;

    int type;
    String id;
    String title;
    String url;

    @Bind(R.id.ctl)
    CollapsingToolbarLayout ctl;
    @Bind(R.id.nest)
    NestedScrollView nest;
    @Bind(R.id.fabButton)
    FloatingActionButton fabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_zhihu_story)
                .setSwipeBackView(R.layout.swipe_back);
        ButterKnife.bind(this);

        type = getIntent().getIntExtra("type", 0);
        id = getIntent().getStringExtra("id");
        title = getIntent().getStringExtra("title");

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        boolean isKitKat = Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;
        int vibrantColor = setToolBar(fabButton,toolbar, false, isKitKat, null);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.swipe_back);
        if (linearLayout != null) {
            linearLayout.setBackgroundColor(vibrantColor);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZhihuStoryActivity.this.onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (type == TYPE_ZHIHU) {
            getZhihuDaily();
        } else if (type == TYPE_GUOKR) {
            getGuokr();
        }

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nest.smoothScrollTo(0,0);
            }
        });

        WebSettings settings = wvZhihu.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setLoadWithOverviewMode(true);
        settings.setBuiltInZoomControls(true);
        //settings.setUseWideViewPort(true);造成文字太小
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(getCacheDir().getAbsolutePath() + "/webViewCache");
        settings.setAppCacheEnabled(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wvZhihu.setWebChromeClient(new WebChromeClient());
        ctl.setContentScrimColor(getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.VIBRANT, ContextCompat.getColor(this, R.color.colorPrimary)));
        ctl.setStatusBarScrimColor(getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.VIBRANT, ContextCompat.getColor(this, R.color.colorPrimary)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + url + getString(R.string.share_tail));
                shareIntent.setType("text/plain");
                //设置分享列表的标题，并且每次都显示分享列表
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getZhihuDaily() {
        ZhihuRequest.getZhihuApi().getZhihuStory(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuStory>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(ZhihuStory zhihuStory) {
                        Glide.with(ZhihuStoryActivity.this).load(zhihuStory.getImage()).into(ivZhihuStory);
                        url = zhihuStory.getShareUrl();
                        if (TextUtils.isEmpty(zhihuStory.getBody())) {
                            wvZhihu.loadUrl(zhihuStory.getShareUrl());
                        } else {
                            String data = WebUtil.BuildHtmlWithCss(zhihuStory.getBody(), zhihuStory.getCss(), false);
                            wvZhihu.loadDataWithBaseURL(WebUtil.BASE_URL, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, WebUtil.FAIL_URL);
                        }
                    }
                });
    }

    private void getGuokr() {
        GuokrRequest.getGuokrApi().getGuokrArticle(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GuokrArticle>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(GuokrArticle guokrArticle) {
                        Glide.with(ZhihuStoryActivity.this).load(guokrArticle.getResult().getSmallImage()).into(ivZhihuStory);
                        url = guokrArticle.getResult().getUrl();
                        if (TextUtils.isEmpty(guokrArticle.getResult().getContent())) {
                            wvZhihu.loadUrl(guokrArticle.getResult().getUrl());
                        } else {
                            //解决图片显示问题
                            String data = WebUtil.BuildHtmlWithCss(guokrArticle.getResult().getContent().replaceAll("(style.*?\")>", ""), new String[]{"news.css"}, false);
                            wvZhihu.loadDataWithBaseURL(WebUtil.BASE_URL, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, WebUtil.FAIL_URL);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
