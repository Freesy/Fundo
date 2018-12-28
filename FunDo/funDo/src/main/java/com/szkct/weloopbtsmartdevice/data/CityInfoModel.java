package com.szkct.weloopbtsmartdevice.data;

/**
 * Created by HRJ on 2017/11/10.
 */

public class CityInfoModel {

    int status;

    Result result;

    public class Result{

        AddressComponent addressComponent;

        public AddressComponent getAddressComponent() {
            return addressComponent;
        }
    }

    public class AddressComponent{

        String country;

        String city;

        String district;

        String province;

        public String getProvince() {
            return province;
        }

        public String getCity() {
            return city;
        }

        public String getDistrict() {
            return district;
        }

        public String getCountry() {
            return country;
        }
    }

    public int getStatus() {
        return status;
    }

    public Result getResult() {
        return result;
    }
}
//{
//        "status": 0,
//        "result": {
//        "location": {
//        "lng": 113.95192999999998,
//        "lat": 22.554468999877738
//        },
//        "formatted_address": "广东省深圳市南山区科苑路17号",
//        "business": "科技园,南山医院,大冲",
//        "addressComponent": {
//        "country": "中国",
//        "country_code": 0,
//        "country_code_iso": "CHN",
//        "province": "广东省",
//        "city": "深圳市",
//        "city_level": 2,
//        "district": "南山区",
//        "town": "",
//        "adcode": "440305",
//        "street": "科苑路",
//        "street_number": "17号",
//        "direction": "附近",
//        "distance": "44"
//        },
//        "pois": [],
//        "roads": [],
//        "poiRegions": [],
//        "sematic_description": "爱康国宾深圳科技园科兴体检分院东北88米",
//        "cityCode": 340
//        }
//        }