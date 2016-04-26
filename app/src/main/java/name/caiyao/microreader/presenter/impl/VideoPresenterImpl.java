package name.caiyao.microreader.presenter.impl;

import android.content.Context;
import android.text.TextUtils;

import com.apkfuns.logutils.LogUtils;
import com.google.gson.Gson;

import java.util.ArrayList;

import name.caiyao.microreader.api.weiboVideo.VideoRequest;
import name.caiyao.microreader.bean.weiboVideo.WeiboVideoBlog;
import name.caiyao.microreader.bean.weiboVideo.WeiboVideoResponse;
import name.caiyao.microreader.config.Config;
import name.caiyao.microreader.presenter.IVideoPresenter;
import name.caiyao.microreader.ui.iView.IVideoFragment;
import name.caiyao.microreader.utils.CacheUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/23 0023.
 */
public class VideoPresenterImpl implements IVideoPresenter {

    private IVideoFragment mIVideoFragment;
    private CacheUtil mCacheUtil;
    private Gson mGson = new Gson();

    public VideoPresenterImpl(IVideoFragment iVideoFragment, Context context) {
        if (iVideoFragment==null)
            throw new IllegalArgumentException("iVideoFragment must not be null");
        mIVideoFragment = iVideoFragment;
        mCacheUtil = CacheUtil.get(context);
    }

    @Override
    public void getVideo(final int page) {
        VideoRequest.getVideoRequstApi().getWeiboVideo(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<WeiboVideoResponse, ArrayList<WeiboVideoBlog>>() {
                    @Override
                    public ArrayList<WeiboVideoBlog> call(WeiboVideoResponse weiboVideoResponse) {
                        ArrayList<WeiboVideoBlog> arrayList = new ArrayList<>();
                        if (!weiboVideoResponse.getCardsItems()[0].getModType().equals("mod/empty")) {
                            ArrayList<WeiboVideoBlog> a = weiboVideoResponse.getCardsItems()[0].getBlogs();
                            for (WeiboVideoBlog w : a) {
                                if (w.getBlog().getmBlog() != null)//处理转发的微博
                                    w.setBlog(w.getBlog().getmBlog());
                                if (w.getBlog().getPageInfo() != null && !TextUtils.isEmpty(w.getBlog().getPageInfo().getVideoPic()))//处理无视频微博
                                    arrayList.add(w);
                            }
                        } else {
                            LogUtils.i("没有数据了！");
                        }
                        return arrayList;
                    }
                })
                .subscribe(new Subscriber<ArrayList<WeiboVideoBlog>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mIVideoFragment.hidProgressDialog();
                        mIVideoFragment.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(ArrayList<WeiboVideoBlog> weiboVideoResponse) {
                        mIVideoFragment.hidProgressDialog();
                        mIVideoFragment.updateList(weiboVideoResponse);
                        mCacheUtil.put(Config.VIDEO + page, mGson.toJson(weiboVideoResponse));
                    }
                });
    }

    @Override
    public void getVideoFromCache(int page) {

    }
}
