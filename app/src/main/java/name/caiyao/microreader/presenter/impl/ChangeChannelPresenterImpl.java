package name.caiyao.microreader.presenter.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.presenter.IChangeChannelPresenter;
import name.caiyao.microreader.ui.iView.IChangeChannel;
import name.caiyao.microreader.utils.SharePreferenceUtil;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class ChangeChannelPresenterImpl implements IChangeChannelPresenter {

    private IChangeChannel mIChangeChannel;
    private SharedPreferences mSharedPreferences;
    private ArrayList<String> savedChannelList;

    public ChangeChannelPresenterImpl(IChangeChannel changeChannel, Context context) {
        mIChangeChannel = changeChannel;
        mSharedPreferences = context.getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        savedChannelList = new ArrayList<>();
    }

    @Override
    public void getSaved() {
        String savedChannel = mSharedPreferences.getString(SharePreferenceUtil.SAVED_CHANNEL, "");
        if (TextUtils.isEmpty(savedChannel)) {
            for (Config.Channel channel : Config.Channel.values()) {
                savedChannelList.add(channel.name());
            }
        } else {
            Collections.addAll(savedChannelList, savedChannel.split(","));
        }
        mIChangeChannel.showSavedChannel(savedChannelList);
    }

    @Override
    public void getAll() {

    }
}
