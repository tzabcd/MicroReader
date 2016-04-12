package name.caiyao.microreader.bean.weiboVideo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by YiuChoi on 2016/4/12 0012.
 */
public class WeiboVideoResponse {
    @SerializedName("cards")
    private WeiboVideoCards cards;

    public WeiboVideoCards getCards() {
        return cards;
    }

    public void setCards(WeiboVideoCards cards) {
        this.cards = cards;
    }
}
