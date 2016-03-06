package name.caiyao.microreader.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.ZhihuRequest;
import name.caiyao.microreader.bean.ZhihuDaily;
import name.caiyao.microreader.bean.ZhihuStory;
import name.caiyao.microreader.ui.activity.WeixinNewsActivity;
import name.caiyao.microreader.ui.view.LoaderMoreView;
import name.caiyao.microreader.ui.view.RefreshHeaderView;
import name.caiyao.microreader.utils.TimeUtils;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ZhihuFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.swipe_refresh_header)
    RefreshHeaderView swipeRefreshHeader;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipe_load_more_footer)
    LoaderMoreView swipeLoadMoreFooter;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    String currentLoadedDate;
    ZhihuAdapter zhihuAdapter;
    ArrayList<ZhihuStory> zhihuStories = new ArrayList<>();

    public ZhihuFragment() {
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
        zhihuAdapter = new ZhihuAdapter(zhihuStories);
        swipeTarget.setAdapter(zhihuAdapter);
        getZhihuDaily();
    }


    private void getZhihuDaily() {
        zhihuStories.clear();
        ZhihuRequest.getZhihuApi().getLastDaily()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ZhihuDaily>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        swipeToLoadLayout.setRefreshing(false);
                        e.printStackTrace();
                        Snackbar.make(swipeTarget, "加载失败:" + e.toString(), Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getZhihuDaily();
                            }
                        }).show();
                    }

                    @Override
                    public void onNext(ZhihuDaily zhihuDaily) {
                        swipeToLoadLayout.setRefreshing(false);
                        currentLoadedDate = zhihuDaily.getDate();
                        zhihuStories.addAll(zhihuDaily.getStories());
                        zhihuAdapter.notifyDataSetChanged();
                    }
                });

    }

    private void getMoreZhihuDaily() {
        if (!TextUtils.isEmpty(currentLoadedDate)) {
            ZhihuRequest.getZhihuApi().getTheDaily(TimeUtils.getSpecifiedDayBefore(currentLoadedDate))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ZhihuDaily>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ZhihuDaily zhihuDaily) {
                            swipeToLoadLayout.setLoadingMore(false);
                            currentLoadedDate = zhihuDaily.getDate();
                            zhihuStories.addAll(zhihuDaily.getStories());
                            zhihuAdapter.notifyDataSetChanged();
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        getZhihuDaily();
    }

    @Override
    public void onLoadMore() {
        getMoreZhihuDaily();
    }

    class ZhihuAdapter extends RecyclerView.Adapter<ZhihuAdapter.ZhihuViewHolder> {


        private ArrayList<ZhihuStory> zhihuStories;

        public ZhihuAdapter(ArrayList<ZhihuStory> zhihuStories) {
            this.zhihuStories = zhihuStories;
        }

        @Override
        public ZhihuViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ZhihuViewHolder(getActivity().getLayoutInflater().inflate(R.layout.zhihu_daily_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ZhihuViewHolder holder, int position) {
            holder.tvZhihuDaily.setText(zhihuStories.get(position).getTitle());
            holder.cvZhihu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WeixinNewsActivity.class);
                }
            });
            Glide.with(getActivity()).load(zhihuStories.get(position).getImages()[0]).into(holder.ivZhihuDaily);
        }

        @Override
        public int getItemCount() {
            return zhihuStories.size();
        }

        public class ZhihuViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.iv_zhihu_daily)
            ImageView ivZhihuDaily;
            @Bind(R.id.tv_zhihu_daily)
            TextView tvZhihuDaily;
            @Bind(R.id.cv_zhihu)
            CardView cvZhihu;
            @Bind(R.id.tv_time)
            TextView tvTime;

            public ZhihuViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
