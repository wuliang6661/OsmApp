package com.heloo.android.osmapp.api;

import com.heloo.android.osmapp.model.BaseResult;
import com.heloo.android.osmapp.model.ShopBannarBO;
import com.heloo.android.osmapp.model.ShopDetailsBO;
import com.heloo.android.osmapp.model.ShopListBO;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * <p>
 * 后台接口统一
 */

public interface HttpInterface {

    String URL = "http://osm.happydoit.com";
    String URLS = "https://osm.happydoit.com";
    String IMG_URL = "https://app.osm.cn/appfile";
//    String URL = "http://192.168.1.115:8090";

    /**
     * 版本检测更新
     */
    @POST("/version.json")
    Observable<ResponseBody> loadVersion();

    /**
     * 获取验证码
     */
    @GET("/ums/getOtpCode")
    Observable<ResponseBody> getCode(@Query("telephone") String telephone,
                                     @Query("type") String type);//获取验证码类型，注册register,登录login,改密码resetPassword

    /**
     * 注册
     */
    @FormUrlEncoded
    @POST("/ums/register")
    Observable<ResponseBody> register(@Field("username") String username,
                                      @Field("otpCode") String otpCode,
                                      @Field("password") String password);

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST("/ums/login")
    Observable<ResponseBody> login(@Field("loginType") String loginType,//密码byPassword/验证码byOtpCode
                                   @Field("username") String username,
                                   @Field("otpCode") String otpCode,
                                   @Field("password") String password);

    /**
     * 修改密码
     */
    @FormUrlEncoded
    @POST("/ums/setPassword")
    Observable<ResponseBody> modifyPassword(@Header("Authorization") String token,
                                            @Field("newPassword") String newPassword,
                                            @Field("oldPassword") String oldPassword);


    /**
     * 获取用户信息
     */
    @GET("/ums/info")
    Observable<ResponseBody> getUserInfo(@Header("Authorization") String token);


    /**
     * 修改个人信息
     */
    @Multipart
    @POST("/app/updateinformation")
    Observable<ResponseBody> changeInfo(@Header("Authorization") String token,
                                        @Part List<MultipartBody.Part> partList);


    /**
     * 首页标题
     */
    @GET("/articleCategory/listArticleCategory")
    Observable<ResponseBody> getTitle(@Header("Authorization") String token);


    /**
     * 首页获取轮播图 公告 专题
     */
    @GET("/app/index")
    Observable<ResponseBody> getBanner(@Header("Authorization") String token);




    /**
     * 首页文章列表
     */
    @GET("/articleInfo/listArticleInfo")
    Observable<ResponseBody> getArticleList(@Header("Authorization") String token,
                                            @Query("pageNum") int pageNum,
                                            @Query("pageSize") int pageSize,
                                            @Query("categoryId") String categoryId,
                                            @Query("isRecommend") String isRecommend,
                                            @Query("keyword") String keyword);

    /**
     * 珍好看 图集
     */
    @GET("/app/imagesarticle")
    Observable<ResponseBody> getPictures(@Header("Authorization") String token,
                                         @Query("pageNum") int pageNum,
                                         @Query("pageSize") int pageSize);

    /**
     * 珍好看 视频
     */
    @GET("/app/videoarticle")
    Observable<ResponseBody> getVideos(@Header("Authorization") String token,
                                       @Query("pageNum") int pageNum,
                                       @Query("pageSize") int pageSize);

    /**
     * 珍好看 图文直播
     */
    @GET("/app/getpicarticlelive")
    Observable<ResponseBody> getLives(@Header("Authorization") String token,
                                      @Query("pageNum") int pageNum,
                                      @Query("pageSize") int pageSize);

    /**
     * 商城商品
     */
    @GET("/shopOrder/getProductList")
    Observable<BaseResult<ShopListBO>> getStoreClassify();

    /**
     * 商城轮播图
     */
    @GET("/shopOrder/getProdlist")
    Observable<BaseResult<ShopBannarBO>> getStoreBanner();


    /**
     * 商城商品详情
     */
    @GET("/shopOrder/getProductListId/{id}")
    Observable<BaseResult<ShopDetailsBO>> getProductDetail(@Path("id") String id);

    /**
     * 加入购物车
     */
    @FormUrlEncoded
    @POST("/shopCart/createPostTopic")
    Observable<BaseResult<String>> addCart(@Field("goodsId") String goodsId,
                                     @Field("userId") String userId,
                                     @Field("num") String num);

    /**
     * 添加地址
     */
    @FormUrlEncoded
    @POST("/app/addAddress")
    Observable<ResponseBody> addAddress(@Header("Authorization") String token,
                                        @Field("address") String address,
                                        @Field("province") String province,
                                        @Field("city") String city,
                                        @Field("area") String area,
                                        @Field("name") String name,
                                        @Field("phone") String phone,
                                        @Field("postcode") String postcode,
                                        @Field("status") String status);//默认地址 1为默认 2为非

    /**
     * 修改地址
     */
    @FormUrlEncoded
    @POST("/app/updateuseraddress")
    Observable<ResponseBody> modifyAddress(@Header("Authorization") String token,
                                           @Field("id") String id,
                                           @Field("address") String address,
                                           @Field("province") String province,
                                           @Field("city") String city,
                                           @Field("area") String area,
                                           @Field("name") String name,
                                           @Field("phone") String phone,
                                           @Field("postcode") String postcode,
                                           @Field("status") String status);//默认地址 1为默认 2为非


    /**
     * 地址列表
     */
    @GET("/app/getuseraddress")
    Observable<ResponseBody> getAddress(@Header("Authorization") String token);


    /**
     * 删除地址
     */
    @FormUrlEncoded
    @POST("/app/deleteuseraddress")
    Observable<ResponseBody> delAddress(@Header("Authorization") String token,
                                        @Field("id") String id);


    /**
     * 评论列表
     */
    @GET("/posting/listComment")
    Observable<ResponseBody> getCommentList(@Header("Authorization") String token,
                                            @Query("postId") String postId);//所在贴子id


    /**
     * 发布评论
     */
    @FormUrlEncoded
    @POST("/posting/createComment")
    Observable<ResponseBody> addComment(@Header("Authorization") String token,
                                        @Field("postId") String postId,//所在贴子id
                                        @Field("word") String word,
                                        @Field("categoryId") String categoryId,//一级评论为1，二级或多级为2
                                        @Field("pId") String pId,//上级的评论id，没有则为空
                                        @Field("pUid") String pUid,//上级的评论用户，没有则为空
                                        @Field("replyId") String replyId,//被回复的评论id，没有则为空
                                        @Field("replyUid") String replyUid);//被回复的评论用户uid，没有则为空

    /**
     * 点赞
     */
    @FormUrlEncoded
    @POST("/posting/postingLike")
    Observable<ResponseBody> like(@Header("Authorization") String token,
                                  @Field("id") String id);

    /**
     * 删除评论
     */
    @FormUrlEncoded
    @POST("/posting/delComment")
    Observable<ResponseBody> deleteComment(@Header("Authorization") String token,
                                           @Field("commentId") String id);

    /**
     * 首页圈子列表
     */
    @GET("/posting/listPostInfo")
    Observable<ResponseBody> getCircle(@Header("Authorization") String token,
                                       @Query("pageNum") int pageNum,
                                       @Query("pageSize") int pageSize,
                                       @Query("topicId") String topicId,//话题ID，不传则全部，传的话就返回指定话题的贴子
                                       @Query("type") String type);//最新newest，最热hot，我的myPost


    /**
     * 全部热门话题
     */
    @GET("/postTopic/listPostTopic")
    Observable<ResponseBody> getTopicList(@Header("Authorization") String token,
                                          @Query("pageNum") int pageNum,
                                          @Query("pageSize") int pageSize);


    /**
     * 点赞人员列表
     */
    @GET("/posting/listPraise")
    Observable<ResponseBody> likePersonList(@Header("Authorization") String token,
                                            @Query("postId") String postId);

    /**
     * 回复评论详情
     */
    @GET("/posting/noticeInCommentPage")
    Observable<ResponseBody> commentDetail(@Header("Authorization") String token,
                                           @Query("commentId") String commentId);

    /**
     * 点赞列表
     */
    @GET("/posting/listPostPraisePage")
    Observable<ResponseBody> likeList(@Header("Authorization") String token,
                                      @Query("pageNum") int pageNo,
                                      @Query("pageSize") int pagesize);

    /**
     * 评论列表
     */
    @GET("/posting/listPostCommentNoticePage")
    Observable<ResponseBody> commentList(@Header("Authorization") String token,
                                         @Query("pageNum") int pageNo,
                                         @Query("pageSize") int pagesize);

    /**
     * 发圈子
     */
    @Multipart
    @POST("/posting/createPostInfo")
    Observable<ResponseBody> sendCircle(@Header("Authorization") String token,
                                        @Part List<MultipartBody.Part> partList);


    /**
     * 热门文章
     */
    @FormUrlEncoded
    @POST("/app/getheatsarticle")
    Observable<ResponseBody> getHotArticles(@Header("Authorization") String token,
                                            @Field("type") String type);//1当日,2本周,3本月,4本年


    /**
     * 个人积分消费明细
     */
    @FormUrlEncoded
    @POST("/app/userconsumptionlog")
    Observable<ResponseBody> getCoinsList(@Header("Authorization") String token,
                                          @Field("pageNum") int pageNum,
                                          @Field("pageSize") int pageSize);

    /**
     * 个人积分获得积分明细
     */
    @FormUrlEncoded
    @POST("/app/usergetscorelog")
    Observable<ResponseBody> getAddCoinsList(@Header("Authorization") String token,
                                             @Field("pageNum") int pageNum,
                                             @Field("pageSize") int pageSize);

    /**
     * 英雄榜
     */
    @FormUrlEncoded
    @POST("/app/getintegral")
    Observable<ResponseBody> getHeroList(@Header("Authorization") String token,
                                         @Field("type") String type);// 1当日,2本周,3本月,4本年

    /**
     * 签到状态判断
     */
    @GET("/app/initialize")
    Observable<ResponseBody> getSignStatus(@Header("Authorization") String token,
                                           @Query("username") String username);


    /**
     * 我的团队
     */
    @FormUrlEncoded
    @POST("/temp/gtemytemp")
    Observable<ResponseBody> getTeamList(@Header("Authorization") String token,
                                         @Field("deptId") String deptId,
                                         @Field("type") String type);

    /**
     * 我的团队详情
     */
    @FormUrlEncoded
    @POST("/temp/getUserDaite")
    Observable<ResponseBody> getTeamDetail(@Header("Authorization") String token,
                                           @Field("pageNum") int pageNum,
                                           @Field("pageSize") int pageSize,
                                           @Field("username") String username);

    /**
     * 专题列表
     */
    @GET("/articleSpecial/listArticleSpecial")
    Observable<ResponseBody> getSubject(@Header("Authorization") String token,
                                        @Query("pageNum") int pageNum,
                                        @Query("pageSize") int pageSize);

    /**
     * 专题详情
     */
    @GET("/app/specialdateil")
    Observable<ResponseBody> getSubjectDetail(@Header("Authorization") String token,
                                              @Query("id") String id);


    /**
     * 珍币说明
     */
    @GET("/app/instructions")
    Observable<ResponseBody> getDes(@Header("Authorization") String token);

    /**
     * 忘记密码
     */
    @FormUrlEncoded
    @POST("/ums/resetPassword")
    Observable<ResponseBody> modifyPassword(@Header("Authorization") String token,
                                            @Field("otpCode") String otpCode,
                                            @Field("password") String password,
                                            @Field("username") String username);
}
