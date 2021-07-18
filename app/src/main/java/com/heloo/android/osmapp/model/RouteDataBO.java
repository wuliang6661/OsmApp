package com.heloo.android.osmapp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by witness on 2018/4/26.
 */

public class RouteDataBO {
    /**
     * 名字
     */
    private String name;
    /**
     * 子项类型
     * 1: 标题
     * 2：普通
     */
    private int type;
    private String pic;
    private String time;
    private int liveType;
    private String id;

    public RouteDataBO(String id,String name, int type,String pic,String time,int liveType) {
        this.name = name;
        this.type = type;
        this.pic = pic;
        this.time = time;
        this.liveType = liveType;
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public String getPic() {
        return pic;
    }

    public String getTime() {
        return time;
    }

    public int getLiveType() {
        return liveType;
    }

    public String getId() {
        return id;
    }

    /**
     * 存储的数据列表
     */
    private static List<RouteDataBO> mDataList;
    public static List<RouteDataBO> getDataList(LivesBean livesBean) {
        if (mDataList == null){
            mDataList = new ArrayList<>();
            if (livesBean == null){
                return mDataList;
            }

            RouteDataBO bean1 = new RouteDataBO("","正在直播",1,"","",1);
            mDataList.add(bean1);
            mDataList.addAll(setData(livesBean,1));

            RouteDataBO bean2 = new RouteDataBO("","直播预告",1,"","",2);
            mDataList.add(bean2);
            mDataList.addAll(setData(livesBean,2));

            RouteDataBO bean3 = new RouteDataBO("","往期回顾",1,"","",3);
            mDataList.add(bean3);
            mDataList.addAll(setData(livesBean,3));

        }
        return mDataList;
    }

    /**
     * 直播数据
     * @param livesBean 直播数据
     * @param m 分类  1 正在直播  2 直播预告  3 往期回顾
     * @return
     */
    private static List<RouteDataBO> setData(LivesBean livesBean,int m){
        List<RouteDataBO> liveData =new ArrayList<>();
        RouteDataBO singleLiveData;
        switch (m){
            case 1:
                for (int i=0;i<livesBean.getDoinglist().size();i++){
                    LivesBean.DoinglistBean livesBean1 = livesBean.getDoinglist().get(i);
                    singleLiveData = new RouteDataBO(livesBean1.getId(),livesBean1.getName(),2,livesBean1.getBanner(),livesBean1.getStartDate(),1);
                    liveData.add(singleLiveData);
                }
                break;
            case 2:
                for (int i=0;i<livesBean.getNotstartedlist().size();i++){
                    LivesBean.NotstartedlistBean livesBean1 = livesBean.getNotstartedlist().get(i);
                    singleLiveData = new RouteDataBO(livesBean1.getId(),livesBean1.getName(),2,livesBean1.getBanner(),livesBean1.getStartDate(),2);
                    liveData.add(singleLiveData);
                }
                break;
            case 3:
                for (int i=0;i<livesBean.getEndlist().size();i++){
                    LivesBean.EndlistBean livesBean1 = livesBean.getEndlist().get(i);
                    singleLiveData = new RouteDataBO(livesBean1.getId(),livesBean1.getName(),2,livesBean1.getBanner(),livesBean1.getStartDate(),3);
                    liveData.add(singleLiveData);
                }
                break;
        }
        return liveData;
    }
}
