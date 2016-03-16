package name.caiyao.microreader.api.weixin;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;

import name.caiyao.microreader.MicroApplication;
import name.caiyao.microreader.utils.NetWorkUtil;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 蔡小木 on 2016/3/4 0004.
 */
public class TxRequest {
    private static TxApi txApi = null;
    public static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY);
    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvaliable(MicroApplication.getContext())) {
                int maxAge = 60 * 20; // 在线缓存在1分钟内可读取
                Logger.i("在线缓存！");
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                Logger.i("离线缓存！");
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    static File httpCacheDirectory = new File(MicroApplication.getContext().getCacheDir(), "txCache");
    static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    static Cache cache = new Cache(httpCacheDirectory, cacheSize);

    static OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .addInterceptor(interceptor)
            .cache(cache)
            .build();

    public static TxApi getTxApi() {
        if (txApi == null) {
            txApi = new Retrofit.Builder()
                    .baseUrl("http://api.huceo.com")
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(TxApi.class);
        }
        return txApi;
    }
}
