package name.caiyao.microreader.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.orhanobut.logger.Logger;

import name.caiyao.microreader.R;

/**
 * Created by 蔡小木 on 2016/3/4 0004.
 */
public class Config {
    public static final String TX_APP_KEY = "1ae28fc9dd5afadc696ad94cd59426d8";

    public static boolean isRefreshOnlyWifi(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.pre_refresh_data), false);
    }

    public static boolean isChangeThemeAuto(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Logger.i("获取图片："+sharedPreferences.getBoolean(context.getResources().getString(R.string.pre_get_image), true));
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.pre_get_image), true);
    }
}
