package name.caiyao.microreader.ui.fragment;

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
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.bean.guokr.GuokrHotItem;
import name.caiyao.microreader.ui.view.LoaderMoreView;
import name.caiyao.microreader.ui.view.RefreshHeaderView;

public class GuokrFragment extends Fragment implements OnRefreshListener, OnLoadMoreListener {

    @Bind(R.id.swipe_refresh_header)
    RefreshHeaderView swipeRefreshHeader;
    @Bind(R.id.swipe_target)
    RecyclerView swipeTarget;
    @Bind(R.id.swipe_load_more_footer)
    LoaderMoreView swipeLoadMoreFooter;
    @Bind(R.id.swipeToLoadLayout)
    SwipeToLoadLayout swipeToLoadLayout;

    private ArrayList<GuokrHotItem> guokrHotItems = new ArrayList<>();
    private GuokrAdapter guokrAdapter;

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
    }

    private void getGuokrHot(){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

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
        public void onBindViewHolder(GuokrViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
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
                ButterKnife.bind(this,itemView);
            }
        }
    }
}
