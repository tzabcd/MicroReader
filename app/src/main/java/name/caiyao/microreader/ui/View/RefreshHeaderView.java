package name.caiyao.microreader.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeRefreshTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

/**
 * Created by 蔡小木 on 2016/3/6 0006.
 */
public class RefreshHeaderView extends TextView implements SwipeRefreshTrigger, SwipeTrigger {

    public RefreshHeaderView(Context context) {
        super(context);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onRefresh() {
        setText("正在拼命加载数据...");
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onSwipe(int i, boolean b) {
        setText("释放刷新");
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void complete() {
        setText("刷新成功");
    }

    @Override
    public void onReset() {

    }
}
