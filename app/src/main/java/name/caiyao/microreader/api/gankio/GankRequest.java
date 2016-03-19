package name.caiyao.microreader.api.gankio;

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
 * Created by 蔡小木 on 2016/3/9 0009.
 */
public class GankRequest {
    /**
     * 分类数据: http://gank.avosapps.com/api/data/数据类型/请求个数/第几页
     * •数据类型： 福利 | Android | iOS | 休息视频 | 拓展资源 | 前端 | all
     * •请求个数： 数字，大于0
     * •第几页：数字，大于0
     * <p/>
     * 例：•http://gank.avosapps.com/api/data/Android/10/1
     * •http://gank.avosapps.com/api/data/福利/10/1
     * •http://gank.avosapps.com/api/data/iOS/20/2
     * •http://gank.avosapps.com/api/data/all/20/2
     * <p/>
     * 每日数据： http://gank.avosapps.com/api/day/年/月/日
     * <p/>
     * 例：
     * •http://gank.avosapps.com/api/day/2015/08/06
     * <p/>
     * 随机数据：http://gank.avosapps.com/api/random/data/分类/个数
     * •数据类型：福利 | Android | iOS | 休息视频 | 拓展资源 | 前端
     * •个数： 数字，大于0
     * <p/>
     * 例：•http://gank.avosapps.com/api/random/data/Android/20
     */
    private static GankApi gankApi = null;
    public static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY);

    private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originalResponse = chain.proceed(chain.request());
            if (NetWorkUtil.isNetWorkAvailable(MicroApplication.getContext())) {
                int maxAge = 60; // 在线缓存在1分钟内可读取
                Logger.i("在线缓存！");
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                Logger.i("离线缓存！");
                int maxStale = 60 * 60 * 24 * 28; // 离线时缓存保存4周
                return originalResponse.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    };

    static File httpCacheDirectory = new File(MicroApplication.getContext().getCacheDir(), "ganCache");
    static int cacheSize = 10 * 1024 * 1024; // 10 MiB
    static Cache cache = new Cache(httpCacheDirectory, cacheSize);

    static OkHttpClient client = new OkHttpClient.Builder()
            //.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR)
            .cache(cache)
            .addInterceptor(interceptor)
            .build();

    public static GankApi getGankApi() {
        if (gankApi == null) {
            gankApi = new Retrofit.Builder()
                    .baseUrl("http://gank.io")
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(GankApi.class);
        }
        return gankApi;
    }
}
