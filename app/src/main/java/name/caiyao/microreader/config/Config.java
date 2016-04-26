package name.caiyao.microreader.config;

import android.support.v4.app.Fragment;

import name.caiyao.microreader.R;
import name.caiyao.microreader.ui.fragment.GuokrFragment;
import name.caiyao.microreader.ui.fragment.ItHomeFragment;
import name.caiyao.microreader.ui.fragment.VideoFragment;
import name.caiyao.microreader.ui.fragment.WeixinFragment;
import name.caiyao.microreader.ui.fragment.ZhihuFragment;

/**
 * Created by 蔡小木 on 2016/3/4 0004.
 */
public class Config {
    public static final String TX_APP_KEY = "1ae28fc9dd5afadc696ad94cd59426d8";

    public static final String DB__IS_READ_NAME = "IsRead";
    public static final String WEIXIN = "weixin";
    public static final String GUOKR = "guokr";
    public static final String ZHIHU = "zhihu";
    public static final String VIDEO = "video";
    public static final String IT = "it";

    public enum Channel {
        WEIXIN( R.string.fragment_wexin_title, R.drawable.icon_weixin,new WeixinFragment()),
        GUOKR(R.string.fragment_guokr_title, R.drawable.icon_guokr,new GuokrFragment()),
        ZHIHU(R.string.fragment_zhihu_title, R.drawable.icon_zhihu, new ZhihuFragment()),
        VIDEO(R.string.fragment_video_title, R.drawable.icon_video,new VideoFragment()),
        IT( R.string.fragment_it_title, R.drawable.it,new ItHomeFragment());

        private int title;
        private int icon;
        private Fragment mFragment;

        Channel(int title, int icon,Fragment fragment) {
            this.title = title;
            this.icon = icon;
            this.mFragment = fragment;
        }

        public int getTitle() {
            return title;
        }

        public void setTitle(int title) {
            this.title = title;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public Fragment getFragment() {
            return mFragment;
        }

        public void setFragment(Fragment fragment) {
            mFragment = fragment;
        }
    }
}
