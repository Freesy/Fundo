package com.szkct.map.bean;

/**
 * Created by Ronny on 2018/5/21.
 */

public class LuachPageBean {
    /**
     * endTimes : 23:00
     * appName : 0
     * skipPageTitle : 活动详情
     * skipPageUrl : http://wx.funos.cn:8080/fundo-skippage/skiptest.html
     * pageUrl : http://wx.funos.cn:8080/fundo-startpage/activity_1_default__1.png
     * pid : 1
     * updateTimes : 2018-05-17 10:06:45.0
     * createTimes : 2018-05-17 10:06:40.0
     * startTimes : 11:00
     * status : 0
     */

    private String endTimes;
    private int appName;
    private String skipPageTitle;
    private String skipPageUrl;
    private String pageUrl;
    private int pid;
    private String updateTimes;
    private String createTimes;
    private String startTimes;
    private int status;

    public String getEndTimes() {
        return endTimes;
    }

    public void setEndTimes(String endTimes) {
        this.endTimes = endTimes;
    }

    public int getAppName() {
        return appName;
    }

    public void setAppName(int appName) {
        this.appName = appName;
    }

    public String getSkipPageTitle() {
        return skipPageTitle;
    }

    public void setSkipPageTitle(String skipPageTitle) {
        this.skipPageTitle = skipPageTitle;
    }

    public String getSkipPageUrl() {
        return skipPageUrl;
    }

    public void setSkipPageUrl(String skipPageUrl) {
        this.skipPageUrl = skipPageUrl;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getUpdateTimes() {
        return updateTimes;
    }

    public void setUpdateTimes(String updateTimes) {
        this.updateTimes = updateTimes;
    }

    public String getCreateTimes() {
        return createTimes;
    }

    public void setCreateTimes(String createTimes) {
        this.createTimes = createTimes;
    }

    public String getStartTimes() {
        return startTimes;
    }

    public void setStartTimes(String startTimes) {
        this.startTimes = startTimes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
