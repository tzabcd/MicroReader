package name.caiyao.microreader.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import name.caiyao.microreader.R;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.image.ImageResponse;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.utils.SharePreferenceUtil;
import rx.Observer;
import rx.schedulers.Schedulers;

public class WelcomeActivity extends BaseActivity {

    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 1;
    private String date;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        sharedPreferences = getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        DateFormat dateFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.DATE_FIELD);
        date = dateFormat.format(new Date());

        if (!sharedPreferences.getString(SharePreferenceUtil.IMAGE_GET_TIME, "").equals(date)) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                new AlertDialog.Builder(this).setMessage(getString(R.string.request_storage_permission)).setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
                    }
                }).show();
            } else {
                getBackground();
            }
        } else {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getBackground();
            } else {
                new AlertDialog.Builder(this).setMessage(getString(R.string.re_request_permission)).setPositiveButton(getString(R.string.common_i_know), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(WelcomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_REQUEST_CODE);
                    }
                }).show();
            }
        }
    }

    private void getBackground() {
        if (Config.isChangeThemeAuto(this)){
            ZhihuRequest.getZhihuApi().getImage().subscribeOn(Schedulers.io())
                    .subscribe(new Observer<ImageResponse>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WelcomeActivity.this,getString(R.string.common_loading_error),Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onNext(ImageResponse imageReponse) {
                            if (imageReponse.getData() != null && imageReponse.getData().getImages() != null) {
                                try {
                                    Bitmap bitmap = BitmapFactory.decodeStream(new URL("http://wpstatic.zuimeia.com/" + imageReponse.getData().getImages().get(0).getImage_url() + "?imageMogr/v2/auto-orient/thumbnail/480x320/quality/100").openConnection().getInputStream());
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(getFilesDir().getPath() + "/bg.jpg")));
                                    Palette palette = Palette.from(bitmap).generate();
                                    Palette.Swatch swatch = palette.getVibrantSwatch();
                                    int color = 0x000000;
                                    int vibrant = palette.getVibrantColor(color);
                                    int vibrantLight = palette.getLightVibrantColor(color);
                                    int vibrantDark = palette.getDarkVibrantColor(color);
                                    int muted = palette.getMutedColor(color);
                                    int mutedLight = palette.getLightMutedColor(color);
                                    int mutedDark = palette.getDarkMutedColor(color);
                                    sharedPreferences.edit()
                                            .putString(SharePreferenceUtil.IMAGE_DESCRIPTION, imageReponse.getData().getImages().get(0).getDescription())
                                            .putInt(SharePreferenceUtil.VIBRANT, vibrant)
                                            .putInt(SharePreferenceUtil.VIBRANT_LIGHT, vibrantLight)
                                            .putInt(SharePreferenceUtil.VIBRANT_DARK, vibrant)
                                            .putInt(SharePreferenceUtil.MUTED, muted)
                                            .putInt(SharePreferenceUtil.MUTED_LIGHT, mutedLight)
                                            .putInt(SharePreferenceUtil.MUTED_DARK, mutedDark)
                                            .putString(SharePreferenceUtil.IMAGE_GET_TIME, date)
                                            .putInt(SharePreferenceUtil.TITLE_TEXT_COLOR, swatch != null ? swatch.getTitleTextColor() : 0)
                                            .apply();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                                    finish();
                                }
                            });
                        }
                    });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
