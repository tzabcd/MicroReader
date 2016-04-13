package name.caiyao.microreader.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
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
        wvVideo.setWebChromeClient(new WebChromeClient() {

            //显示全屏按钮
            private View myView = null;
            private CustomViewCallback myCallback = null;

            @Override
            public void onShowCustomView(View view, CustomViewCallback callback) {
                if (myCallback != null) {
                    myCallback.onCustomViewHidden();
                    myCallback = null;
                    return;
                }
                ViewGroup parent = (ViewGroup) wvVideo.getParent();
                parent.removeView(wvVideo);
                parent.addView(view);
                myView = view;
                myCallback = callback;
            }

            @Override
            public void onHideCustomView() {
                if (myView != null) {
                    if (myCallback != null) {
                        myCallback.onCustomViewHidden();
                        myCallback = null;
                    }
                    ViewGroup parent = (ViewGroup) myView.getParent();
                    parent.removeView(myView);
                    parent.addView(wvVideo);
                    myView = null;
                }
            }

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                result.confirm();
                return super.onJsAlert(view, url, message, result);
            }
        });
        wvVideo.loadUrl(url);
    }
}
