package name.caiyao.microreader.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import name.caiyao.microreader.R;
import name.caiyao.microreader.config.Config;

/**
 * Created by 蔡小木 on 2016/4/27 0027.
 */
public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_CHANNEL_HEADER = 0;
    private static final int TYPE_CHANNEL = 1;


    private List<Config.Channel> mSavedChannel, mOtherChannelItems;
    private Context mContext;
    private ItemTouchHelper mItemTouchHelper;

    public ChannelAdapter(Context context, ItemTouchHelper itemTouchHelper, ArrayList<Config.Channel> savedChannel, ArrayList<Config.Channel> otherChannelItems) {
        this.mContext = context;
        this.mItemTouchHelper = itemTouchHelper;
        this.mSavedChannel = savedChannel;
        this.mOtherChannelItems = otherChannelItems;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_CHANNEL_HEADER;
        } else if (position == mSavedChannel.size() + 1) {
            return TYPE_CHANNEL_HEADER;
        } else {
            return TYPE_CHANNEL;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_CHANNEL_HEADER:
                view = LayoutInflater.from(mContext).inflate(R.layout.channel_header, parent, false);
                return new SavedChannelHeaderViewHolder(view);
            case TYPE_CHANNEL:
                view = LayoutInflater.from(mContext).inflate(R.layout.saved_channel, parent, false);
                return new SavedChannelViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SavedChannelHeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_channel_header)
        TextView mTvChannelHeader;

        public SavedChannelHeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }
    }

    class SavedChannelViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.iv_channel)
        ImageView mIvChannel;
        @Bind(R.id.tv_channel)
        TextView mTvChannel;

        public SavedChannelViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(itemView);
        }
    }
}
