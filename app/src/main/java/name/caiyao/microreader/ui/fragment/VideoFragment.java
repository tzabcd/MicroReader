package name.caiyao.microreader.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import name.caiyao.microreader.api.gankio.GankRequest;
import name.caiyao.microreader.api.util.UtilRequest;
import name.caiyao.microreader.bean.gankio.GankVideo;
import name.caiyao.microreader.bean.gankio.GankVideoItem;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.ui.activity.VideoActivity;
import name.caiyao.microreader.ui.activity.WeixinNewsActivity;
import name.caiyao.microreader.utils.CacheUtil;
import name.caiyao.microreader.utils.NetWorkUtil;
import okhttp3.ResponseBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class VideoFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private ArrayList<GankVideoItem> gankVideoItems = new ArrayList<>();
    private int currentPage=1;
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
        getFromCache(1);
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
            gankVideoItems.addAll(gankVideo.getResults());
            videoAdapter.notifyDataSetChanged();
        }
    }

    private void getVideo(final int page) {
        GankRequest.getGankApi().getVideoList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GankVideo>() {
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
                        e.printStackTrace();
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
                    public void onNext(GankVideo gankVideo) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.INVISIBLE);
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                            swipeToLoadLayout.setLoadingMore(false);
                        }
                        if (!gankVideo.isError()) {
                            cacheUtil.put(CacheUtil.VIDEO + page, gson.toJson(gankVideo));
                            currentPage++;
                            gankVideoItems.addAll(gankVideo.getResults());
                            videoAdapter.notifyDataSetChanged();
                        }
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

        private ArrayList<GankVideoItem> gankVideoItems;

        public VideoAdapter(ArrayList<GankVideoItem> gankVideoItems) {
            this.gankVideoItems = gankVideoItems;
        }

        @Override
        public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VideoViewHolder(getActivity().getLayoutInflater().inflate(R.layout.video_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final VideoViewHolder holder, int position) {
            holder.tvTitle.setText(gankVideoItems.get(position).getDesc());
            holder.tvTime.setText(gankVideoItems.get(position).getPublishedAt());
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
            UtilRequest.getUtilApi().getVideoUrl(gankVideoItems.get(holder.getAdapterPosition()).getUrl())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                            Snackbar.make(swipeTarget, getString(R.string.fragment_video_get_url_error), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getPlayUrl(holder);
                                }
                            }).show();
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            progressDialog.dismiss();
                            try {
                                Pattern pattern = Pattern.compile("target=\"blank\">(.*?mp4)</a>");
                                final Matcher matcher = pattern.matcher(responseBody.string());
                                if (matcher.find()) {
                                    startActivity(new Intent(getActivity(), VideoActivity.class)
                                            .putExtra("url", matcher.group(1))
                                            .putExtra("shareUrl", gankVideoItems.get(holder.getAdapterPosition()).getUrl())
                                            .putExtra("title",gankVideoItems.get(holder.getAdapterPosition()).getDesc()));
                                } else {
                                    startActivity(new Intent(getActivity(), WeixinNewsActivity.class)
                                            .putExtra("title", gankVideoItems.get(holder.getAdapterPosition()).getDesc())
                                            .putExtra("url", gankVideoItems.get(holder.getAdapterPosition()).getUrl()));
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
