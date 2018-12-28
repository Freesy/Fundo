package com.szkct.weloopbtsmartdevice.data;

import java.util.List;


/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/5/21
 * 描述: ${VERSION}
 * 修订历史：
 */

public class WeatherCity {

    private String country;

    private String city;

    private String weatherCode;

    private String aqi;

    private String temperature;

    private String cityid;

    private String pressure;

    private String updateTimes;  // todo --- 当前天气的更新时间

    private List<DailyForecast> dailyForecast;


    public WeatherCity(String country, String city, String weatherCode, String aqi, String temperature, String cityid, String pressure,String updateTimes, List<DailyForecast> data) {
        this.country = country;
        this.city = city;
        this.weatherCode = weatherCode;
        this.aqi = aqi;
        this.temperature = temperature;
        this.cityid = cityid;
        this.pressure = pressure;
        this.updateTimes = updateTimes;
        this.dailyForecast = data;
    }


    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    public List<DailyForecast> getDailyForecast() {
        return dailyForecast;
    }

    public void setDailyForecast(List<DailyForecast> data) {
        this.dailyForecast = data;
    }

    public String getCountry() {

        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getUpdateTimes() {
        return updateTimes;
    }

    public void setUpdateTimes(String updateTimes) {
        this.updateTimes = updateTimes;
    }

    @Override
    public String toString() {
        return "WeatherCity{" +
                "country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", weatherCode='" + weatherCode + '\'' +
                ", aqi='" + aqi + '\'' +
                ", temperature='" + temperature + '\'' +
                ", cityid='" + cityid + '\'' +
                ", pressure='" + pressure + '\'' +
                ", DailyForecast=" + dailyForecast +
                '}';
    }
}
