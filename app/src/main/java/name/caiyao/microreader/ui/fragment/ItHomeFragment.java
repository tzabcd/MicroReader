package name.caiyao.microreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.itHome.ItHomeRequest;
import name.caiyao.microreader.bean.itHome.ItHomeItem;
import name.caiyao.microreader.bean.itHome.ItHomeResponse;
import name.caiyao.microreader.ui.view.LoaderMoreView;
import name.caiyao.microreader.ui.view.RefreshHeaderView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/3/24 0024.
 */
public class ItHomeFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener {


    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.swipe_refresh_header)
    RefreshHeaderView swipeRefreshHeader;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipe_load_more_footer)
    LoaderMoreView swipeLoadMoreFooter;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private ArrayList<ItHomeItem> itHomeItems = new ArrayList<>();
    private ItAdapter itAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        itAdapter = new ItAdapter(itHomeItems);
        swipeTarget.setAdapter(itAdapter);
        getIthomeNews();
    }

    private void getIthomeNews() {
        progressBar.setVisibility(View.VISIBLE);
        ItHomeRequest.getItHomeApi().getItHomeNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ItHomeResponse>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if (progressBar != null)
                            progressBar.setVisibility(View.INVISIBLE);
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void onNext(ItHomeResponse itHomeResponse) {
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                        }
                        if (progressBar != null)
                            progressBar.setVisibility(View.INVISIBLE);
                        itHomeItems.addAll(itHomeResponse.getChannel().getItems());
                        itAdapter.notifyDataSetChanged();
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
        itHomeItems.clear();
        getIthomeNews();
    }

    @Override
    public void onLoadMore() {

    }

    class ItAdapter extends RecyclerView.Adapter<ItAdapter.ItViewHolder> {

        private ArrayList<ItHomeItem> itHomeItems;

        public ItAdapter(ArrayList<ItHomeItem> itHomeItems) {
            this.itHomeItems = itHomeItems;
        }

        @Override
        public ItViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItViewHolder(getActivity().getLayoutInflater().inflate(R.layout.ithome_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ItViewHolder holder, int position) {
            holder.tvTitle.setText(itHomeItems.get(position).getTitle());
            holder.tvTime.setText(itHomeItems.get(position).getPostdate());
            holder.tvDescription.setText(itHomeItems.get(position).getDescription());
            Glide.with(getActivity()).load(itHomeItems.get(position).getImage()).into(holder.ivIthome);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return itHomeItems.size();
        }

        public class ItViewHolder extends RecyclerView.ViewHolder {

            @Bind(R.id.iv_ithome)
            ImageView ivIthome;
            @Bind(R.id.tv_title)
            TextView tvTitle;
            @Bind(R.id.tv_description)
            TextView tvDescription;
            @Bind(R.id.tv_time)
            TextView tvTime;

            public ItViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
