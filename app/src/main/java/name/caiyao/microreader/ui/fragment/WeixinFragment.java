package name.caiyao.microreader.ui.fragment;

import android.content.Intent;
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
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.weixin.TxRequest;
import name.caiyao.microreader.bean.weixin.TxWeixinResponse;
import name.caiyao.microreader.bean.weixin.WeixinNews;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.ui.activity.WeixinNewsActivity;
import name.caiyao.microreader.utils.CacheUtil;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.ScreenUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WeixinFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {


    int currentPage = 1;
    WeixinAdapter weixinAdapter;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private CacheUtil cacheUtil;

    private ArrayList<WeixinNews> weixinNewses = new ArrayList<>();

    public WeixinFragment() {
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
        weixinAdapter = new WeixinAdapter(weixinNewses);
        swipeTarget.setAdapter(weixinAdapter);
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

//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
//                //Remove swiped item from list and notify the RecyclerView
//                LogUtils.i("滑动了：" + viewHolder.getAdapterPosition());
//                weixinNewses.remove(viewHolder.getAdapterPosition());
//                weixinAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
//            }
//
//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//                // 默认是操作ViewHolder的itemView，这里调用ItemTouchUIUtil的clearView方法传入指定的view
//                getDefaultUIUtil().onDraw(c, recyclerView, ((WeixinAdapter.WeixinViewHolder) viewHolder).cvMain, dX, dY, actionState, isCurrentlyActive);
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//        };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(swipeTarget);
    }

    private void getFromCache(int page) {
        if (cacheUtil.getAsJSONObject(CacheUtil.WEIXIN + page) != null) {
            TxWeixinResponse txWeixinResponse = new Gson().fromJson(cacheUtil.getAsJSONObject(CacheUtil.WEIXIN + page).toString(), TxWeixinResponse.class);
            cacheUtil.put(CacheUtil.WEIXIN + page, new Gson().toJson(txWeixinResponse));
            weixinNewses.addAll(txWeixinResponse.getNewslist());
            weixinAdapter.notifyDataSetChanged();
            currentPage++;
        }
    }

    private void getWeixinNews(final int page) {
        TxRequest.getTxApi().getWeixin(page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TxWeixinResponse>() {
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
                            getFromCache(page);
                            Snackbar.make(swipeTarget, getString(R.string.common_loading_error), Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getWeixinNews(page);
                                }
                            }).show();
                        }
                    }

                    @Override
                    public void onNext(TxWeixinResponse txWeixinResponse) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.INVISIBLE);
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                            swipeToLoadLayout.setLoadingMore(false);
                        }
                        if (txWeixinResponse.getCode() == 200) {
                            cacheUtil.put(CacheUtil.WEIXIN + page, new Gson().toJson(txWeixinResponse));
                            weixinNewses.addAll(txWeixinResponse.getNewslist());
                            weixinAdapter.notifyDataSetChanged();
                            currentPage++;
                        } else {
                            Snackbar.make(swipeTarget, getString(R.string.common_loading_error), Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getWeixinNews(page);
                                }
                            }).show();
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
        weixinNewses.clear();
        //2016-04-05修复Inconsistency detected. Invalid view holder adapter positionViewHolder
        weixinAdapter.notifyDataSetChanged();
        getWeixinNews(currentPage);
    }

    @Override
    public void onLoadMore() {
        getWeixinNews(currentPage);
    }

    class WeixinAdapter extends RecyclerView.Adapter<WeixinAdapter.WeixinViewHolder> {

        public ArrayList<WeixinNews> weixinNewses;

        public WeixinAdapter(ArrayList<WeixinNews> weixinNewses) {
            this.weixinNewses = weixinNewses;
        }

        @Override
        public WeixinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new WeixinViewHolder(getActivity().getLayoutInflater().inflate(R.layout.weixin_item, parent, false));
        }


        @Override
        public void onBindViewHolder(final WeixinViewHolder holder, int position) {
            holder.tvDescription.setText(weixinNewses.get(position).getDescription());
            holder.tvTitle.setText(weixinNewses.get(position).getTitle());
            holder.tvTime.setText(weixinNewses.get(position).getHottime());
            if (!TextUtils.isEmpty(weixinNewses.get(position).getPicUrl())) {
                Glide.with(getActivity()).load(weixinNewses.get(position).getPicUrl()).placeholder(R.drawable.bg).into(holder.ivWeixin);
            } else {
                holder.ivWeixin.setImageResource(R.drawable.bg);
            }
            runEnterAnimation(holder.itemView, position);
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WeixinNewsActivity.class);
                    intent.putExtra("url", weixinNewses.get(holder.getAdapterPosition()).getUrl());
                    intent.putExtra("title", weixinNewses.get(holder.getAdapterPosition()).getTitle());
                    startActivity(intent);
                }
            });
        }

        private void runEnterAnimation(View view, int position) {
            view.setTranslationY(ScreenUtil.getScreenHight(getActivity()));
            view.animate()
                    .translationY(0)
                    .setStartDelay(100 * (position % 5))
                    .setInterpolator(new DecelerateInterpolator(3.f))
                    .setDuration(700)
                    .start();
        }

        @Override
        public int getItemCount() {
            return weixinNewses.size();
        }

        public class WeixinViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.iv_weixin)
            ImageView ivWeixin;
            @Bind(R.id.tv_title)
            TextView tvTitle;
            @Bind(R.id.tv_time)
            TextView tvTime;
            @Bind(R.id.tv_description)
            TextView tvDescription;
            @Bind(R.id.cv_main)
            CardView cvMain;

            public WeixinViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
