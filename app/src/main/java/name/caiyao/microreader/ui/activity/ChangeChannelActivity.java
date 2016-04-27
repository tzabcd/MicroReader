package name.caiyao.microreader.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hannesdorfmann.swipeback.Position;
import com.hannesdorfmann.swipeback.SwipeBack;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.presenter.IChangeChannelPresenter;
import name.caiyao.microreader.presenter.impl.ChangeChannelPresenterImpl;
import name.caiyao.microreader.ui.iView.IChangeChannel;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class ChangeChannelActivity extends BaseActivity implements IChangeChannel {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.rv_)
    RecyclerView mRv;

    private ArrayList<Config.Channel> savedChannel = new ArrayList<>();
    private ArrayList<Config.Channel> dismissChannel = new ArrayList<>();
    private IChangeChannelPresenter mIChangeChannelPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SwipeBack.attach(this, Position.LEFT)
                .setContentView(R.layout.activity_change_channel)
                .setSwipeBackView(R.layout.swipe_back);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mRv.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

    }

    private void initData() {
        mIChangeChannelPresenter = new ChangeChannelPresenterImpl(this, this);
    }

    @Override
    public void showSavedChannel(ArrayList<Config.Channel> savedChannel) {

    }

    @Override
    public void showDismissChannel(ArrayList<Config.Channel> savedChannel) {

    }


}
