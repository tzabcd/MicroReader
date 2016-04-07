package name.caiyao.microreader.api.zhihu;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 蔡小木 on 2016/3/6 0006.
 */
public class ZhihuRequest {
    private static ZhihuApi zhihuApi = null;

    public static ZhihuApi getZhihuApi() {
        if (zhihuApi == null) {
            zhihuApi = new Retrofit.Builder()
                    .baseUrl("http://news-at.zhihu.com")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(ZhihuApi.class);
        }
        return zhihuApi;
    }
}
