package name.caiyao.microreader.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

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

    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "请允许此应用的读写文件以便于缓存文件！", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            getBackground();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getBackground();
            }
        } else {
            Snackbar.make(getWindow().getDecorView(), "没有权限臣妾做不到呀!", Snackbar.LENGTH_INDEFINITE).setAction("再次获取权限", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            });
        }
    }

    private void getBackground() {
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
                                startActivity(new Intent(WelcomeActivity.this, MainActivity.class).putExtra(SharePreferenceUtil.HAS_GET_IMAGE, false));
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
                                startActivity(new Intent(WelcomeActivity.this, MainActivity.class).putExtra(SharePreferenceUtil.HAS_GET_IMAGE, true));
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
