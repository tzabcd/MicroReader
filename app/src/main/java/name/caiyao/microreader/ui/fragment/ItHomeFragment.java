package name.caiyao.microreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.itHome.ItHomeItem;
import name.caiyao.microreader.presenter.IItHomePresenter;
import name.caiyao.microreader.presenter.impl.ItHomePresenterImpl;
import name.caiyao.microreader.ui.adapter.ItAdapter;
import name.caiyao.microreader.ui.iView.IItHomeFragment;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;

/**
 * Created by 蔡小木 on 2016/3/24 0024.
 */
public class ItHomeFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, IItHomeFragment {


    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.swipe_target)
    RecyclerView swipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private Unbinder mUnbinder;

    private ArrayList<ItHomeItem> itHomeItems = new ArrayList<>();
    private ItAdapter itAdapter;
    private IItHomePresenter mItHomePresenter;
    private String currentNewsId = "0";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initView() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeTarget.setHasFixedSize(true);
        itAdapter = new ItAdapter(getActivity(), itHomeItems);
        swipeTarget.setAdapter(itAdapter);
        mItHomePresenter.getNewsFromCache();
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

    private void initData() {
        mItHomePresenter = new ItHomePresenterImpl(this, getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        mItHomePresenter.unsubcrible();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onRefresh() {
        currentNewsId = "0";
        itHomeItems.clear();
        //2016-04-05修复Inconsistency detected. Invalid view holder adapter positionViewHolder
        itAdapter.notifyDataSetChanged();
        mItHomePresenter.getNewItHomeNews();
    }

    @Override
    public void onLoadMore() {
        mItHomePresenter.getMoreItHomeNews(currentNewsId);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
        if (swipeToLoadLayout != null) {//不加可能会崩溃
            swipeToLoadLayout.setRefreshing(false);
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    @Override
    public void showError(String error) {
        Snackbar.make(swipeToLoadLayout, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_SHORT).setAction(getString(R.string.comon_retry), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentNewsId.equals("0")) {
                    mItHomePresenter.getNewItHomeNews();
                } else {
                    mItHomePresenter.getMoreItHomeNews(currentNewsId);
                }
            }
        }).show();
    }

    @Override
    public void updateList(ArrayList<ItHomeItem> itHomeItems) {
        currentNewsId = itHomeItems.get(itHomeItems.size() - 1).getNewsid();
        this.itHomeItems.addAll(itHomeItems);
        itAdapter.notifyDataSetChanged();
    }
}
