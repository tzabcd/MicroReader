package name.caiyao.microreader.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.UnsupportedEncodingException;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.guokr.GuokrRequest;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.guokr.GuokrArticle;
import name.caiyao.microreader.bean.zhihu.ZhihuStory;
import name.caiyao.microreader.utils.WebUtil;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ZhihuStoryActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhihu_story);
        ButterKnife.bind(this);
        type = getIntent().getIntExtra("type", 0);
        id = getIntent().getStringExtra("id");
        String title = getIntent().getStringExtra("title");
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (type == TYPE_ZHIHU) {
            getZhihuDaily();
        } else if (type == TYPE_GUOKR) {
            getGuokr();
        }
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
                        if (TextUtils.isEmpty(zhihuStory.getBody())) {
                            WebSettings settings = wvZhihu.getSettings();
                            settings.setJavaScriptEnabled(true);
                            settings.setDomStorageEnabled(true);
                            settings.setAppCacheEnabled(true);
                            wvZhihu.setWebChromeClient(new WebChromeClient());
                            wvZhihu.loadUrl(zhihuStory.getShare_url());
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
                        Glide.with(ZhihuStoryActivity.this).load(guokrArticle.getResult().getSmall_image()).into(ivZhihuStory);
                        if (TextUtils.isEmpty(guokrArticle.getResult().getContent())) {
                            WebSettings settings = wvZhihu.getSettings();
                            settings.setJavaScriptEnabled(true);
                            settings.setDomStorageEnabled(true);
                            settings.setAppCacheEnabled(true);
                            wvZhihu.setWebChromeClient(new WebChromeClient());
                            wvZhihu.loadUrl(guokrArticle.getResult().getUrl());
                        } else {
                            String data = WebUtil.BuildHtmlWithCss(guokrArticle.getResult().getContent(), new String[0], false);
                            wvZhihu.loadDataWithBaseURL(WebUtil.BASE_URL, data, WebUtil.MIME_TYPE, WebUtil.ENCODING, WebUtil.FAIL_URL);
                        }
                    }
                });
    }
}
