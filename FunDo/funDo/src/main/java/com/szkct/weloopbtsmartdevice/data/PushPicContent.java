package com.szkct.weloopbtsmartdevice.data;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/5/21
 * 描述: ${VERSION}
 * 修订历史：
 */

public class PushPicContent {
      /*
                   返回结果（json）：
                   {"code":0,
                   "data":{"country":"中国","city":"龙岗","weatherCode":"101","aqi":"","temperature":"30","cityid":"CN101280606","pressure":"1006","updateTimes":"2018-05-18 17:41","createTimes":"2018-05-18 17:41",
                   "dailyForecast":[{"weatherDate":"2018-05-18","weatherCode":"101","temperatureMax":"32","temperatureMin":"27","pressure":"1008","uvIndex":"10"},
                   {"weatherDate":"2018-05-19","weatherCode":"101","temperatureMax":"32","temperatureMin":"26","pressure":"1009","uvIndex":"9"},
                   {"weatherDate":"2018-05-20","weatherCode":"101","temperatureMax":"32","temperatureMin":"26","pressure":"1009","uvIndex":"11"}]},
                   "message":"请求成功"}
                   返回结果（json）：
                   {"code":0,
                   "data":[{"dialId":84,"adaptiveNumber":301,"dialPictureUrl":"http://wx.funos.cn:8080/fundo-dialPic/Chrysanthemum.jpg","dialFileUrl":"http://wx.funos.cn:8080/fundo-dialFile/test.zip","dialName":"测试1"},
                           {"dialId":85,"adaptiveNumber":301,"dialPictureUrl":"http://wx.funos.cn:8080/fundo-dialPic/Chrysanthemum.jpg","dialFileUrl":"http://wx.funos.cn:8080/fundo-dialFile/test.zip","dialName":"测试2"}],
                           "message":"请求成功"}
                    */

    private int dialId;

    private int adaptiveNumber;

    private String dialPictureUrl;

    private String dialFileUrl;

    private String dialName;

    public int getDialId() {
        return dialId;
    }

    public void setDialId(int dialId) {
        this.dialId = dialId;
    }

    public int getAdaptiveNumber() {
        return adaptiveNumber;
    }

    public void setAdaptiveNumber(int adaptiveNumber) {
        this.adaptiveNumber = adaptiveNumber;
    }

    public String getDialPictureUrl() {
        return dialPictureUrl;
    }

    public void setDialPictureUrl(String dialPictureUrl) {
        this.dialPictureUrl = dialPictureUrl;
    }

    public String getDialFileUrl() {
        return dialFileUrl;
    }

    public void setDialFileUrl(String dialFileUrl) {
        this.dialFileUrl = dialFileUrl;
    }

    public String getDialName() {
        return dialName;
    }

    public void setDialName(String dialName) {
        this.dialName = dialName;
    }

    public PushPicContent(int dialId, int adaptiveNumber, String dialPictureUrl, String dialFileUrl, String dialName) {
        this.dialId = dialId;
        this.adaptiveNumber = adaptiveNumber;
        this.dialPictureUrl = dialPictureUrl;
        this.dialFileUrl = dialFileUrl;
        this.dialName = dialName;
    }

    @Override
    public String toString() {
        return "PushPicContent{" +
                "dialId='" + dialId + '\'' +
                ", adaptiveNumber='" + adaptiveNumber + '\'' +
                ", dialPictureUrl='" + dialPictureUrl + '\'' +
                ", dialFileUrl='" + dialFileUrl + '\'' +
                ", dialName='" + dialName + '\'' +
                '}';
    }
}
