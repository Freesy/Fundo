package com.szkct.map.bean;

/**
 * Created by Ronny on 2018/6/6.
 */

public class GuangGaoBean {
    /**
     * mobileSystem : 0
     * country : 0
     * appName : 0
     * dataType : 1
     * sex : 0
     * description : 分动开屏活动页
     * duration : 3
     * detailPageUrl : https://www.baidu.com
     * endTimes : 20:00
     * detailPageTitle : 活动详情
     * beginTimes : 10:00
     * id : 1
     * operationPosition : 0
     * pageEntry : http://wx.funos.cn:8080/fundo-startpage/activity_1_default__1.png
     * status : 1
     */

    private int mobileSystem;
    private int country;
    private int appName;
    private int dataType;
    private int sex;
    private String description;
    private int duration;
    private String detailPageUrl;
    private String endTimes;
    private String detailPageTitle;
    private String beginTimes;
    private int pageId;
    private int operationPosition;
    private String pageEntry;
    private String status;

    public int getMobileSystem() {
        return mobileSystem;
    }

    public void setMobileSystem(int mobileSystem) {
        this.mobileSystem = mobileSystem;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }

    public int getAppName() {
        return appName;
    }

    public void setAppName(int appName) {
        this.appName = appName;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDetailPageUrl() {
        return detailPageUrl;
    }

    public void setDetailPageUrl(String detailPageUrl) {
        this.detailPageUrl = detailPageUrl;
    }

    public String getEndTimes() {
        return endTimes;
    }

    public void setEndTimes(String endTimes) {
        this.endTimes = endTimes;
    }

    public String getDetailPageTitle() {
        return detailPageTitle;
    }

    public void setDetailPageTitle(String detailPageTitle) {
        this.detailPageTitle = detailPageTitle;
    }

    public String getBeginTimes() {
        return beginTimes;
    }

    public void setBeginTimes(String beginTimes) {
        this.beginTimes = beginTimes;
    }

    public int getId() {
        return pageId;
    }

    public void setId(int id) {
        this.pageId = id;
    }

    public int getOperationPosition() {
        return operationPosition;
    }

    public void setOperationPosition(int operationPosition) {
        this.operationPosition = operationPosition;
    }

    public String getPageEntry() {
        return pageEntry;
    }

    public void setPageEntry(String pageEntry) {
        this.pageEntry = pageEntry;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
