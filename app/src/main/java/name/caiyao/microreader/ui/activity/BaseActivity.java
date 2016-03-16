package name.caiyao.microreader.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.bugtags.library.Bugtags;
import com.umeng.analytics.MobclickAgent;

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
}
