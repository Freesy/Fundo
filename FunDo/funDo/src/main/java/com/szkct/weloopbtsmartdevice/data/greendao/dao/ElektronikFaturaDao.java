package com.szkct.weloopbtsmartdevice.data.greendao.dao;

import org.kymjs.kjframe.database.annotate.Id;

/**
 * 电子发票数据库
 * Created by HRJ on 2018/3/2.
 */
public class ElektronikFaturaDao {

    // 将id属性设置为主键，必须有一个主键，
    // 其实如果变量名为：'id'或'_id'默认就是主键
    // 也就是在一个JavaBean里面必须有'id'或'_id'或'@Id()'注解，否则会报错
    @Id()
    private int id;
    String company_letterhead;//公司抬头
    String tax_id;//税号
    String address;//地址
    String phone;//电话
    String bank;//开户行
    String bank_account;//银行账户
    String remarks;//备注
    String server_id;//服务器数据id
    String encode_str;//发票加密格式
    int isDefault;//是否是默认发票

    /************* getter and setter 必须有 *******************/
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompany_letterhead() {
        return company_letterhead;
    }

    public void setCompany_letterhead(String company_letterhead) {
        this.company_letterhead = company_letterhead;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBank_account() {
        return bank_account;
    }

    public void setBank_account(String bank_account) {
        this.bank_account = bank_account;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getServer_id() {
        return server_id;
    }

    public void setServer_id(String server_id) {
        this.server_id = server_id;
    }

    public String getEncode_str() {
        return encode_str;
    }

    public void setEncode_str(String encode_str) {
        this.encode_str = encode_str;
    }

    public int getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(int isDefault) {
        this.isDefault = isDefault;
    }

    @Override
    public String toString() {
        return company_letterhead+"</>"+tax_id+"</>"+address+" "+phone+"</>"+bank+" "+bank_account+"</>";
    }
}
