package name.caiyao.microreader.bean.itHome;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by 蔡小木 on 2016/3/24 0024.
 */
@Root(name = "item")
public class ItHomeItem {
    @Element
    private String newsid;
    @Element
    private String title;
    @Element(name = "c", required = false)
    private String c;
    @Element(required = false)
    private String v;
    @Element
    private String url;
    @Element
    private String postdate;
    @Element
    private String image;
    @Element
    private String description;
    @Element(required = false)
    private int hitcount;
    @Element(required = false)
    private int commentcount;
    @Element(required = false)
    private String forbidcomment;
    @Element(required = false)
    private String tags;

    public String getNewsid() {
        return newsid;
    }

    public void setNewsid(String newsid) {
        this.newsid = newsid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPostdate() {
        return postdate;
    }

    public void setPostdate(String postdate) {
        this.postdate = postdate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
