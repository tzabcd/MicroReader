package name.caiyao.microreader.presenter;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public interface IZhihuPresenter {
    void getLastZhihuNews();

    void getTheDaily(String date);

    void getLastFromCache();
}
