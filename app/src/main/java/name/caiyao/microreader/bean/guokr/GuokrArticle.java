package name.caiyao.microreader.bean.guokr;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 *
 */
public class GuokrArticle{
    private String now;
    private boolean ok = false;
    private GuokrArticleResult result;

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

    public GuokrArticleResult getResult() {
        return result;
    }

    public void setResult(GuokrArticleResult result) {
        this.result = result;
    }
}
