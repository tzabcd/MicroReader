package name.caiyao.microreader.api.video;

import name.caiyao.microreader.api.gankio.GankApi;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 蔡小木 on 2016/3/9 0009.
 */
public class VideoRequest {
    private static VideoApi videoApi = null;

    public static VideoApi getVideoApi() {
        if (videoApi == null) {
            videoApi = new Retrofit.Builder()
                    .baseUrl("http://www.baidu.com")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build().create(VideoApi.class);
        }
        return videoApi;
    }
}
