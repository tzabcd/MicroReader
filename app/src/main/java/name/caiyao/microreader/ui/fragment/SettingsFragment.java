package name.caiyao.microreader.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import name.caiyao.microreader.BuildConfig;
import name.caiyao.microreader.R;
import name.caiyao.microreader.utils.CacheUtil;


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
                    startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(" http://caiyao.name")));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        Preference version = findPreference(getString(R.string.pre_version));
        version.setSummary(BuildConfig.VERSION_NAME);
        version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(getActivity())
                        .setUpdateFrom(UpdateFrom.XML)
                        .setUpdateXML("https://raw.githubusercontent.com/YiuChoi/MicroReader/master/app/update.xml")
                        .withListener(new AppUpdaterUtils.UpdateListener() {
                            @Override
                            public void onSuccess(Update update, Boolean isUpdateAvailable) {
                                AppUpdater appUpdater = new AppUpdater(getActivity());
                                appUpdater.setDialogTitleWhenUpdateAvailable(getString(R.string.update_title))
                                        .setDialogDescriptionWhenUpdateAvailable(String.format(getString(R.string.update_description), update.getLatestVersion()))
                                        .setDialogButtonUpdate(getString(R.string.update_button))
                                        .setDialogButtonDoNotShowAgain(getString(R.string.update_not_show))
                                        .setDialogTitleWhenUpdateNotAvailable(getString(R.string.update_no_update))
                                        .setDialogDescriptionWhenUpdateNotAvailable(getString(R.string.update_no_update_description));
                                appUpdater.setUpdateFrom(UpdateFrom.XML).showAppUpdated(true)
                                        .setUpdateXML("https://raw.githubusercontent.com/YiuChoi/MicroReader/master/app/update.xml");
                                appUpdater.start();
                            }

                            @Override
                            public void onFailed(AppUpdaterError error) {
                            }
                        });
                appUpdaterUtils.start();
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
