package name.caiyao.microreader.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

    public static boolean isChangeThemeAuto(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getResources().getString(R.string.pre_get_image), true);
    }

    public static boolean isImmersiveMode(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pre_status_bar), false);
    }

    public static boolean isChangeNavColor(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean(context.getString(R.string.pre_nav_color), true);
    }
}
