package name.caiyao.microreader.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.ui.fragment.GuokrFragment;
import name.caiyao.microreader.ui.fragment.ItHomeFragment;
import name.caiyao.microreader.ui.fragment.VideoFragment;
import name.caiyao.microreader.ui.fragment.WeixinFragment;
import name.caiyao.microreader.ui.fragment.ZhihuFragment;
import name.caiyao.microreader.utils.SharePreferenceUtil;

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
        setToolBar(toolbar, true, true, true);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
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
        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this)
                .setUpdateFrom(UpdateFrom.XML)
                .setUpdateXML("http://caiyao.name/releases/update.xml")
                .withListener(new AppUpdaterUtils.UpdateListener() {
                    @Override
                    public void onSuccess(Update update, Boolean isUpdateAvailable) {
                        if (isUpdateAvailable){
                            AppUpdater appUpdater = new AppUpdater(MainActivity.this);
                            appUpdater.setDialogTitleWhenUpdateAvailable(getString(R.string.update_title))
                                    .setDialogDescriptionWhenUpdateAvailable(String.format(getString(R.string.update_description),update.getLatestVersion()))
                                    .setDialogButtonUpdate(getString(R.string.update_button))
                                    .setDialogButtonDoNotShowAgain(getString(R.string.update_not_show))
                                    .setDialogTitleWhenUpdateNotAvailable(getString(R.string.update_no_update))
                                    .setDialogDescriptionWhenUpdateNotAvailable(getString(R.string.update_no_update_description));
                            appUpdater.setUpdateFrom(UpdateFrom.XML).showAppUpdated(true)
                                    .setUpdateXML("http://caiyao.name/releases/update.xml");
                            appUpdater.start();
                        }
                    }

                    @Override
                    public void onFailed(AppUpdaterError error) {
                    }
                });
        appUpdaterUtils.start();
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
            slideTransition = new Slide(Gravity.START);
            slideTransition.setDuration(700);
            fragment.setEnterTransition(slideTransition);
            fragment.setExitTransition(slideTransition);
        }
        if (currentFragment == null || !currentFragment.getClass().getName().equals(fragment.getClass().getName())) {
            getSupportFragmentManager().beginTransaction().replace(R.id.replace, fragment).commit();
            currentFragment = fragment;
            getSupportActionBar().setTitle(title);
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
}
