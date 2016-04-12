package name.caiyao.microreader.bean.weiboVideo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YiuChoi on 2016/4/12 0012.
 */
public class WeiboVideoMBlog {
    @SerializedName("created_at")
    private String createTime;
    @SerializedName("text")
    private String text;
    @SerializedName("page_info")
    private WeiboVideoPageInfo mPageInfo;
    @SerializedName("retweeted_status")
    private WeiboVideoMBlog mBlog;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public WeiboVideoPageInfo getPageInfo() {
        return mPageInfo;
    }

    public void setPageInfo(WeiboVideoPageInfo pageInfo) {
        mPageInfo = pageInfo;
    }

    public WeiboVideoMBlog getmBlog() {
        return mBlog;
    }

    public void setmBlog(WeiboVideoMBlog mBlog) {
        this.mBlog = mBlog;
    }
}
