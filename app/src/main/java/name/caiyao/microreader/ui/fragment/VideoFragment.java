package name.caiyao.microreader.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.util.UtilRequest;
import name.caiyao.microreader.api.weiboVideo.VideoRequest;
import name.caiyao.microreader.bean.gankio.GankVideo;
import name.caiyao.microreader.bean.weiboVideo.WeiboVideoBlog;
import name.caiyao.microreader.bean.weiboVideo.WeiboVideoResponse;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.ui.activity.VideoActivity;
import name.caiyao.microreader.ui.activity.VideoWebViewActivity;
import name.caiyao.microreader.utils.CacheUtil;
import name.caiyao.microreader.utils.NetWorkUtil;
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

    private ArrayList<WeiboVideoBlog> gankVideoItems = new ArrayList<>();
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
        videoAdapter = new VideoAdapter(gankVideoItems);
        swipeTarget.setAdapter(videoAdapter);
        cacheUtil = CacheUtil.get(getActivity());
        //getFromCache(1);
        if (Config.isRefreshOnlyWifi(getActivity())) {
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
        if (cacheUtil.getAsJSONObject(CacheUtil.VIDEO + page) != null) {
            GankVideo gankVideo = gson.fromJson(cacheUtil.getAsJSONObject(CacheUtil.VIDEO + page).toString(), GankVideo.class);
            currentPage++;
            //gankVideoItems.addAll(gankVideo.getResults());
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
                                if (w.getBlog().getmBlog() != null)
                                    w.setBlog(w.getBlog().getmBlog());
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
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                            swipeToLoadLayout.setLoadingMore(false);
                        }
                        if (progressBar != null)
                            progressBar.setVisibility(View.INVISIBLE);
                        if (swipeTarget != null) {
                            //getFromCache(page);
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
                        gankVideoItems.addAll(weiboVideoResponse);
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
        gankVideoItems.clear();
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
            String title = gankVideoItems.get(position).getBlog().getText();
            holder.tvTitle.setText(title.replaceAll("&[a-zA-Z]{1,10};", "").replaceAll(
                    "<[^>]*>", ""));
            holder.tvTime.setText(gankVideoItems.get(position).getBlog().getCreateTime());
            holder.cvVideo.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    return true;
                }
            });
            holder.cvVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoAdapter.this.getPlayUrl(holder);
                }
            });
        }

        private void getPlayUrl(final VideoViewHolder holder) {
            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getActivity().getString(R.string.fragment_video_get_url));
            progressDialog.show();
            UtilRequest.getUtilApi().getVideoUrl(gankVideoItems.get(holder.getAdapterPosition()).getBlog().getPageInfo().getVideoUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(),"视频解析失败！",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(gankVideoItems.get(holder.getAdapterPosition()).getBlog().getPageInfo().getVideoUrl())));
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            progressDialog.dismiss();
                            try {
                                String title, shareUrl;
                                Pattern pattern = Pattern.compile("href\\s*=\\s*(?:\"([^\"]*)\"|'([^']*)'|([^\"'>\\s]+)).*target=\"blank\">http");
                                final Matcher matcher = pattern.matcher(responseBody.string());
                                title = gankVideoItems.get(holder.getAdapterPosition()).getBlog().getPageInfo().getVideoUrl();
                                shareUrl = gankVideoItems.get(holder.getAdapterPosition()).getBlog().getPageInfo().getVideoUrl();
                                if (TextUtils.isEmpty(shareUrl)) {
                                    Toast.makeText(getActivity(), "播放地址为空", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                LogUtils.i(shareUrl);
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

            public VideoViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
