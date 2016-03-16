package name.caiyao.microreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.orhanobut.logger.Logger;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.MediaPlayerWrapper;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

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
    private int currentPage;
    private VideoAdapter videoAdapter;
    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });

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
        currentPage = 1;
        gankVideoItems.clear();
        getVideo(currentPage);
    }

    private void getVideo(int page) {
        GankRequest.getGankApi().getVideoList(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GankVideo>() {
                    @Override
                    public void onCompleted() {
                        if (progressBar != null)
                            progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                            swipeToLoadLayout.setLoadingMore(false);
                        }
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(GankVideo gankVideo) {
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                            swipeToLoadLayout.setLoadingMore(false);
                        }
                        if (!gankVideo.getError()) {
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
        mVideoPlayerManager.resetMediaPlayer();
        currentPage = 1;
        gankVideoItems.clear();
        getVideo(currentPage);
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoPlayerManager.resetMediaPlayer();
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
            holder.pbVideo.setVisibility(View.INVISIBLE);
            holder.ivVideo.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(gankVideoItems.get(position).getDesc());
            holder.tvTime.setText(gankVideoItems.get(position).getPublishedAt());
            holder.vpvVideo.addMediaPlayerListener(new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
                @Override
                public void onVideoSizeChangedMainThread(int width, int height) {

                }

                @Override
                public void onVideoPreparedMainThread() {
                    holder.ivVideo.setVisibility(View.INVISIBLE);
                    holder.pbVideo.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onVideoCompletionMainThread() {
                    holder.ivVideo.setVisibility(View.VISIBLE);
                    holder.pbVideo.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onErrorMainThread(int what, int extra) {
                    holder.ivVideo.setVisibility(View.VISIBLE);
                    holder.pbVideo.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onBufferingUpdateMainThread(int percent) {
                    holder.pbVideo.setProgress(percent);
                }

                @Override
                public void onVideoStoppedMainThread() {
                    holder.ivVideo.setVisibility(View.VISIBLE);
                    holder.pbVideo.setVisibility(View.INVISIBLE);
                }
            });

            holder.ivVideo.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    holder.pbVideo.setVisibility(View.VISIBLE);
                    holder.ivVideo.setVisibility(View.INVISIBLE);
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
                                }

                                @Override
                                public void onNext(ResponseBody responseBody) {
                                    try {
                                        Pattern pattern = Pattern.compile("target=\"blank\">(.*?mp4)</a>");
                                        final Matcher matcher = pattern.matcher(responseBody.string());
                                        if (matcher.find()) {
                                            Logger.i(matcher.group(1));
                                            mVideoPlayerManager.playNewVideo(null, holder.vpvVideo, matcher.group(1));
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
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
            @Bind(R.id.vpv_video)
            VideoPlayerView vpvVideo;
            @Bind(R.id.iv_video)
            ImageView ivVideo;
            @Bind(R.id.pb_video)
            ProgressBar pbVideo;
            @Bind(R.id.cv_video)
            CardView cvVideo;

            public VideoViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
