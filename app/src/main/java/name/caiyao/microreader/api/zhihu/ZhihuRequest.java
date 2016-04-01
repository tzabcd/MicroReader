package name.caiyao.microreader.api.zhihu;

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
 * Created by 蔡小木 on 2016/3/6 0006.
 */
public class ZhihuRequest {
    private static ZhihuApi zhihuApi = null;

    static OkHttpClient client = new OkHttpClient.Builder()
            .build();

    public static ZhihuApi getZhihuApi() {
        if (zhihuApi == null) {
            zhihuApi = new Retrofit.Builder()
                    .baseUrl("http://news-at.zhihu.com")
                    .client(client)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ZhihuApi.class);
        }
        return zhihuApi;
    }
}
