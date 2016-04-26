package name.caiyao.microreader.presenter.impl;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import name.caiyao.microreader.R;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.UpdateItem;
import name.caiyao.microreader.presenter.IMainPresenter;
import name.caiyao.microreader.ui.fragment.GuokrFragment;
import name.caiyao.microreader.ui.fragment.ItHomeFragment;
import name.caiyao.microreader.ui.fragment.VideoFragment;
import name.caiyao.microreader.ui.fragment.WeixinFragment;
import name.caiyao.microreader.ui.fragment.ZhihuFragment;
import name.caiyao.microreader.ui.iView.IMain;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class MainPresenterImpl implements IMainPresenter {

    private ArrayList<Fragment> mFragments;
    private ArrayList<Integer> titles;
    private IMain mIMain;
    private String[] menuItemArr = new String[]{
            "zhihu",
            "it",
            "guokr",
            "weixin",
            "video"
    };

    public MainPresenterImpl(IMain main) {
        if (main == null)
            throw new IllegalArgumentException("main must not be null");
        mIMain = main;
        mFragments = new ArrayList<>();
        titles = new ArrayList<>();
    }

    @Override
    public void initMenu(NavigationView navigationView) {
        Menu menu = navigationView.getMenu();
        menu.clear();
        for (int i = 0; i < menuItemArr.length; i++) {
            MenuItem menuItem = menu.add(0, i, 0, getTitle(menuItemArr[i]));
            titles.add(getTitle(menuItemArr[i]));
            menuItem.setIcon(getIconId(menuItemArr[i]));
            menuItem.setCheckable(true);
            addFragment(menuItemArr[i]);
            if (i == 0) {
                menuItem.setChecked(true);
            }
        }
        navigationView.inflateMenu(R.menu.activity_main_drawer);
        mIMain.initMenu(mFragments, titles);
    }

    @Override
    public void checkUpdate() {
        ZhihuRequest.getZhihuApi().getUpdateInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(final UpdateItem updateItem) {
                        mIMain.showUpdate(updateItem);
                    }
                });
    }

    private void addFragment(String key) {
        switch (key) {
            case "guokr":
                mFragments.add(new GuokrFragment());
                break;
            case "it":
                mFragments.add(new ItHomeFragment());
                break;
            case "zhihu":
                mFragments.add(new ZhihuFragment());
                break;
            case "weixin":
                mFragments.add(new WeixinFragment());
                break;
            case "video":
                mFragments.add(new VideoFragment());
                break;
        }
    }

    private int getIconId(String key) {
        switch (key) {
            case "guokr":
                return R.drawable.icon_guokr;
            case "it":
                return R.drawable.it;
            case "zhihu":
                return R.drawable.icon_zhihu;
            case "weixin":
                return R.drawable.icon_weixin;
            case "video":
                return R.drawable.icon_video;
        }
        return 0;
    }

    private int getTitle(String key) {
        switch (key) {
            case "guokr":
                return R.string.fragment_guokr_title;
            case "it":
                return R.string.fragment_it_title;
            case "zhihu":
                return R.string.fragment_zhihu_title;
            case "weixin":
                return R.string.fragment_wexin_title;
            case "video":
                return R.string.fragment_video_title;
        }
        return 0;
    }
}
