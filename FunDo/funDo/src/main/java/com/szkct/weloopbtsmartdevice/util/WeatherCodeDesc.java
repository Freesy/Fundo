package com.szkct.weloopbtsmartdevice.util;

import java.util.HashMap;

/**
 * 天气码 和风接口天气code转雅虎接口天气code
 */

public class WeatherCodeDesc {

    /*雅虎接口天气:
    0 :'龙卷风';
    1 :'热带风暴';
    2 :'暴风';
    3 :'大雷雨';
    4 :'雷阵雨';
    5 :'雨夹雪';
    6 :'雨夹雹';
    7 :'雪夹雹';
    8 :'冻雾雨';
    9 :'细雨';
    10 :'冻雨';
    11 :'阵雨';
    12 :'阵雨';
    13 :'阵雪';
    14 :'小阵雪';
    15 :'高吹雪';
    16 :'雪';
    17 :'冰雹';
    18 :'雨淞';
    19 :'粉尘';
    20 :'雾';
    21 :'薄雾';
    22 :'烟雾';
    23 :'大风';
    24 :'风';
    25 :'冷';
    26 :'阴';
    27 :'多云';
    28 :'多云';
    29 :'局部多云';
    30 :'局部多云';
    31 :'晴';
    32 :'晴';
    33 :'转晴';
    34 :'转晴';
    35 :'雨夹冰雹';
    36 :'热';
    37 :'局部雷雨';
    38 :'偶有雷雨';
    39 :'偶有雷雨';
    40 :'偶有阵雨';
    41 :'大雪';
    42 :'零星阵雪';
    43 :'大雪';
    44 : '局部多云';
    45 :'雷阵雨';
    46 :'阵雪';
    47 :'局部雷阵雨';
    */

    /*
    100	晴	Sunny/Clear	100.png
    101	多云	Cloudy	101.png
    102	少云	Few Clouds	102.png
    103	晴间多云	Partly Cloudy	103.png
    104	阴	Overcast	104.png
    200	有风	Windy	200.png
    201	平静	Calm	201.png
    202	微风	Light Breeze	202.png
    203	和风	Moderate/Gentle Breeze	203.png
    204	清风	Fresh Breeze	204.png
    205	强风/劲风	Strong Breeze	205.png
    206	疾风	High Wind, Near Gale	206.png
    207	大风	Gale	207.png
    208	烈风	Strong Gale	208.png
    209	风暴	Storm	209.png
    210	狂爆风	Violent Storm	210.png
    211	飓风	Hurricane	211.png
    212	龙卷风	Tornado	212.png
    213	热带风暴	Tropical Storm	213.png
    300	阵雨	Shower Rain	300.png
    301	强阵雨	Heavy Shower Rain	301.png
    302	雷阵雨	Thundershower	302.png
    303	强雷阵雨	Heavy Thunderstorm	303.png
    304	雷阵雨伴有冰雹	Hail	304.png
    305	小雨	Light Rain	305.png
    306	中雨	Moderate Rain	306.png
    307	大雨	Heavy Rain	307.png
    308	极端降雨	Extreme Rain	308.png
    309	毛毛雨/细雨	Drizzle Rain	309.png
    310	暴雨	Storm	310.png
    311	大暴雨	Heavy Storm	311.png
    312	特大暴雨	Severe Storm	312.png
    313	冻雨	Freezing Rain	313.png
    400	小雪	Light Snow	400.png
    401	中雪	Moderate Snow	401.png
    402	大雪	Heavy Snow	402.png
    403	暴雪	Snowstorm	403.png
    404	雨夹雪	Sleet	404.png
    405	雨雪天气	Rain And Snow	405.png
    406	阵雨夹雪	Shower Snow	406.png
    407	阵雪	Snow Flurry	407.png
    500	薄雾	Mist	500.png
    501	雾	Foggy	501.png
    502	霾	Haze	502.png
    503	扬沙	Sand	503.png
    504	浮尘	Dust	504.png
    507	沙尘暴	Duststorm	507.png
    508	强沙尘暴	Sandstorm	508.png
    900	热	Hot	900.png
    901	冷	Cold	901.png
    999	未知	Unknown	999.png
     */

    public static HashMap<Integer,Integer> weatherCodeMap1 = new HashMap<>();//

    static{
        weatherCodeMap1.put(100,32);
        weatherCodeMap1.put(101,26);
        weatherCodeMap1.put(102,30);
        weatherCodeMap1.put(103,28);
        weatherCodeMap1.put(104,26);
        weatherCodeMap1.put(200,24);
        weatherCodeMap1.put(201,26);
        weatherCodeMap1.put(202,24);
        weatherCodeMap1.put(203,24);
        weatherCodeMap1.put(204,24);
        weatherCodeMap1.put(205,23);
        weatherCodeMap1.put(206,23);
        weatherCodeMap1.put(207,23);
        weatherCodeMap1.put(208,23);
        weatherCodeMap1.put(209,2);
        weatherCodeMap1.put(210,2);
        weatherCodeMap1.put(211,2);
        weatherCodeMap1.put(212,0);
        weatherCodeMap1.put(213,1);
        weatherCodeMap1.put(300,12);
        weatherCodeMap1.put(301,11);
        weatherCodeMap1.put(302,47);
        weatherCodeMap1.put(303,45);
        weatherCodeMap1.put(304,35);
        weatherCodeMap1.put(305,9);
        weatherCodeMap1.put(306,11);
        weatherCodeMap1.put(307,12);
        weatherCodeMap1.put(308,3);
        weatherCodeMap1.put(309,9);
        weatherCodeMap1.put(310,6);
        weatherCodeMap1.put(311,6);
        weatherCodeMap1.put(312,6);
        weatherCodeMap1.put(313,10);
        weatherCodeMap1.put(400,14);
        weatherCodeMap1.put(401,41);
        weatherCodeMap1.put(402,43);
        weatherCodeMap1.put(403,43);
        weatherCodeMap1.put(404,5);
        weatherCodeMap1.put(405,5);
        weatherCodeMap1.put(406,46);
        weatherCodeMap1.put(407,46);
        weatherCodeMap1.put(500,21);
        weatherCodeMap1.put(501,20);
        weatherCodeMap1.put(502,21);
        weatherCodeMap1.put(503,19);
        weatherCodeMap1.put(504,19);
        weatherCodeMap1.put(507,19);
        weatherCodeMap1.put(508,19);
        weatherCodeMap1.put(900,36);
        weatherCodeMap1.put(901,25);
        weatherCodeMap1.put(999,26);

    }

    //获得雅虎天气接口code
    public static int weatherCodeTransform(int code){
        int newCode = 0;

        try{
            if(0<=code&&code<=47){
                newCode = code;
            }else {
                newCode = weatherCodeMap1.get(code);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return newCode;
    }


}
