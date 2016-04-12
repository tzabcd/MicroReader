package name.caiyao.microreader.bean.weiboVideo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YiuChoi on 2016/4/12 0012.
 */
public class WeiboVideoCards {
    //=mod/empty表示没有数据
    @SerializedName("mod_type")
    private String modType;
    @SerializedName("card_group")
    private WeiboViewCardGroup cardGroup;

    public String getModType() {
        return modType;
    }

    public void setModType(String modType) {
        this.modType = modType;
    }

    public WeiboViewCardGroup getCardGroup() {
        return cardGroup;
    }

    public void setCardGroup(WeiboViewCardGroup cardGroup) {
        this.cardGroup = cardGroup;
    }
}
