package com.heloo.android.osmapp.api;


import com.heloo.android.osmapp.model.BaseResult;
import com.heloo.android.osmapp.model.CreateOrderBo;
import com.heloo.android.osmapp.model.MyAdressBean;
import com.heloo.android.osmapp.model.OrderBO;
import com.heloo.android.osmapp.model.OrderPriceBO;
import com.heloo.android.osmapp.model.PayBean;
import com.heloo.android.osmapp.model.SearchShopBO;
import com.heloo.android.osmapp.model.ShopAddressList;
import com.heloo.android.osmapp.model.ShopBannarBO;
import com.heloo.android.osmapp.model.ShopCarBO;
import com.heloo.android.osmapp.model.ShopDetailsBO;
import com.heloo.android.osmapp.model.ShopListBO;
import com.heloo.android.osmapp.model.SignBO;
import com.heloo.android.osmapp.model.TeamDetailBean;
import com.heloo.android.osmapp.model.UserInfo;
import com.heloo.android.osmapp.model.VersionBO;
import com.heloo.android.osmapp.utils.rx.RxHelper;
import com.heloo.android.osmapp.utils.rx.RxResultHelper;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import rx.Observable;


/**
 * <p>
 * 所有后台接口的实现类
 */

public class HttpInterfaceIml {

    private static HttpInterface service;

    /**
     * 获取代理对象
     */
    private static HttpInterface getService() {
        if (service == null)
            service = ApiManager.getInstance().configRetrofit(HttpInterface.class, HttpInterface.URL);
        return service;
    }

    /**
     * 获取验证码
     */
    public static Observable<ResponseBody> getCode(String telephone, String type) {
        return getService().getCode(telephone, type).compose(RxHelper.httpRusult());
    }

    /**
     * 获取验证码2
     */
    public static Observable<Object> getOPTCode(String telephone, String type) {
        return getService().getOPTCode(telephone, type).compose(RxResultHelper.httpRusult());
    }

    /**
     * 注册
     */
    public static Observable<Object> register(String username, String otpCode, String password, String nickname) {
        return getService().register(username, otpCode, password, nickname).compose(RxResultHelper.httpRusult());
    }

    /**
     * 签到
     */
    public static Observable<SignBO> sign() {
        return getService().sign().compose(RxResultHelper.httpRusult());
    }


    /**
     * 检测版本更新
     */
    public static Observable<VersionBO> getVersionInfo() {
        return getService().loadVersion().compose(RxResultHelper.httpRusult());
    }

    /**
     * 登录
     */
    public static Observable<ResponseBody> login(String loginType, String username, String otpCode, String password) {
        return getService().login(loginType, username, otpCode, password).compose(RxHelper.httpRusult());
    }

    /**
     * 修改密码
     */
    public static Observable<ResponseBody> modifyPassword(String token, String newPassword, String oldPassword) {
        return getService().modifyPassword(token, newPassword, oldPassword).compose(RxHelper.httpRusult());
    }

    /**
     * 获取用户信息
     */
    public static Observable<ResponseBody> getUserInfo(String token) {
        return getService().getUserInfo(token).compose(RxHelper.httpRusult());
    }

    /**
     * 修改用户信息
     */
    public static Observable<ResponseBody> changeInfo(String token, List<MultipartBody.Part> partList) {
        return getService().changeInfo(token, partList).compose(RxHelper.httpRusult());
    }

    /**
     * 获取首页标题
     */
    public static Observable<ResponseBody> getTitle(String token) {
        return getService().getTitle(token).compose(RxHelper.httpRusult());
    }

    /**
     * 首页获取轮播图 公告 专题
     */
    public static Observable<ResponseBody> getBanner(String token) {
        return getService().getBanner(token).compose(RxHelper.httpRusult());
    }


    /**
     * 首页文章列表
     */
    public static Observable<ResponseBody> getArticleList(String token, int pageNum, int pageSize, String categoryId, String isRecommend, String keyword) {
        return getService().getArticleList(token, pageNum, pageSize, categoryId, isRecommend, keyword).compose(RxHelper.httpRusult());
    }

    /**
     * 珍好看 图集
     */
    public static Observable<ResponseBody> getPictures(String token, int pageNum, int pageSize) {
        return getService().getPictures(token, pageNum, pageSize).compose(RxHelper.httpRusult());
    }


    /**
     * 珍好看 视频
     */
    public static Observable<ResponseBody> getVideos(String token, int pageNum, int pageSize) {
        return getService().getVideos(token, pageNum, pageSize).compose(RxHelper.httpRusult());
    }

    /**
     * 珍好看 图文直播
     */
    public static Observable<ResponseBody> getLives(String token, int pageNum, int pageSize) {
        return getService().getLives(token, pageNum, pageSize).compose(RxHelper.httpRusult());
    }

    /**
     * 商城商品分类
     */
    public static Observable<ShopListBO> getStoreClassify() {
        return getService().getStoreClassify().compose(RxResultHelper.httpRusult());
    }

    /**
     * 商城轮播图
     */
    public static Observable<ShopBannarBO> getStoreBanner() {
        return getService().getStoreBanner().compose(RxResultHelper.httpRusult());
    }

    /**
     * 商城商品详情
     */
    public static Observable<ShopDetailsBO> getProductDetail(String id) {
        return getService().getProductDetail(id).compose(RxResultHelper.httpRusult());
    }

    /**
     * 加入购物车
     */
    public static Observable<String> addCart(String goodsId, String id, String num) {
        return getService().addCart(goodsId, id, num).compose(RxResultHelper.httpRusult());
    }

    /**
     * 查询购物车商品
     */
    public static Observable<ShopCarBO> getCar(String userId) {
        return getService().getShopCar(userId).compose(RxResultHelper.httpRusult());
    }

    /**
     * 查询购物车数量
     */
    public static Observable<String> getNumCar(String userId) {
        return getService().getNumCar(userId).compose(RxResultHelper.httpRusult());
    }


    /**
     * 删除购物车商品
     */
    public static Observable<String> delCar(String id) {
        return getService().delCar(id).compose(RxResultHelper.httpRusult());
    }

    /**
     * 批量删除购物车
     */
    public static Observable<String> batchDelete(String shopIds, String userId) {
        return getService().batchDelete(shopIds, userId).compose(RxResultHelper.httpRusult());
    }

    /**
     * 批量修改购物车商品个数
     */
    public static Observable<String> getUpdateNum(String shopIds, String shopNums, String userId) {
        return getService().getUpdateNum(shopIds, shopNums, userId).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取订单列表
     */
    public static Observable<List<OrderBO>> getShopOrderItem(int pageNum, int type) {
        return getService().getShopOrderItem(pageNum, 30, type).compose(RxResultHelper.httpRusult());
    }

    /**
     * 订单详情
     */
    public static Observable<OrderBO> getorderDetail(String id) {
        return getService().getorderDetail(id).compose(RxResultHelper.httpRusult());
    }


    /**
     * 获取剩余珍币
     */
    public static Observable<UserInfo> getUserIntegration(String userId) {
        return getService().getUserIntegration(userId).compose(RxResultHelper.httpRusult());
    }

    /**
     * 计算珍币
     */
    public static Observable<OrderPriceBO> getshopInter(String shopIds, String shopNums) {
        return getService().getshopInter(shopIds, shopNums).compose(RxResultHelper.httpRusult());
    }

    /**
     * 生成订单
     */
    public static Observable<CreateOrderBo> createOrder(int distribution, String addressId, String shopIds, String shopNums,
                                                        String remarks, Integer scoreflag) {
        return getService().createOrder(distribution, addressId, shopIds, shopNums, 1, remarks, scoreflag).compose(RxResultHelper.httpRusult());
    }

    /**
     * 搜索商品
     */
    public static Observable<List<SearchShopBO>> getSearch(String shopName) {
        return getService().getSearch(shopName).compose(RxResultHelper.httpRusult());
    }


    /**
     * 获取地址
     */
    public static Observable<ShopAddressList> getUserAdd(String userId) {
        return getService().getUserAdd(userId).compose(RxResultHelper.httpRusult());
    }


    /**
     * 取消订单
     */
    public static Observable<Object> cancleOrder(String id) {
        return getService().cancleOrder(id).compose(RxResultHelper.httpRusult());
    }

    /**
     * 确认订单
     */
    public static Observable<Object> comfimOrder(String id) {
        return getService().comfimOrder(id).compose(RxResultHelper.httpRusult());
    }

    /**
     * 支付订单
     */
    public static Observable<PayBean> pay(String orderId) {
        return getService().pay(orderId).compose(RxResultHelper.httpRusult());
    }

    /**
     * 退款
     */
    public static Observable<Object> paRefundy(String orderId) {
        return getService().paRefundy(orderId).compose(RxResultHelper.httpRusult());
    }


    /**
     * 转发文章
     */
    public static Observable<Object> articleforward(String articleId, String username) {
        return getService().articleforward(articleId, username).compose(RxResultHelper.httpRusult());
    }


    /**
     * 添加地址
     */
    public static Observable<ResponseBody> addAddress(String token, String address, String province, String city, String area, String name, String phone, String postcode, String status) {
        return getService().addAddress(token, address, province, city, area, name, phone, postcode, status).compose(RxHelper.httpRusult());
    }

    /**
     * 修改地址
     */
    public static Observable<ResponseBody> modifyAddress(String token, String id, String address, String province, String city, String area, String name, String phone, String postcode, String status) {
        return getService().modifyAddress(token, id, address, province, city, area, name, phone, postcode, status).compose(RxHelper.httpRusult());
    }

    /**
     * 地址列表
     */
    public static Observable<ResponseBody> getAddress(String token) {
        return getService().getAddress(token).compose(RxHelper.httpRusult());
    }

    /**
     * 删除地址
     */
    public static Observable<ResponseBody> delAddress(String token, String id) {
        return getService().delAddress(token, id).compose(RxHelper.httpRusult());
    }

    /**
     * 获取评论
     */
    public static Observable<ResponseBody> getCommentList(String token, String postId) {
        return getService().getCommentList(token, postId).compose(RxHelper.httpRusult());
    }

    /**
     * 发布评论
     */
    public static Observable<ResponseBody> addComment(String token, String postId, String word, String categoryId,
                                                      String pId, String pUid, String replyId, String replyUid) {
        return getService().addComment(token, postId, word, categoryId, pId, pUid, replyId, replyUid).compose(RxHelper.<ResponseBody>httpRusult());
    }

    /**
     * 点赞
     */
    public static Observable<ResponseBody> like(String token, String id) {
        return getService().like(token, id).compose(RxHelper.httpRusult());
    }

    /**
     * 删除评论
     */
    public static Observable<ResponseBody> deleteComment(String token, String id) {
        return getService().deleteComment(token, id).compose(RxHelper.<ResponseBody>httpRusult());
    }

    /**
     * 首页圈子列表
     */
    public static Observable<ResponseBody> getCircle(String token, int pageNum, int pageSize, String topicId, String type) {
        return getService().getCircle(token, pageNum, pageSize, topicId, type).compose(RxHelper.httpRusult());
    }

    /**
     * 全部热门话题
     */
    public static Observable<ResponseBody> getTopicList(String token, int pageNum, int pageSize) {
        return getService().getTopicList(token, pageNum, pageSize).compose(RxHelper.httpRusult());
    }

    /**
     * 点赞人员列表
     */
    public static Observable<ResponseBody> likePersonList(String token, String postId) {
        return getService().likePersonList(token, postId).compose(RxHelper.<ResponseBody>httpRusult());
    }

    /**
     * 回复评论详情
     */
    public static Observable<ResponseBody> commentDetail(String token, String commentId) {
        return getService().commentDetail(token, commentId).compose(RxHelper.<ResponseBody>httpRusult());
    }

    /**
     * 点赞列表
     */
    public static Observable<ResponseBody> likeList(String token, int pageNo, int pagesize) {
        return getService().likeList(token, pageNo, pagesize).compose(RxHelper.<ResponseBody>httpRusult());
    }

    /**
     * 评论列表
     */
    public static Observable<ResponseBody> commentList(String token, int pageNo, int pagesize) {
        return getService().commentList(token, pageNo, pagesize).compose(RxHelper.<ResponseBody>httpRusult());
    }

    /**
     * 发圈子
     */
    public static Observable<ResponseBody> sendCircle(String token, List<MultipartBody.Part> partList) {
        return getService().sendCircle(token, partList).compose(RxHelper.<ResponseBody>httpRusult());
    }

    /**
     * 热门文章
     */
    public static Observable<ResponseBody> getHotArticles(String token, String type) {
        return getService().getHotArticles(token, type).compose(RxHelper.httpRusult());
    }

    /**
     * 个人积分消费明细
     */
    public static Observable<ResponseBody> getCoinsList(String token, int pageNum, int pageSize) {
        return getService().getCoinsList(token, pageNum, pageSize).compose(RxHelper.httpRusult());
    }

    /**
     * 个人积分获得积分明细
     */
    public static Observable<ResponseBody> getAddCoinsList(String token, int pageNum, int pageSize) {
        return getService().getAddCoinsList(token, pageNum, pageSize).compose(RxHelper.httpRusult());
    }

    /**
     * 英雄榜
     */
    public static Observable<ResponseBody> getHeroList(String token, String type) {
        return getService().getHeroList(token, type).compose(RxHelper.httpRusult());
    }

    /**
     * 签到状态判断
     */
    public static Observable<ResponseBody> getSignStatus(String token, String username) {
        return getService().getSignStatus(token, username).compose(RxHelper.httpRusult());
    }

    /**
     * 我的团队
     */
    public static Observable<ResponseBody> getTeamList(String deptId, String type) {
        return getService().getTeamList(deptId, type).compose(RxHelper.httpRusult());
    }

    /**
     * 我的团队详情
     */
    public static Observable<TeamDetailBean> getTeamDetail(String username) {
        return getService().getTeamDetail(username).compose(RxResultHelper.httpRusult());
    }

    /**
     * 专题列表
     */
    public static Observable<ResponseBody> getSubject(String token, int pageNum, int pageSize) {
        return getService().getSubject(token, pageNum, pageSize).compose(RxHelper.httpRusult());
    }

    /**
     * 专题详情
     */
    public static Observable<ResponseBody> getSubjectDetail(String token, String id) {
        return getService().getSubjectDetail(token, id).compose(RxHelper.httpRusult());
    }

    /**
     * 珍币说明
     */
    public static Observable<ResponseBody> getDes(String token) {
        return getService().getDes(token).compose(RxHelper.httpRusult());
    }

    /**
     * 忘记密码
     */
    public static Observable<Object> resetPassword(String otpCode, String password, String username) {
        return getService().resetPassword(otpCode, password, username).compose(RxResultHelper.httpRusult());
    }

    /**
     * 获取自提地址
     */
    public static Observable<List<MyAdressBean>> getSincePoint() {
        return getService().getSincePoint().compose(RxResultHelper.httpRusult());
    }
}
