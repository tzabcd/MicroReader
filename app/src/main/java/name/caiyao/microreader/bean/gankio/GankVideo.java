package name.caiyao.microreader.bean.gankio;

import java.util.ArrayList;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 *
 */
public class GankVideo{
    private boolean error;
    private ArrayList<GankVideoItem> results;


    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ArrayList<GankVideoItem> getResults() {
        return results;
    }

    public void setResults(ArrayList<GankVideoItem> results) {
        this.results = results;
    }
}
