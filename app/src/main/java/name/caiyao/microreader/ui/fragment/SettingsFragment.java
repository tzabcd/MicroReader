package name.caiyao.microreader.ui.fragment;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import name.caiyao.microreader.R;
import name.caiyao.microreader.utils.CacheUtil;


public class SettingsFragment extends PreferenceFragment {
    Preference pref;

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
        pref = findPreference("cache_size");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CacheUtil.deleteDir(getActivity().getCacheDir());
                showCacheSize(pref);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        showCacheSize(pref);
    }

    private void showCacheSize(Preference preference) {
        preference.setSummary("缓存大小：" + CacheUtil.getCacheSize(getActivity().getCacheDir()));
    }
}
