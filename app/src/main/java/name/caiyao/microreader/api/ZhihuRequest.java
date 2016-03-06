package name.caiyao.microreader.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 蔡小木 on 2016/3/6 0006.
 */
public class ZhihuRequest {
    private static ZhihuApi zhihuApi = null;
    public static HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(HttpLoggingInterceptor.Logger.DEFAULT).setLevel(HttpLoggingInterceptor.Level.BODY);
    public static ZhihuApi getZhihuApi() {
        if (zhihuApi == null) {
            zhihuApi = new Retrofit.Builder()
                    .baseUrl("http://news-at.zhihu.com")
                    .client(new OkHttpClient.Builder().addInterceptor(interceptor).build())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ZhihuApi.class);
        }
        return zhihuApi;
    }
}
