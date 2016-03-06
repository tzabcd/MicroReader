package name.caiyao.microreader.api;

import name.caiyao.microreader.bean.ZhihuDaily;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 蔡小木 on 2016/3/6 0006.
 */
public interface ZhihuApi {

    @GET("/api/4/news/latest")
    Observable<ZhihuDaily> getLastDaily();

    @GET("/api/4/news/before/{date}")
    Observable<ZhihuDaily> getTheDaily(@Path("date") String date);
}
