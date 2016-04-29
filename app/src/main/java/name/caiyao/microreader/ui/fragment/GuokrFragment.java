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

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.guokr.GuokrHotItem;
import name.caiyao.microreader.presenter.IGuokrPresenter;
import name.caiyao.microreader.presenter.impl.GuokrPresenterImpl;
import name.caiyao.microreader.ui.adapter.GuokrAdapter;
import name.caiyao.microreader.ui.iView.IGuokrFragment;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;

public class GuokrFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, IGuokrFragment {

    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private ArrayList<GuokrHotItem> guokrHotItems = new ArrayList<>();
    private GuokrAdapter guokrAdapter;
    private IGuokrPresenter mGuokrPresenter;
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
        initData();
        initView();
    }

    private void initData() {
        mGuokrPresenter = new GuokrPresenterImpl(this, getActivity());
    }

    private void initView() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeTarget.setHasFixedSize(true);
        guokrAdapter = new GuokrAdapter(guokrHotItems, getActivity());
        swipeTarget.setAdapter(guokrAdapter);
        mGuokrPresenter.getGuokrHotFromCache(0);
        if (SharePreferenceUtil.isRefreshOnlyWifi(getActivity())) {
            if (NetWorkUtil.isWifiConnected(getActivity())) {
                onRefresh();
            } else {
                Toast.makeText(getActivity(), R.string.toast_wifi_refresh_data, Toast.LENGTH_SHORT).show();
            }
        } else {
            onRefresh();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        mGuokrPresenter.unsubcrible();
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
        //2016-04-05修复Inconsistency detected. Invalid view holder adapter positionViewHolder
        guokrAdapter.notifyDataSetChanged();
        mGuokrPresenter.getGuokrHot(currentOffset);
    }

    @Override
    public void onLoadMore() {
        mGuokrPresenter.getGuokrHot(currentOffset);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidProgressDialog() {
        swipeToLoadLayout.setRefreshing(false);
        swipeToLoadLayout.setLoadingMore(false);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        mGuokrPresenter.getGuokrHotFromCache(currentOffset);
        Snackbar.make(swipeToLoadLayout, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_SHORT).setAction(getString(R.string.comon_retry), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGuokrPresenter.getGuokrHot(currentOffset);
            }
        }).show();
    }

    @Override
    public void updateList(ArrayList<GuokrHotItem> guokrHotItems) {
        currentOffset++;
        this.guokrHotItems.addAll(guokrHotItems);
        guokrAdapter.notifyDataSetChanged();
    }
}
