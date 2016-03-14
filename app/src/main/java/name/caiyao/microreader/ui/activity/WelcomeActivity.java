package name.caiyao.microreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import name.caiyao.microreader.R;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.image.ImageReponse;
import name.caiyao.microreader.utils.SharePreferenceUtil;
import rx.Observer;
import rx.schedulers.Schedulers;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        ZhihuRequest.getZhihuApi().getImage().subscribeOn(Schedulers.io())
                .subscribe(new Observer<ImageReponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(WelcomeActivity.this, MainActivity.class).putExtra(SharePreferenceUtil.HAS_GET_IMAGE,false));
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onNext(ImageReponse imageReponse) {
                        if (imageReponse.getData() != null && imageReponse.getData().getImages() != null) {
                            getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putString(SharePreferenceUtil.IMAGE_DESCRIPTION, imageReponse.getData().getImages().get(0).getDescription()).apply();
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(new URL("http://wpstatic.zuimeia.com/" + imageReponse.getData().getImages().get(0).getImage_url() + "?imageMogr/v2/auto-orient/thumbnail/480x320/quality/100").openConnection().getInputStream());
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(getFilesDir().getPath() + "/bg.jpg")));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(WelcomeActivity.this, MainActivity.class).putExtra(SharePreferenceUtil.HAS_GET_IMAGE,true));
                                finish();
                            }
                        });
                    }
                });


    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
