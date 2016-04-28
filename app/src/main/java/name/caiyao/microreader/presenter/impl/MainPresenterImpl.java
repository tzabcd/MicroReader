package name.caiyao.microreader.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collections;

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
import name.caiyao.microreader.utils.SharePreferenceUtil;
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
    private ArrayList<Config.Channel> savedChannelList;
    private SharedPreferences mSharedPreferences;

    public MainPresenterImpl(IMain main, Context context) {
        if (main == null)
            throw new IllegalArgumentException("main must not be null");
        mSharedPreferences = context.getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        mIMain = main;
        mFragments = new ArrayList<>();
        titles = new ArrayList<>();
        savedChannelList = new ArrayList<>();
    }

    @Override
    public void initMenu(NavigationView navigationView) {
        savedChannelList.clear();
        titles.clear();
        Menu menu = navigationView.getMenu();
        menu.clear();
        mFragments.clear();
        String savedChannel = mSharedPreferences.getString(SharePreferenceUtil.SAVED_CHANNEL, null);
        if (TextUtils.isEmpty(savedChannel)) {
            Collections.addAll(savedChannelList, Config.Channel.values());
        } else {
            for (String s : savedChannel.split(",")) {
                savedChannelList.add(Config.Channel.valueOf(s));
            }
        }
        for (int i = 0; i < savedChannelList.size(); i++) {
            MenuItem menuItem = menu.add(0, i, 0, savedChannelList.get(i).getTitle());
            titles.add(savedChannelList.get(i).getTitle());
            menuItem.setIcon(savedChannelList.get(i).getIcon());
            menuItem.setCheckable(true);
            addFragment(savedChannelList.get(i).name());
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
