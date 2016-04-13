package name.caiyao.microreader.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import name.caiyao.microreader.BuildConfig;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.UpdateItem;
import name.caiyao.microreader.event.StatusBarEvent;
import name.caiyao.microreader.utils.CacheUtil;
import name.caiyao.microreader.utils.RxBus;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class SettingsFragment extends PreferenceFragment {
    Preference prefCache;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prefCache = findPreference(getString(R.string.pre_cache_size));
        prefCache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CacheUtil.deleteDir(SettingsFragment.this.getActivity().getCacheDir());
                showCacheSize(prefCache);
                return true;
            }
        });
        findPreference(getString(R.string.pre_feedback)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "me@caiyao.name", null));
                    startActivity(Intent.createChooser(intent, "选择邮件客户端:"));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
        findPreference(getString(R.string.pre_author)).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://caiyao.name/releases")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        findPreference(getString(R.string.pre_status_bar)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                RxBus.getDefault().send(new StatusBarEvent());
                return true;
            }
        });
        findPreference(getString(R.string.pre_nav_color)).setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                RxBus.getDefault().send(new StatusBarEvent());
                return true;
            }
        });
        Preference version = findPreference(getString(R.string.pre_version));
        version.setSummary(BuildConfig.VERSION_NAME);
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ZhihuRequest.getZhihuApi().getUpdateInfo()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<UpdateItem>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getActivity(), getString(R.string.update_no_update), Toast.LENGTH_SHORT).show();
                                //这个可能异常Error occurred when trying to propagate error to Observer.onError
                                //e.printStackTrace();
                            }

                            @Override
                            public void onNext(final UpdateItem updateItem) {
                                if (updateItem.getVersionCode() > BuildConfig.VERSION_CODE)
                                    new AlertDialog.Builder(getActivity())
                                            .setTitle(getString(R.string.update_title))
                                            .setMessage(String.format(getString(R.string.update_description), updateItem.getVersionName(), updateItem.getReleaseNote()))
                                            .setPositiveButton(getString(R.string.update_button), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateItem.getDownloadUrl())));
                                                }
                                            })
                                            .setNegativeButton(getString(R.string.common_cancel), new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                else
                                    Toast.makeText(getActivity(), getString(R.string.update_no_update), Toast.LENGTH_SHORT).show();
                            }
                        });
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showCacheSize(prefCache);
    }

    private void showCacheSize(Preference preference) {
        preference.setSummary(getActivity().getString(R.string.cache_size) + CacheUtil.getCacheSize(getActivity().getCacheDir()));
    }
}
