package com.szkct.weloopbtsmartdevice.data;

import com.szkct.weloopbtsmartdevice.login.Gdata;

import java.io.Serializable;

/**
 * Created by ${Justin} on 2017/12/1.
 */

public class Person implements Serializable {
    public static final String MALE = "male";
    public static final String FEMALE = "female";
    public static final String USERNAME_DEFAULT = "";

    private int mid = Gdata.NOT_LOGIN;
    private String sex = MALE;
    private String birth = "1990-01-01";
    private String height = "175";
    private String weight = "65";
    private String username = USERNAME_DEFAULT;
    private String face = "";

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
