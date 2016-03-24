package name.caiyao.microreader.bean.itHome;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by 蔡小木 on 2016/3/24 0024.
 */
@Root(name = "rss")
public class ItHomeResponse {
    @Element
    ItHomeChannel channel;

    @Attribute(name = "version")
    String version;

    public ItHomeChannel getChannel() {
        return channel;
    }

    public void setChannel(ItHomeChannel channel) {
        this.channel = channel;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
