package name.caiyao.microreader.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.util.UtilRequest;
import name.caiyao.microreader.bean.weiboVideo.WeiboVideoBlog;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.presenter.IVideoPresenter;
import name.caiyao.microreader.presenter.impl.VideoPresenterImpl;
import name.caiyao.microreader.ui.activity.VideoActivity;
import name.caiyao.microreader.ui.activity.VideoWebViewActivity;
import name.caiyao.microreader.ui.iView.IVideoFragment;
import name.caiyao.microreader.utils.DBUtils;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;
import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, IVideoFragment {

    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private ArrayList<WeiboVideoBlog> mWeiboVideoBlogs = new ArrayList<>();
    private int currentPage = 1;
    private IVideoPresenter mIVideoPresenter;
    private VideoAdapter videoAdapter;

    public VideoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initData() {
        mIVideoPresenter = new VideoPresenterImpl(this, getActivity());
    }

    private void initView() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeTarget.setHasFixedSize(true);
        videoAdapter = new VideoAdapter(mWeiboVideoBlogs);
        swipeTarget.setAdapter(videoAdapter);
        mIVideoPresenter.getVideoFromCache(1);
        if (SharePreferenceUtil.isRefreshOnlyWifi(getActivity())) {
            if (NetWorkUtil.isWifiConnected(getActivity())) {
                onRefresh();
            } else {
                Toast.makeText(getActivity(), getString(R.string.toast_wifi_refresh_data), Toast.LENGTH_SHORT).show();
            }
        } else {
            onRefresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        mWeiboVideoBlogs.clear();
        mIVideoPresenter.getVideo(currentPage);
    }

    @Override
    public void onLoadMore() {
        mIVideoPresenter.getVideo(currentPage);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgressDialog() {
        if (swipeToLoadLayout != null) {//不加可能会崩溃
            swipeToLoadLayout.setRefreshing(false);
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        if (swipeTarget != null) {
            mIVideoPresenter.getVideoFromCache(currentPage);
            Snackbar.make(swipeTarget, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIVideoPresenter.getVideo(currentPage);
                }
            }).show();
        }
    }

    @Override
    public void updateList(ArrayList<WeiboVideoBlog> weiboVideoBlogs) {
        currentPage++;
        mWeiboVideoBlogs.addAll(weiboVideoBlogs);
        videoAdapter.notifyDataSetChanged();
    }

    class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

        private ArrayList<WeiboVideoBlog> gankVideoItems;

        public VideoAdapter(ArrayList<WeiboVideoBlog> gankVideoItems) {
            this.gankVideoItems = gankVideoItems;
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VideoViewHolder(getActivity().getLayoutInflater().inflate(R.layout.video_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final VideoViewHolder holder, int position) {
            final WeiboVideoBlog weiboVideoBlog = gankVideoItems.get(position);
            final String title = weiboVideoBlog.getBlog().getText().replaceAll("&[a-zA-Z]{1,10};", "").replaceAll(
                    "<[^>]*>", "");
            if (DBUtils.getDB(getActivity()).isRead(Config.VIDEO, weiboVideoBlog.getBlog().getPageInfo().getVideoUrl(), 1))
                holder.tvTitle.setTextColor(Color.GRAY);
            else
                holder.tvTitle.setTextColor(Color.BLACK);
            Glide.with(getActivity()).load(weiboVideoBlog.getBlog().getPageInfo().getVideoPic()).into(holder.mIvVideo);
            holder.tvTitle.setText(title);
            holder.tvTime.setText(weiboVideoBlog.getBlog().getCreateTime());
            holder.btnVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), holder.btnVideo);
                    popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                    popupMenu.getMenu().removeItem(R.id.pop_fav);
                    final boolean isRead = DBUtils.getDB(getActivity()).isRead(Config.VIDEO, weiboVideoBlog.getBlog().getPageInfo().getVideoUrl(), 1);
                    if (!isRead)
                        popupMenu.getMenu().findItem(R.id.pop_unread).setTitle("标记为已读");
                    else
                        popupMenu.getMenu().findItem(R.id.pop_unread).setTitle("标记为未读");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.pop_unread:
                                    if (isRead) {
                                        DBUtils.getDB(getActivity()).insertHasRead(Config.VIDEO, weiboVideoBlog.getBlog().getPageInfo().getVideoUrl(), 0);
                                        holder.tvTitle.setTextColor(Color.BLACK);
                                    } else {
                                        DBUtils.getDB(getActivity()).insertHasRead(Config.VIDEO, weiboVideoBlog.getBlog().getPageInfo().getVideoUrl(), 1);
                                        holder.tvTitle.setTextColor(Color.GRAY);
                                    }
                                    break;
                                case R.id.pop_share:
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + weiboVideoBlog.getBlog().getPageInfo().getVideoUrl() + getString(R.string.share_tail));
                                    shareIntent.setType("text/plain");
                                    //设置分享列表的标题，并且每次都显示分享列表
                                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            holder.cvVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBUtils.getDB(getActivity()).insertHasRead(Config.VIDEO, weiboVideoBlog.getBlog().getPageInfo().getVideoUrl(), 1);
                    holder.tvTitle.setTextColor(Color.GRAY);
                    VideoAdapter.this.getPlayUrl(weiboVideoBlog, title);
                }
            });
        }

        private void getPlayUrl(final WeiboVideoBlog weiboVideoBlog, final String title) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getActivity().getString(R.string.fragment_video_get_url));
            progressDialog.show();
            UtilRequest.getUtilApi().getVideoUrl(weiboVideoBlog.getBlog().getPageInfo().getVideoUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                Toast.makeText(getActivity(), "视频解析失败！", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(weiboVideoBlog.getBlog().getPageInfo().getVideoUrl())));
                            }
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            //防止停止后继续执行
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                                try {
                                    String shareUrl;
                                    Pattern pattern = Pattern.compile("href\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\"'>\\s]+)).*target=\"blank\">http");
                                    final Matcher matcher = pattern.matcher(responseBody.string());
                                    shareUrl = weiboVideoBlog.getBlog().getPageInfo().getVideoUrl();
                                    if (TextUtils.isEmpty(shareUrl)) {
                                        Toast.makeText(getActivity(), "播放地址为空", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (matcher.find() && matcher.group(1).endsWith(".mp4")) {
                                        startActivity(new Intent(getActivity(), VideoActivity.class)
                                                .putExtra("url", matcher.group(1))
                                                .putExtra("shareUrl", shareUrl)
                                                .putExtra("title", title));
                                    } else {
                                        if (SharePreferenceUtil.isUseLocalBrowser(getActivity()))
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(matcher.group(1))));
                                        else
                                            startActivity(new Intent(getActivity(), VideoWebViewActivity.class)
                                                    .putExtra("url", matcher.group(1)));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        @Override
        public int getItemCount() {
            return gankVideoItems.size();
        }

        public class VideoViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.tv_title)
            TextView tvTitle;
            @Bind(R.id.tv_time)
            TextView tvTime;
            @Bind(R.id.cv_video)
            CardView cvVideo;
            @Bind(R.id.iv_video)
            ImageView mIvVideo;
            @Bind(R.id.btn_video)
            Button btnVideo;

            public VideoViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
