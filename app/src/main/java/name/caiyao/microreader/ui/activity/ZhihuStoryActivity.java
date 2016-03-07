package name.caiyao.microreader.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.zhihu.ZhihuStory;
import name.caiyao.microreader.utils.WebUtil;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ZhihuStoryActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.wv_zhihu)
    WebView wvZhihu;
    @Bind(R.id.iv_zhihu_story)
    ImageView ivZhihuStory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhihu_story);
        ButterKnife.bind(this);
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
        get();
    }

    String id;

    private void get() {
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
}
