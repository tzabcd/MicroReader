package name.caiyao.microreader.api.guokr;

import name.caiyao.microreader.bean.guokr.GuokrArticle;
import name.caiyao.microreader.bean.guokr.GuokrHot;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by 蔡小木 on 2016/3/7 0007.
 */
public interface GuokrApi {

    @GET("hot")
    Observable<GuokrHot> getGuokrHot();

    @GET("http://apis.guokr.com/minisite/article/{id}.json")
    Observable<GuokrArticle> getGuokrArticle(@Path("id") String id);
}
