package name.caiyao.microreader.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.srx.widget.PullCallback;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.api.TxRequest;
import name.caiyao.microreader.bean.TxWeixinResponse;
import name.caiyao.microreader.bean.WeixinNews;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WeixinFragment extends Fragment {


    int currentPage = 1;
    boolean isLoading = false;
    WeixinAdapter weixinAdapter;
    @Bind(R.id.rv_weixin)
    RecyclerView rvWeixin;

    private ArrayList<WeixinNews> weixinNewses = new ArrayList<>();

    public WeixinFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weixin, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvWeixin.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        weixinAdapter = new WeixinAdapter(weixinNewses);
        rvWeixin.setAdapter(weixinAdapter);
        plvWeixin.isLoadMoreEnabled(true);
        plvWeixin.setPullCallback(new PullCallback() {
            @Override
            public void onLoadMore() {
                getWeixinNews(currentPage);
            }

            @Override
            public void onRefresh() {
                currentPage = 1;
                weixinNewses.clear();
                getWeixinNews(currentPage);
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return false;
            }
        });
        plvWeixin.initLoad();
    }

    private void getWeixinNews(final int page) {
        isLoading = true;
        TxRequest.getTxApi().getWeixin(page).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<TxWeixinResponse>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        isLoading = false;
                        Snackbar.make(plvWeixin, e.toString(), Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                getWeixinNews(page);
                            }
                        });
                    }

                    @Override
                    public void onNext(TxWeixinResponse txWeixinResponse) {
                        plvWeixin.setComplete();
                        isLoading = false;
                        if (txWeixinResponse.getCode() == 200) {
                            weixinNewses.addAll(txWeixinResponse.getNewslist());
                            weixinAdapter.notifyDataSetChanged();
                            //currentPage++;
                        } else {
                            Snackbar.make(plvWeixin, "获取失败！", Snackbar.LENGTH_INDEFINITE).setAction("重试", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getWeixinNews(page);
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
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
        public void onBindViewHolder(WeixinViewHolder holder, int position) {
            Glide.with(getActivity()).load(weixinNewses.get(position).getPicUrl()).into(holder.ivWeixin);
            holder.tvTitle.setText(weixinNewses.get(position).getTitle());
            holder.tvTime.setText(weixinNewses.get(position).getHottime());
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

            public WeixinViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
