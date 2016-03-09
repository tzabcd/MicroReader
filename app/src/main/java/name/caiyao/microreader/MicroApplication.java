package name.caiyao.microreader;

import android.app.Application;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;

/**
 * Created by 蔡小木 on 2016/3/9 0009.
 */
public class MicroApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingLocation(true).//是否获取位置
                trackingCrashLog(false).//是否收集crash
                trackingConsoleLog(true).//是否收集console log
                trackingUserSteps(true).//是否收集用户操作步骤
                build();
        Bugtags.start("9c1b1a3234ceeb5b9c531177a93b65ec", this, Bugtags.BTGInvocationEventBubble, options);
    }
}
