package com.szkct.weloopbtsmartdevice.data;

/**
 * Created by ${Justin} on 2018/11/23.
 */

public class advertisingBean {


    /**
     * code : 0
     * message : 请求成功
     * data : {"guideUrl":"http://group.fundo.xyz/fundogroup_activity_share/tianjiezuo.png"}
     */

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * guideUrl : http://group.fundo.xyz/fundogroup_activity_share/tianjiezuo.png
         */

        private String guideUrl;

        public String getGuideUrl() {
            return guideUrl;
        }

        public void setGuideUrl(String guideUrl) {
            this.guideUrl = guideUrl;
        }
    }
}
