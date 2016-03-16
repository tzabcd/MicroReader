package name.caiyao.microreader.ui.fragment;


import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;


public class BaseFragment extends Fragment {


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

}
