package name.caiyao.microreader.ui.iView;

import name.caiyao.microreader.bean.zhihu.ZhihuDaily;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public interface IZhihuFragment {
    void showProgressDialog();

    void hidProgressDialog();

    void showError(String error);

    void updateList(ZhihuDaily zhihuDaily);
}
