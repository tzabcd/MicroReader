package name.caiyao.microreader.api.gankio;

import name.caiyao.microreader.bean.gankio.GankVideo;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 蔡小木 on 2016/3/9 0009.
 */
public interface GankApi {

    @GET("/api/data/休息视频/10/{page}")
    Observable<GankVideo> getVideoList(@Path("page") int page);
}
