package name.caiyao.microreader.api.gankio;

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

    public static GankApi getGankApi() {
        if (gankApi == null) {
            gankApi = new Retrofit.Builder()
                    .baseUrl("http://gank.avosapps.com")
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(GankApi.class);
        }
        return gankApi;
    }
}
