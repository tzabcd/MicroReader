package name.caiyao.microreader.bean.guokr;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 *
 */
public class GuokrHotItem   {
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("small_image")
    private String mSmallImage;
    @SerializedName("summary")
    private String summary;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmallImage() {
        return mSmallImage;
    }

    public void setSmallImage(String smallImage) {
        this.mSmallImage = smallImage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
