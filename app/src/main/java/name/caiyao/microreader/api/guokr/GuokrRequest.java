package name.caiyao.microreader.api.guokr;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 蔡小木 on 2016/3/7 0007.
 */
public class GuokrRequest {
    public static String[] channel_key = {"hot", "frontier", "review", "interview", "visual", "brief", "fact", "techb"};
    public static String[] channel_title = {"热点", "前沿", "评论", "专访", "视觉", "速读", "谣言粉碎机", "商业科技"};
    public static String science_channel_url = "http://www.guokr.com/apis/minisite/article.json?retrieve_type=by_channel&channel_key=";

    private static GuokrApi guokrApi = null;
    public static GuokrApi getGuokrApi() {
        if (guokrApi == null) {
            guokrApi = new Retrofit.Builder()
                    .baseUrl(science_channel_url)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(GuokrApi.class);
        }
        return guokrApi;
    }

}
