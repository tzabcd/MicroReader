package name.caiyao.microreader;

import android.app.Application;

import com.bugtags.library.Bugtags;
import com.bugtags.library.BugtagsOptions;
import com.orhanobut.logger.AndroidLogTool;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by 蔡小木 on 2016/3/9 0009.
 */
public class MicroApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Logger
                .init("MicroReader")             // default PRETTYLOGGER or use just init()
                .methodCount(3)                 // default 2
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL)        // default LogLevel.FULL
                .methodOffset(2)                // default 0
                .logTool(new AndroidLogTool()); // custom log tool, optional

        BugtagsOptions options = new BugtagsOptions.Builder().
                trackingLocation(true).//是否获取位置
                trackingCrashLog(false).//是否收集crash
                trackingConsoleLog(true).//是否收集console log
                trackingUserSteps(true).//是否收集用户操作步骤
                build();
        Bugtags.start("9c1b1a3234ceeb5b9c531177a93b65ec", this, Bugtags.BTGInvocationEventNone, options);
        MobclickAgent.setCatchUncaughtExceptions(false);
    }
}
