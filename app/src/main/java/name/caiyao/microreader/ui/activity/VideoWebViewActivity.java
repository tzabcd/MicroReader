package name.caiyao.microreader.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.webkit.WebView;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;

/**
 * Created by 蔡小木 on 2016/4/12 0012.
 */
public class VideoWebViewActivity extends BaseActivity {

    @Bind(R.id.wv_video)
    WebView wvVideo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_webview);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra("url");
        wvVideo.loadUrl(url);
    }
}
