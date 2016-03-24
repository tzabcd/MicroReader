package name.caiyao.microreader.bean.itHome;

import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

/**
 * Created by 蔡小木 on 2016/3/24 0024.
 */
@Root(name = "channel")
public class ItHomeResponse {
    @ElementArray(name = "item")
    ItHomeItem[] item;

    public ItHomeItem[] getItem() {
        return item;
    }

    public void setItem(ItHomeItem[] item) {
        this.item = item;
    }
}
