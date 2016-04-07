package name.caiyao.microreader.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.apkfuns.logutils.LogUtils;
import com.jaeger.library.StatusBarUtil;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.BuildConfig;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.UpdateItem;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.event.StatusBarEvent;
import name.caiyao.microreader.ui.fragment.GuokrFragment;
import name.caiyao.microreader.ui.fragment.ItHomeFragment;
import name.caiyao.microreader.ui.fragment.VideoFragment;
import name.caiyao.microreader.ui.fragment.WeixinFragment;
import name.caiyao.microreader.ui.fragment.ZhihuFragment;
import name.caiyao.microreader.utils.RxBus;
import name.caiyao.microreader.utils.SharePreferenceUtil;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.replace)
    FrameLayout replace;
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.ctl_main)
    CoordinatorLayout ctlMain;

    private Fragment currentFragment;

    public Subscription rxSubscription;

    private WeixinFragment weixinFragment = new WeixinFragment();
    private ItHomeFragment itHomeFragment = new ItHomeFragment();
    private ZhihuFragment zhihuFragment = new ZhihuFragment();
    private GuokrFragment guokrFragment = new GuokrFragment();
    private VideoFragment videoFragment = new VideoFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        rxSubscription = RxBus.getDefault().toObservable(StatusBarEvent.class)
                .subscribe(new Action1<StatusBarEvent>() {
                    @Override
                    public void call(StatusBarEvent statusBarEvent) {
                        setToolBar(toolbar, true, false,drawer);                    }
                });
        setToolBar(toolbar, true, false,drawer);
        //改变statusBar颜色而DrawerLayout依然可以显示在StatusBar
        // ctlMain.setStatusBarBackgroundColor(Config.vibrantColor);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);
        int[][] state = new int[][]{
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_checked}  // pressed
        };

        int[] color = new int[]{
                Color.BLACK,
                getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(SharePreferenceUtil.MUTED, ContextCompat.getColor(this, R.color.colorAccent))
        };
        navigationView.setItemTextColor(new ColorStateList(state, color));
        navigationView.setItemIconTintList(new ColorStateList(state, color));

        View headerLayout = navigationView.getHeaderView(0);
        LinearLayout llImage = (LinearLayout) headerLayout.findViewById(R.id.side_image);
        TextView imageDescription = (TextView) headerLayout.findViewById(R.id.image_description);
        if (new File(getFilesDir().getPath() + "/bg.jpg").exists()) {
            BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), getFilesDir().getPath() + "/bg.jpg");
            llImage.setBackground(bitmapDrawable);
            imageDescription.setText(getSharedPreferences(SharePreferenceUtil.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).getString(SharePreferenceUtil.IMAGE_DESCRIPTION, "我的愿望，就是希望你的愿望里，也有我"));
        }

        switchFragment(weixinFragment, getString(R.string.fragment_wexin_title));
        ZhihuRequest.getZhihuApi().getUpdateInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(final UpdateItem updateItem) {
                        LogUtils.i(updateItem.getDownloadUrl());
                        if (updateItem.getVersionCode() > BuildConfig.VERSION_CODE)
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(getString(R.string.update_title))
                                    .setMessage(String.format(getString(R.string.update_description), updateItem.getVersionName(), updateItem.getReleaseNote()))
                                    .setPositiveButton(getString(R.string.update_button), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(updateItem.getDownloadUrl())));
                                        }
                                    })
                                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void switchFragment(Fragment fragment, String title) {
        Slide slideTransition;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Gravity.START部分机型崩溃java.lang.IllegalArgumentException: Invalid slide direction
            slideTransition = new Slide(GravityCompat.START);
            slideTransition.setDuration(700);
            fragment.setEnterTransition(slideTransition);
            fragment.setExitTransition(slideTransition);
        }
        if (currentFragment == null || !currentFragment.getClass().getName().equals(fragment.getClass().getName())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.replace, fragment).commit();
            currentFragment = fragment;
            ActionBar actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setTitle(title);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_weixin) {
            switchFragment(weixinFragment, getString(R.string.fragment_wexin_title));
        } else if (id == R.id.nav_zhihu) {
            switchFragment(zhihuFragment, getString(R.string.fragment_zhihu_title));
        } else if (id == R.id.nav_it) {
            switchFragment(itHomeFragment, getString(R.string.fragment_it_title));
        } else if (id == R.id.nav_guokr) {
            switchFragment(guokrFragment, getString(R.string.fragment_guokr_title));
        } else if (id == R.id.nav_video) {
            switchFragment(videoFragment, getString(R.string.fragment_video_title));
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!rxSubscription.isUnsubscribed()) {
            rxSubscription.unsubscribe();
        }
    }
}
