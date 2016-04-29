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
import name.caiyao.microreader.bean.weixin.WeixinNews;
import name.caiyao.microreader.presenter.IWeixinPresenter;
import name.caiyao.microreader.presenter.impl.WeiXinPresenterImpl;
import name.caiyao.microreader.ui.adapter.WeixinAdapter;
import name.caiyao.microreader.ui.iView.IWeixinFragment;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;

public class WeixinFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, IWeixinFragment {


    WeixinAdapter weixinAdapter;
    @BindView(R.id.swipe_target)
    RecyclerView swipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private Unbinder mUnbinder;

    private IWeixinPresenter mWeixinPresenter;

    private ArrayList<WeixinNews> weixinNewses = new ArrayList<>();

    private int currentPage = 1;

    public WeixinFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, container, false);
        mUnbinder= ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWeixinPresenter.unsubcrible();
    }

    private void initView() {
        showProgressDialog();
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeTarget.setHasFixedSize(true);
        weixinAdapter = new WeixinAdapter(getActivity(),weixinNewses);
        swipeTarget.setAdapter(weixinAdapter);
        mWeixinPresenter.getWeixinNews(1);
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
        mWeixinPresenter = new WeiXinPresenterImpl(this, getActivity());
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        weixinNewses.clear();
        //2016-04-05修复Inconsistency detected. Invalid view holder adapter positionViewHolder
        weixinAdapter.notifyDataSetChanged();
        mWeixinPresenter.getWeixinNews(currentPage);
    }

    @Override
    public void onLoadMore() {
        mWeixinPresenter.getWeixinNews(currentPage);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void hidProgressDialog() {
        if (swipeToLoadLayout != null) {//不加可能会崩溃
            swipeToLoadLayout.setRefreshing(false);
            swipeToLoadLayout.setLoadingMore(false);
        }
        if (progressBar != null)
            progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showError(String error) {
        if (swipeTarget != null) {
            mWeixinPresenter.getWeixinNewsFromCache(currentPage);
            Snackbar.make(swipeTarget, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mWeixinPresenter.getWeixinNews(currentPage);
                }
            }).show();
        }
    }

    @Override
    public void updateList(ArrayList<WeixinNews> weixinNewsesList) {
        currentPage++;
        weixinNewses.addAll(weixinNewsesList);
        weixinAdapter.notifyDataSetChanged();
    }
}
