package name.caiyao.microreader.ui.activity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;

public class VideoActivity extends AppCompatActivity {

    @Bind(R.id.vv_gank)
    VideoView vvGank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        ButterKnife.bind(this);
        String url = getIntent().getStringExtra("url");
        vvGank.setVideoPath(url);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.show();
        progressDialog.setMessage(getString(R.string.common_loading));
        vvGank.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progressDialog.dismiss();
            }
        });
        vvGank.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Snackbar.make(vvGank, R.string.common_loading_error, Snackbar.LENGTH_SHORT).setAction(R.string.comon_retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        vvGank.start();
                    }
                });
                return true;
            }
        });
        final MediaController mediaController = new MediaController(this);
        vvGank.setMediaController(mediaController);
        vvGank.start();
    }

}
