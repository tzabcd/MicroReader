package name.caiyao.microreader.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

/**
 * Created by 蔡小木 on 2016/3/6 0006.
 */
public class LoaderMoreView extends TextView implements SwipeTrigger, SwipeLoadMoreTrigger {
    public LoaderMoreView(Context context) {
        super(context);
    }

    public LoaderMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LoaderMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LoaderMoreView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLoadMore() {
        setText("正在拼命加载数据...");
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onSwipe(int i, boolean b) {
        setText("释放加载");
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void complete() {
        setText("加载成功");
    }

    @Override
    public void onReset() {

    }
}
