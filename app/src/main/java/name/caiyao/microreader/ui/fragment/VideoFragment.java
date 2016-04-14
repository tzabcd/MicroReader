package name.caiyao.microreader.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.apkfuns.logutils.LogUtils;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.util.UtilRequest;
import name.caiyao.microreader.api.weiboVideo.VideoRequest;
import name.caiyao.microreader.bean.weiboVideo.WeiboVideoBlog;
import name.caiyao.microreader.bean.weiboVideo.WeiboVideoResponse;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.ui.activity.VideoActivity;
import name.caiyao.microreader.ui.activity.VideoWebViewActivity;
import name.caiyao.microreader.utils.CacheUtil;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;
import okhttp3.ResponseBody;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class VideoFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private ArrayList<WeiboVideoBlog> mWeiboVideoBlogs = new ArrayList<>();
    private int currentPage = 1;
    CacheUtil cacheUtil;
    Gson gson = new Gson();
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
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeTarget.setHasFixedSize(true);
        videoAdapter = new VideoAdapter(mWeiboVideoBlogs);
        swipeTarget.setAdapter(videoAdapter);
        cacheUtil = CacheUtil.get(getActivity());
        getFromCache(1);
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

    private void getFromCache(int page) {
        if (cacheUtil.getAsJSONArray(Config.VIDEO + page) != null) {
            ArrayList<WeiboVideoBlog> video = gson.fromJson(cacheUtil.getAsJSONArray(Config.VIDEO + page).toString(), new TypeToken<ArrayList<WeiboVideoBlog>>() {
            }.getType());
            currentPage++;
            mWeiboVideoBlogs.addAll(video);
            videoAdapter.notifyDataSetChanged();
        }
    }

    private void getVideo(final int page) {
        VideoRequest.getVideoRequstApi().getWeiboVideo(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<WeiboVideoResponse, ArrayList<WeiboVideoBlog>>() {
                    @Override
                    public ArrayList<WeiboVideoBlog> call(WeiboVideoResponse weiboVideoResponse) {
                        ArrayList<WeiboVideoBlog> arrayList = new ArrayList<>();
                        if (!weiboVideoResponse.getCardsItems()[0].getModType().equals("mod/empty")) {
                            ArrayList<WeiboVideoBlog> a = weiboVideoResponse.getCardsItems()[0].getBlogs();
                            for (WeiboVideoBlog w : a) {
                                if (w.getBlog().getmBlog() != null)//处理转发的微博
                                    w.setBlog(w.getBlog().getmBlog());
                                if (w.getBlog().getPageInfo() != null && !TextUtils.isEmpty(w.getBlog().getPageInfo().getVideoPic()))//处理无视频微博
                                    arrayList.add(w);
                            }
                        } else {
                            LogUtils.i("没有数据了！");
                        }
                        return arrayList;
                    }
                })
                .subscribe(new Subscriber<ArrayList<WeiboVideoBlog>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                            swipeToLoadLayout.setLoadingMore(false);
                        }
                        if (progressBar != null)
                            progressBar.setVisibility(View.INVISIBLE);
                        if (swipeTarget != null) {
                            getFromCache(page);
                            Snackbar.make(swipeTarget, getString(R.string.common_loading_error), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getVideo(page);
                                }
                            }).show();
                        }
                    }

                    @Override
                    public void onNext(ArrayList<WeiboVideoBlog> weiboVideoResponse) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.INVISIBLE);
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                            swipeToLoadLayout.setLoadingMore(false);
                        }
                        cacheUtil.put(Config.VIDEO + page, gson.toJson(weiboVideoResponse));
                        mWeiboVideoBlogs.addAll(weiboVideoResponse);
                        videoAdapter.notifyDataSetChanged();
                        currentPage++;
                    }
                });
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
        //2016-04-05修复Inconsistency detected. Invalid view holder adapter positionViewHolder
        videoAdapter.notifyDataSetChanged();
        getVideo(currentPage);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onLoadMore() {
        getVideo(currentPage);
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
            String title = weiboVideoBlog.getBlog().getText();
            Glide.with(getActivity()).load(weiboVideoBlog.getBlog().getPageInfo().getVideoPic()).into(holder.mIvVideo);
            holder.tvTitle.setText(title.replaceAll("&[a-zA-Z]{1,10};", "").replaceAll(
                    "<[^>]*>", ""));
            holder.tvTime.setText(weiboVideoBlog.getBlog().getCreateTime());
            holder.btnVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), holder.btnVideo);
                    popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
            holder.cvVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoAdapter.this.getPlayUrl(weiboVideoBlog);
                }
            });
        }

        private void getPlayUrl(final WeiboVideoBlog weiboVideoBlog) {
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
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "视频解析失败！", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(weiboVideoBlog.getBlog().getPageInfo().getVideoUrl())));
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            progressDialog.dismiss();
                            try {
                                String title, shareUrl;
                                Pattern pattern = Pattern.compile("href\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\"'>\\s]+)).*target=\"blank\">http");
                                final Matcher matcher = pattern.matcher(responseBody.string());
                                title = weiboVideoBlog.getBlog().getText();
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
                                    startActivity(new Intent(getActivity(), VideoWebViewActivity.class)
                                            .putExtra("url", matcher.group(1)));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
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
