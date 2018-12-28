package com.szkct.weloopbtsmartdevice.data;

/**
 * 版权：深圳金康特智能科技有限公司
 * 作者：ZGH
 * 版本：
 * 创建日期：2018/5/21
 * 描述: ${VERSION}
 * 修订历史：
 */

public class DailyForecast {

    private String weatherDate;

    private String temperatureMin;

    private String temperatureMax;

    private String uvIndex;

    private String weatherCode;

    private String pressure;

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public DailyForecast(String weatherDate, String temperatureMin, String temperatureMax, String uvIndex, String weatherCode, String pressure) {
        this.weatherDate = weatherDate;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.uvIndex = uvIndex;
        this.weatherCode = weatherCode;
        this.pressure = pressure;
    }

    public String getWeatherDate() {
        return weatherDate;
    }

    public void setWeatherDate(String weatherDate) {
        this.weatherDate = weatherDate;
    }

    public String getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(String temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public String getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(String temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(String uvIndex) {
        this.uvIndex = uvIndex;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }

    @Override
    public String toString() {
        return "DailyForecast{" +
                "weatherDate='" + weatherDate + '\'' +
                ", temperatureMin='" + temperatureMin + '\'' +
                ", temperatureMax='" + temperatureMax + '\'' +
                ", uvIndex='" + uvIndex + '\'' +
                ", weatherCode='" + weatherCode + '\'' +
                ", pressure='" + pressure + '\'' +
                '}';
    }
}
