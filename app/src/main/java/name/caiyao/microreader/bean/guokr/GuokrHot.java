package name.caiyao.microreader.bean.guokr;


import java.util.ArrayList;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 */
public class GuokrHot {
    private String now;
    private boolean ok = false;
    private int limit;
    private ArrayList<GuokrHotItem> result;
    private int offset;
    private int total;

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public ArrayList<GuokrHotItem> getResult() {
        return result;
    }

    public void setResult(ArrayList<GuokrHotItem> result) {
        this.result = result;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
