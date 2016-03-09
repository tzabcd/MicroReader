package name.caiyao.microreader.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.guokr.GuokrRequest;
import name.caiyao.microreader.bean.guokr.GuokrHot;
import name.caiyao.microreader.bean.guokr.GuokrHotItem;
import name.caiyao.microreader.ui.activity.ZhihuStoryActivity;
import name.caiyao.microreader.ui.view.LoaderMoreView;
import name.caiyao.microreader.ui.view.RefreshHeaderView;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class GuokrFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.swipe_refresh_header)
    RefreshHeaderView swipeRefreshHeader;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipe_load_more_footer)
    LoaderMoreView swipeLoadMoreFooter;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<GuokrHotItem> guokrHotItems = new ArrayList<>();
    private GuokrAdapter guokrAdapter;
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
        currentOffset = 0;
        getGuokrHot(currentOffset);
    }

    private void getGuokrHot(int offset) {
        GuokrRequest.getGuokrApi().getGuokrHot(offset)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GuokrHot>() {
                    @Override
                    public void onCompleted() {
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
                    public void onNext(GuokrHot guokrHot) {
                        if (swipeToLoadLayout != null) {//不加可能会崩溃
                            swipeToLoadLayout.setRefreshing(false);
                            swipeToLoadLayout.setLoadingMore(false);
                        }
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
