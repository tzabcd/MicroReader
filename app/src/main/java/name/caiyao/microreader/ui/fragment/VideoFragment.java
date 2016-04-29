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
import name.caiyao.microreader.bean.weiboVideo.WeiboVideoBlog;
import name.caiyao.microreader.presenter.IVideoPresenter;
import name.caiyao.microreader.presenter.impl.VideoPresenterImpl;
import name.caiyao.microreader.ui.adapter.VideoAdapter;
import name.caiyao.microreader.ui.iView.IVideoFragment;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;

public class VideoFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, IVideoFragment {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.swipe_target)
    RecyclerView swipeTarget;
    @BindView(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private Unbinder mUnbinder;

    private ArrayList<WeiboVideoBlog> mWeiboVideoBlogs = new ArrayList<>();
    private int currentPage = 1;
    private IVideoPresenter mIVideoPresenter;
    private VideoAdapter videoAdapter;

    public VideoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

    private void initData() {
        mIVideoPresenter = new VideoPresenterImpl(this, getActivity());
    }

    private void initView() {
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeTarget.setHasFixedSize(true);
        videoAdapter = new VideoAdapter(getActivity(), mWeiboVideoBlogs);
        swipeTarget.setAdapter(videoAdapter);
        mIVideoPresenter.getVideoFromCache(1);
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

    @Override
    public void onPause() {
        super.onPause();
        mIVideoPresenter.unsubcrible();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        mWeiboVideoBlogs.clear();
        videoAdapter.notifyDataSetChanged();
        mIVideoPresenter.getVideo(currentPage);
    }

    @Override
    public void onLoadMore() {
        mIVideoPresenter.getVideo(currentPage);
    }

    @Override
    public void showProgressDialog() {
        if (progressBar != null)
            progressBar.setVisibility(View.VISIBLE);
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
            mIVideoPresenter.getVideoFromCache(currentPage);
            Snackbar.make(swipeTarget, getString(R.string.common_loading_error) + error, Snackbar.LENGTH_SHORT).setAction("重试", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mIVideoPresenter.getVideo(currentPage);
                }
            }).show();
        }
    }

    @Override
    public void updateList(ArrayList<WeiboVideoBlog> weiboVideoBlogs) {
        currentPage++;
        mWeiboVideoBlogs.addAll(weiboVideoBlogs);
        videoAdapter.notifyDataSetChanged();
    }
}
