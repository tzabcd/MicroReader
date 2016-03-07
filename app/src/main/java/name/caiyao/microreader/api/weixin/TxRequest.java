package name.caiyao.microreader.api.weixin;

import okhttp3.OkHttpClient;
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
    public static TxApi getTxApi() {
        if (txApi == null) {
            txApi = new Retrofit.Builder()
                    .baseUrl("http://api.huceo.com")
                    .client(new OkHttpClient.Builder().addInterceptor(interceptor).build())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(TxApi.class);
        }
        return txApi;
    }
}
