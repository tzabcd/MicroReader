package name.caiyao.microreader.ui.fragment;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import java.io.File;

import name.caiyao.microreader.R;
import name.caiyao.microreader.utils.CacheUtil;


public class SettingsFragment extends PreferenceFragment {


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
        final Preference pref = findPreference("cache_size");
        showCacheSize(pref);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                CacheUtil.get(getActivity()).clear();
                showCacheSize(pref);
                return true;
            }
        });
    }

    private void showCacheSize(Preference preference){
        File f = new File(getActivity().getCacheDir(), "ACache");
        preference.setSummary("缓存大小："+f.length()/1024+"KB");
    }
}
