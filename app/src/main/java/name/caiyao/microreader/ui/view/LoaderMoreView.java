package name.caiyao.microreader.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.SwipeLoadMoreTrigger;
import com.aspsine.swipetoloadlayout.SwipeTrigger;

import name.caiyao.microreader.R;

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
        setText(R.string.common_view_loading);
    }

    @Override
    public void onPrepare() {
        setText("");
    }

    @Override
    public void onMove(int yScrolled, boolean isComplete, boolean automatic) {
        if (!isComplete) {
            if (yScrolled >= getHeight()) {
                setText(R.string.common_view_release);
            } else {
                setText("SWIPE TO REFRESH");
            }
        } else {
            setText("REFRESH RETURNING");
        }
    }

    @Override
    public void onRelease() {
        setText("LOADING MORE");
    }

    @Override
    public void onComplete() {
        setText(R.string.common_view_loading_ok);
    }

    @Override
    public void onReset() {
        setText("");
    }
}
