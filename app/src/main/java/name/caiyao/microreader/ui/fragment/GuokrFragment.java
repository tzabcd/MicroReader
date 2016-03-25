package name.caiyao.microreader.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import name.caiyao.microreader.api.guokr.GuokrRequest;
import name.caiyao.microreader.bean.guokr.GuokrHot;
import name.caiyao.microreader.bean.guokr.GuokrHotItem;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.ui.activity.ZhihuStoryActivity;
import name.caiyao.microreader.utils.CacheUtil;
import name.caiyao.microreader.utils.NetWorkUtil;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GuokrFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<GuokrHotItem> guokrHotItems = new ArrayList<>();
    private GuokrAdapter guokrAdapter;
    private CacheUtil cacheUtil;
    private int currentOffset;

    public GuokrFragment() {
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
        guokrAdapter = new GuokrAdapter(guokrHotItems);
        swipeTarget.setAdapter(guokrAdapter);
        cacheUtil = CacheUtil.get(getActivity());
        getFromCache(1);
        if (Config.isRefreshOnlyWifi(getActivity())) {
            if (NetWorkUtil.isWifiConnected(getActivity())) {
                onRefresh();
            } else {
                Toast.makeText(getActivity(), R.string.toast_wifi_refresh_data, Toast.LENGTH_SHORT).show();
            }
        } else {
            onRefresh();
        }
    }

    private void getFromCache(int offset) {
        if (cacheUtil.getAsJSONObject(CacheUtil.GUOKR + offset) != null) {
            GuokrHot guokrHot = new Gson().fromJson(cacheUtil.getAsJSONObject(CacheUtil.GUOKR + offset).toString(), GuokrHot.class);
            currentOffset++;
            guokrHotItems.addAll(guokrHot.getResult());
            guokrAdapter.notifyDataSetChanged();
        }
    }

    private void getGuokrHot(final int offset) {
        GuokrRequest.getGuokrApi().getGuokrHot(offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GuokrHot>() {
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
                        getFromCache(offset);
                        e.printStackTrace();
                        Snackbar.make(swipeToLoadLayout, getString(R.string.common_loading_error), Snackbar.LENGTH_SHORT).setAction(getString(R.string.comon_retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getGuokrHot(offset);
                            }
                        });
                    }

                    @Override
                    public void onNext(GuokrHot guokrHot) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.INVISIBLE);
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                            swipeToLoadLayout.setLoadingMore(false);
                        }
                        cacheUtil.put(CacheUtil.GUOKR + offset, new Gson().toJson(guokrHot));
                        currentOffset++;
                        guokrHotItems.addAll(guokrHot.getResult());
                        guokrAdapter.notifyDataSetChanged();
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
        currentOffset = 0;
        guokrHotItems.clear();
        getGuokrHot(currentOffset);
    }

    @Override
    public void onLoadMore() {
        getGuokrHot(currentOffset);
    }

    class GuokrAdapter extends RecyclerView.Adapter<GuokrAdapter.GuokrViewHolder> {

        private ArrayList<GuokrHotItem> guokrHotItems;

        public GuokrAdapter(ArrayList<GuokrHotItem> guokrHotItems) {
            this.guokrHotItems = guokrHotItems;
        }

        @Override
        public GuokrViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GuokrViewHolder(getActivity().getLayoutInflater().inflate(R.layout.guokr_item, parent, false));
        }

        @Override
        public void onBindViewHolder(final GuokrViewHolder holder, int position) {
            holder.tvTitle.setText(guokrHotItems.get(position).getTitle());
            holder.tvSummary.setText(guokrHotItems.get(position).getSummary());
            Glide.with(getActivity()).load(guokrHotItems.get(position).getSmall_image()).into(holder.ivGuokr);
            holder.cvGuokr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ZhihuStoryActivity.class);
                    intent.putExtra("type", ZhihuStoryActivity.TYPE_GUOKR);
                    intent.putExtra("id", guokrHotItems.get(holder.getAdapterPosition()).getId());
                    intent.putExtra("title", guokrHotItems.get(holder.getAdapterPosition()).getTitle());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return guokrHotItems.size();
        }

        public class GuokrViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.iv_guokr)
            ImageView ivGuokr;
            @Bind(R.id.tv_title)
            TextView tvTitle;
            @Bind(R.id.tv_summary)
            TextView tvSummary;
            @Bind(R.id.cv_guokr)
            CardView cvGuokr;

            public GuokrViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
