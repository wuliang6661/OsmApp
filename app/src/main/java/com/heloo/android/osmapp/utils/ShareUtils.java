package com.heloo.android.osmapp.utils;

import android.app.Activity;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

public class ShareUtils {


    public static void shareWeChat(Activity context, String title, String desc, String img, String url) {
        SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN;
        ShareAction shareAction = new ShareAction(context).setPlatform(platform);
        UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        web.setDescription(desc);//描述
        UMImage umImage = new UMImage(context, img);
        umImage.setTitle(title);
        umImage.setDescription(desc);
        web.setThumb(umImage);
        shareAction.withMedia(web);
        shareAction.withSubject(title).withText(desc).share();
    }


    public static void shareWeCommont(Activity context, String title, String desc, String img, String url) {
        SHARE_MEDIA platform = SHARE_MEDIA.WEIXIN_CIRCLE;
        ShareAction shareAction = new ShareAction(context).setPlatform(platform);
        UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        web.setDescription(desc);//描述
        UMImage umImage = new UMImage(context, img);
        umImage.setTitle(title);
        umImage.setDescription(desc);
        web.setThumb(umImage);
        shareAction.withMedia(web);
        shareAction.withSubject(title).withText(desc).share();
    }


    public static void shareQQ(Activity context, String title, String desc, String img, String url) {
        SHARE_MEDIA platform = SHARE_MEDIA.QQ;
        ShareAction shareAction = new ShareAction(context).setPlatform(platform);
        UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        web.setDescription(desc);//描述
        UMImage umImage = new UMImage(context, img);
        umImage.setTitle(title);
        umImage.setDescription(desc);
        web.setThumb(umImage);
        shareAction.withMedia(web);
        shareAction.withSubject(title).withText(desc).share();
    }



    public static void shareQQZone(Activity context, String title, String desc, String img, String url) {
        SHARE_MEDIA platform = SHARE_MEDIA.QZONE;
        ShareAction shareAction = new ShareAction(context).setPlatform(platform);
        UMWeb web = new UMWeb(url);
        web.setTitle(title);//标题
        web.setDescription(desc);//描述
        UMImage umImage = new UMImage(context, img);
        umImage.setTitle(title);
        umImage.setDescription(desc);
        web.setThumb(umImage);
        shareAction.withMedia(web);
        shareAction.withSubject(title).withText(desc).share();
    }
}
