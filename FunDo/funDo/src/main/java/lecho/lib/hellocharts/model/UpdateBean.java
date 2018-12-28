package lecho.lib.hellocharts.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ronny on 2018/7/7.
 */

public class UpdateBean implements Parcelable {
    /**
     * mobileSystem : 1
     * appVersion : 1.3.2
     * appMarketForeignUrl : https://play.google.com/store/apps/details?id=com.kct.fundo.btnotification&rdid=com.kct.fundo.btnotification
     * appName : 0
     * description : 分动安卓端版本
     * appMarketUrl : http://imtt.dd.qq.com/16891/6551F8D1828AFAD7C2DE3020B70F8D5D.apk?fsname=com.kct.fundo.btnotification_V1.3.2_1277.apk&amp;csr=1bbd
     * isUpgrade : true
     * status : 2
     */

    private int mobileSystem;
    private String appVersion;
    private String appMarketForeignUrl;
    private int appName;
    private String description;
    private String appMarketUrl;
    private boolean isUpgrade;
    private int status;

    protected UpdateBean(Parcel in) {
        mobileSystem = in.readInt();
        appVersion = in.readString();
        appMarketForeignUrl = in.readString();
        appName = in.readInt();
        description = in.readString();
        appMarketUrl = in.readString();
        isUpgrade = in.readByte() != 0;
        status = in.readInt();
    }

    public static final Creator<UpdateBean> CREATOR = new Creator<UpdateBean>() {
        @Override
        public UpdateBean createFromParcel(Parcel in) {
            return new UpdateBean(in);
        }

        @Override
        public UpdateBean[] newArray(int size) {
            return new UpdateBean[size];
        }
    };

    public int getMobileSystem() {
        return mobileSystem;
    }

    public void setMobileSystem(int mobileSystem) {
        this.mobileSystem = mobileSystem;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppMarketForeignUrl() {
        return appMarketForeignUrl;
    }

    public void setAppMarketForeignUrl(String appMarketForeignUrl) {
        this.appMarketForeignUrl = appMarketForeignUrl;
    }

    public int getAppName() {
        return appName;
    }

    public void setAppName(int appName) {
        this.appName = appName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAppMarketUrl() {
        return appMarketUrl;
    }

    public void setAppMarketUrl(String appMarketUrl) {
        this.appMarketUrl = appMarketUrl;
    }

    public boolean isIsUpgrade() {
        return isUpgrade;
    }

    public void setIsUpgrade(boolean isUpgrade) {
        this.isUpgrade = isUpgrade;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mobileSystem);
        dest.writeString(appVersion);
        dest.writeString(appMarketForeignUrl);
        dest.writeInt(appName);
        dest.writeString(description);
        dest.writeString(appMarketUrl);
        dest.writeByte((byte) (isUpgrade ? 1 : 0));
        dest.writeInt(status);
    }
}
