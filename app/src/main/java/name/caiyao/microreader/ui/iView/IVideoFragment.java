package name.caiyao.microreader.ui.iView;

import java.util.ArrayList;

import name.caiyao.microreader.bean.weiboVideo.WeiboVideoBlog;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public interface IVideoFragment {
    void showProgressDialog();

    void hidProgressDialog();

    void showError(String error);

    void updateList(ArrayList<WeiboVideoBlog> weiboVideoBlogs);
}
