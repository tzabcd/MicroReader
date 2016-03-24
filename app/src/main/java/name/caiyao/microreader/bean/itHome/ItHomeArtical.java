package name.caiyao.microreader.bean.itHome;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by 蔡小木 on 2016/3/24 0024.
 */
@Root(name = "item")
public class ItHomeArtical {
    @Element
    private String newssource;
    @Element
    private String author;
    @Element
    private String detail;

    public String getNewssource() {
        return newssource;
    }

    public void setNewssource(String newssource) {
        this.newssource = newssource;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
