package name.caiyao.microreader.bean.gankio;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by 蔡小木 on 2016/3/21 0021.
 *
 */
public class GankVideo{
    @SerializedName("error")
    private boolean error;
    @SerializedName("results")
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
