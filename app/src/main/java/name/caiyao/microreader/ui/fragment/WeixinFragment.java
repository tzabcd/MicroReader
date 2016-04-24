package name.caiyao.microreader.ui.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.weixin.WeixinNews;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.presenter.IWeixinPresenter;
import name.caiyao.microreader.presenter.impl.WeiXinPresenterImpl;
import name.caiyao.microreader.ui.activity.WeixinNewsActivity;
import name.caiyao.microreader.ui.iView.IWeixinFragment;
import name.caiyao.microreader.utils.DBUtils;
import name.caiyao.microreader.utils.NetWorkUtil;
import name.caiyao.microreader.utils.ScreenUtil;
import name.caiyao.microreader.utils.SharePreferenceUtil;

public class WeixinFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, IWeixinFragment {


    int currentPage = 1;
    WeixinAdapter weixinAdapter;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;

    private IWeixinPresenter mWeixinPresenter;

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
        initData();
        initView();
    }

    private void initView() {
        showProgressDialog();
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        swipeTarget.setLayoutManager(new LinearLayoutManager(getActivity()));
        swipeTarget.setHasFixedSize(true);
        weixinAdapter = new WeixinAdapter(weixinNewses);
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
            final WeixinNews weixinNews = weixinNewses.get(position);
            if (DBUtils.getDB(getActivity()).isRead(Config.WEIXIN, weixinNews.getUrl(), 1))
                holder.tvTitle.setTextColor(Color.GRAY);
            else
                holder.tvTitle.setTextColor(Color.BLACK);
            holder.tvDescription.setText(weixinNews.getDescription());
            holder.tvTitle.setText(weixinNews.getTitle());
            holder.tvTime.setText(weixinNews.getHottime());
            if (!TextUtils.isEmpty(weixinNews.getPicUrl())) {
                Glide.with(getActivity()).load(weixinNews.getPicUrl()).placeholder(R.drawable.bg).into(holder.ivWeixin);
            } else {
                holder.ivWeixin.setImageResource(R.drawable.bg);
            }
            holder.btnWeixin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), holder.btnWeixin);
                    popupMenu.getMenuInflater().inflate(R.menu.pop_menu, popupMenu.getMenu());
                    popupMenu.getMenu().removeItem(R.id.pop_fav);
                    final boolean isRead = DBUtils.getDB(getActivity()).isRead(Config.WEIXIN, weixinNews.getUrl(), 1);
                    if (!isRead)
                        popupMenu.getMenu().findItem(R.id.pop_unread).setTitle("标记为已读");
                    else
                        popupMenu.getMenu().findItem(R.id.pop_unread).setTitle("标记为未读");
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.pop_unread:
                                    if (isRead) {
                                        DBUtils.getDB(getActivity()).insertHasRead(Config.WEIXIN, weixinNews.getUrl(), 0);
                                        holder.tvTitle.setTextColor(Color.BLACK);
                                    } else {
                                        DBUtils.getDB(getActivity()).insertHasRead(Config.WEIXIN, weixinNews.getUrl(), 1);
                                        holder.tvTitle.setTextColor(Color.GRAY);
                                    }
                                    break;
                                case R.id.pop_share:
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    shareIntent.putExtra(Intent.EXTRA_TEXT, weixinNews.getTitle() + " " + weixinNews.getUrl() + getString(R.string.share_tail));
                                    shareIntent.setType("text/plain");
                                    //设置分享列表的标题，并且每次都显示分享列表
                                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            runEnterAnimation(holder.itemView, position);
            holder.cvMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DBUtils.getDB(getActivity()).insertHasRead(Config.WEIXIN, weixinNews.getUrl(), 1);
                    holder.tvTitle.setTextColor(Color.GRAY);
                    if (SharePreferenceUtil.isUseLocalBrowser(getActivity())) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(weixinNews.getUrl())));
                    } else {
                        Intent intent = new Intent(getActivity(), WeixinNewsActivity.class);
                        intent.putExtra("url", weixinNews.getUrl());
                        intent.putExtra("title", weixinNews.getTitle());
                        startActivity(intent);
                    }
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
            @Bind(R.id.btn_weixin)
            Button btnWeixin;

            public WeixinViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
