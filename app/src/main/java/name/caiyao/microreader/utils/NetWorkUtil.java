package name.caiyao.microreader.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.orhanobut.logger.Logger;

/**
 * Created by 蔡小木 on 2016/3/16 0016.
 */
public class NetWorkUtil {

    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        Logger.i("网络是否可用:"+(networkInfo != null && networkInfo.isConnected()));
        return networkInfo != null && networkInfo.isConnected();
    }
}
