package name.caiyao.microreader.presenter.impl;

import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import name.caiyao.microreader.R;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.UpdateItem;
import name.caiyao.microreader.config.Config;
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
    private Config.Channel[] menuItemArr = Config.Channel.values();

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
            MenuItem menuItem = menu.add(0, i, 0, menuItemArr[i].getTitle());
            titles.add(menuItemArr[i].getTitle());
            menuItem.setIcon(menuItemArr[i].getIcon());
            menuItem.setCheckable(true);
            addFragment(menuItemArr[i].name());
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

    private void addFragment(String name) {
        switch (name) {
            case "GUOKR":
                mFragments.add(new GuokrFragment());
                break;
            case "WEIXIN":
                mFragments.add(new WeixinFragment());
                break;
            case "ZHIHU":
                mFragments.add(new ZhihuFragment());
                break;
            case "VIDEO":
                mFragments.add(new VideoFragment());
                break;
            case "IT":
                mFragments.add(new ItHomeFragment());
                break;
        }

    }
}
