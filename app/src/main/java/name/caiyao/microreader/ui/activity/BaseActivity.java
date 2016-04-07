package name.caiyao.microreader.ui.activity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import com.apkfuns.logutils.LogUtils;
import com.bugtags.library.Bugtags;
import com.jaeger.library.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import name.caiyao.microreader.R;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.event.StatusBarEvent;
import name.caiyao.microreader.utils.RxBus;
import name.caiyao.microreader.utils.SharePreferenceUtil;
import rx.Subscription;
import rx.functions.Action1;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();
        Bugtags.onResume(this);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Bugtags.onPause(this);
        MobclickAgent.onPause(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Bugtags.onDispatchTouchEvent(this, event);
        return super.dispatchTouchEvent(event);
    }

    public int setToolBar(Toolbar toolbar, boolean isChangeToolbar, boolean isChangeStatusBar, DrawerLayout drawerLayout) {
        int vibrantColor = getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.VIBRANT, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(vibrantColor);
        }
        if (isChangeToolbar)
            toolbar.setBackgroundColor(vibrantColor);
        if (isChangeStatusBar) {
            if (Config.isImmersiveMode(this))
                StatusBarUtil.setColorNoTranslucent(this, vibrantColor);
            else
                StatusBarUtil.setColor(this,vibrantColor);
        }
        if (drawerLayout != null){
            //目前只能显示默认颜色，待解决
            if (Config.isImmersiveMode(this))
                StatusBarUtil.setColorNoTranslucentForDrawerLayout(this, drawerLayout, ContextCompat.getColor(this,R.color.colorPinkPrimary));
            else
                StatusBarUtil.setColorForDrawerLayout(this, drawerLayout,  ContextCompat.getColor(this,R.color.colorPinkPrimary));
        }
        return vibrantColor;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            SystemBarTintManager tintManager = new SystemBarTintManager(this);
//            if (isChangeStatusBar){
//                tintManager.setStatusBarTintEnabled(true);
//            }
//            if (marginTop && Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT){
//                SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
//                getWindow().getDecorView().getRootView().setPadding(0, toolbar.getHeight(), config.getPixelInsetRight(), 0);
//            }
//            tintManager.setNavigationBarTintEnabled(true);
//            tintManager.setTintColor(Config.vibrantColor);
//        }
    }
}
