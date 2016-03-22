package name.caiyao.microreader.ui.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.ui.fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.fl_preference)
    FrameLayout flPreference;

    private SettingsFragment settingsFragment = new SettingsFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setToolBar(toolbar,true,true,false);
        getFragmentManager().beginTransaction().replace(R.id.fl_preference, settingsFragment).commit();
    }
}
