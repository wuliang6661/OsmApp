package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.mvp.BasePresenter;
import com.heloo.android.osmapp.mvp.BaseRequestView;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class SuggestContract {
    public interface View extends BaseRequestView {

        void getList(ResponseBody data) throws JSONException, IOException;

        void getBanner(ResponseBody data) throws JSONException, IOException;

    }

    public  interface Presenter extends BasePresenter<View> {

        /**
         * 文章列表
         * @param token  token
         * @param pageNum pageNum
         * @param pageSize pageSize
         * @param categoryId 所属栏目Id
         * @param isRecommend 是否推荐Y/N
         * @param keyword 标题关键字
         */
        void getList(String token,int pageNum,int pageSize,String categoryId,String isRecommend,String keyword);

        /**
         * 首页获取轮播图 公告 专题
         * @param token
         */
        void getBanner(String token);
    }
}
