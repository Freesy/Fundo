package com.szkct.weloopbtsmartdevice.data.greendao.dao;

import java.util.List;

/**
 * 服务器返回的查询电子发票数据
 * Created by HRJ on 2018/3/6.
 */
public class ServerElektronikFaturaDao {

    String flag;
    int msgCode;
    List<Data> data;

    public class Data{
        String bankAccount;
        String phoneNumber;
        String receiptQrcodeContent;
        String name;
        int mid;
        String taxNumber;
        int id;//服务器对应的id 需要用来删除修改数据   服务器真心偷懒
        String updateTimes;
        String bankOfDeposit;
        String remarks;
        String unitAddress;
        int isDefault;//1为默认 发票

        public String getBankAccount() {
            return bankAccount;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getReceiptQrcodeContent() {
            return receiptQrcodeContent;
        }

        public String getName() {
            return name;
        }

        public int getMid() {
            return mid;
        }

        public String getTaxNumber() {
            return taxNumber;
        }

        public int getId() {
            return id;
        }

        public String getUpdateTimes() {
            return updateTimes;
        }

        public String getBankOfDeposit() {
            return bankOfDeposit;
        }

        public String getRemarks() {
            return remarks;
        }

        public String getUnitAddress() {
            return unitAddress;
        }

        public int getIfDefault() {
            return isDefault;
        }
    }

    public String getFlag() {
        return flag;
    }

    public int getMsgCode() {
        return msgCode;
    }

    public List<Data> getData() {
        return data;
    }
}
//id	int	数据id
//        mid	Int	用户id
//        name	String	单位名称
//        taxNumber	String	税号
//        unitAddress	String	单位地址
//        phoneNumber	String	电话号码
//        bankOfDeposit	String	开户银行
//        bankAccount	String	银行账号
//        receiptQrcodeContent	String	                 发票二维码字符串
//        remarks	String	备注
