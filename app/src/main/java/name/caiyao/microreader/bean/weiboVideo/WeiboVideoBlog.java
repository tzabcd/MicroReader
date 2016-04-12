package name.caiyao.microreader.bean.weiboVideo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YiuChoi on 2016/4/12 0012.
 */
public class WeiboVideoBlog {
    @SerializedName("mblog")
    private WeiboVideoMBlog mBlog;

    public WeiboVideoMBlog getBlog() {
        return mBlog;
    }

    public void setBlog(WeiboVideoMBlog blog) {
        mBlog = blog;
    }
}
