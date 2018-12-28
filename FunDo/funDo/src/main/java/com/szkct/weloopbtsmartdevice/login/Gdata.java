package com.szkct.weloopbtsmartdevice.login;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.NumberPicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.szkct.weloopbtsmartdevice.data.DataEntity;
import com.szkct.weloopbtsmartdevice.data.Person;
import com.szkct.weloopbtsmartdevice.main.BTNotificationApplication;
import com.szkct.weloopbtsmartdevice.main.MyDataActivity;
import com.szkct.weloopbtsmartdevice.util.ParseNumber;
import com.szkct.weloopbtsmartdevice.util.SharedPreUtil;
import com.szkct.weloopbtsmartdevice.util.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;

/**
 * Created by ${Justin} on 2017/12/1.
 */

/** 保存全局用到的数据,数据被用户主动删除的情况没有考虑 */
public class Gdata {
    private static final String TAG = Gdata.class.getSimpleName();

    /** 通过mid来判断是否为登录状态,如未登录则用-1表示. */
    public static final int NOT_LOGIN = -1;

    /** 序列化到本地的文件名,路径用全局的那个 */
    public static final String NO_UPLOAD_DATA_DAT = "NoUploadData.dat";
    public static final String PERSON_DAT = "Person.dat";

    /** 当用户登录后,把用户信息存本地,只存一个用户信息,切换账户则覆盖 */
    private static Person person;

    public static final String NETHOMEDATAS_dat = "Nethomedatas.dat";


    public static void setPersonData(String jsonStr) {
        Gson gson = new Gson();
        Type jsonType = new TypeToken<DataEntity<Person>>() {
        }.getType();
        DataEntity<Person> temp = gson.fromJson(jsonStr, jsonType);
        person = temp.getData();
        if (person == null) {
            person = new Person();
            Log.e(TAG, "setPersonData: *这代码应该永远不执行的*从网络拿Json数据异常");
        }
        save2sp();
        writeObjectToFile(person, PERSON_DAT);
    }


    /** 保存到本地 */
    private static void save2sp() {
        person.setHeight(ParseNumber.parseInt(person.getHeight()) + "");
        person.setWeight(ParseNumber.parseInt(person.getWeight()) + "");
        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.HEIGHT, person.getHeight());
        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.WEIGHT, person.getWeight());
        if (person.getUsername().equals(Person.MALE)) {
            SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SEX, 0 + "");
        } else {
            SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.SEX, 1 + "");
        }
        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.BIRTH, person.getBirth());
        SharedPreUtil.savePre(BTNotificationApplication.getInstance(), SharedPreUtil.USER, SharedPreUtil.NAME, person.getUsername());
    }


    public static Person getPersonData() {
        if (person == null) {
            Log.e(TAG, "getPersonData: *这代码应该永远不执行的*获取用户数据异常,可能是登录时没赋值");
            person = (Person) readObjectFromFile();
        }
        return person;
    }

    public static void savePersonToFile(Person per) {
        person = per;
        writeObjectToFile(person, PERSON_DAT);
    }

    public static void savePersonToFile() {
        if (person != null) {
            Log.i(TAG, "savePersonToFile: 直接保存本类中的person,因为没有getPersonData去");
            writeObjectToFile(person, PERSON_DAT);
        }
    }

    public static void writeObjectToFile(Object obj, String fileName) {

        File file = new File(getDiskFilesDir(BTNotificationApplication.getInstance()), fileName);

        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(obj);
            objOut.flush();
            objOut.close();
            Log.i(TAG, "writeObjectToFile: 保存Person到文件成功");
        } catch (IOException e) {
            Log.e(TAG, "writeObjectToFile: *这代码应该永远不执行的*失败=" + e.getMessage());
            e.printStackTrace();
        }
    }

    /** 为了保证一定不会返回空所以加这个方法,本来有一个带参的就ok了 */
    public static Object readObjectFromFile() {
        Object temp = readObjectFromFile(PERSON_DAT);
        if (temp == null) {
            Person p = new Person();
            Log.e(TAG, "readObjectFromFile: *这代码应该永远不执行的*本地文件读取失败,从新初始化=" + p.toString());
            return p;
        }
        return temp;
    }

    public static Object readObjectFromFile(String fileName) {
        Object temp = null;
        FileInputStream in;
        File file = new File(getDiskFilesDir(BTNotificationApplication.getInstance()), fileName);
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            temp = objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            Log.e(TAG, "readObjectFromFile: 异常读取保存本地文件失败=" + e.getMessage());
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "readObjectFromFile: 异常类找不到=" + e.getMessage());
            e.printStackTrace();
        }
        return temp;
    }

    /**
     * App的用户模式只有两种:游客和登录. Mid = 0是游客.其它是登录
     * 游客模式定义:
     * 1.数据不能存服务器
     * 2.不能使用排行榜功能。
     * 3.不能点击账号管理，
     * 4.不能点击意见反馈。
     * 登录:
     * 可以使用所有功能.
     */
    public static int getMid() {
//        return MID_TEST;
        if (person == null) {
            Log.e(TAG, "getMid: null");
            person = getPersonData();   //拿存本地的值
//            return NOT_LOGIN;
//            throw new NullPointerException("获取Mid出错,登录app时没赋值");
        }
        return person.getMid();
    }

    /**
     * 用户数据保存目录
     *
     * @param context
     * @return
     */
    public static String getDiskFilesDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalFilesDir(null).getPath();
        } else {
            cachePath = context.getFilesDir().getPath();
        }
        return cachePath;
    }
}
