package com.aqi.global;

import java.util.HashMap;
import java.util.Map;

public class GlobalConstant {

    public static final String token = "5980c4eee4d951e729ee88592815c7c4341ab8b2";

    public static final String keywordUrl = "https://api.waqi.info/search/?keyword=";

    public static final String aqiUrl = "https://api.waqi.info/feed/";

    public static final String LOCK_KEYWORD = "lock_keyword";

    public static final String LOCK_SCANCITY = "lock_scanCity";

    public static final String LOCK_SCANAREA = "lock_scanArea";

    public static final String LOCK_UPDATETIME = "lock_updatetime";

    public static final String ALL_AREA = "ALL:AREA";

    public static final String MER_HOUR_AQI = "MER:HOUR:AQI";

    public static final int a[][] = {{0,154,0,500},
                                     {155,404,510,1000},
                                     {405,654,1010,1500},
                                     {655,1504,151,2000},
                                     {1505,2504,2010,3000},
                                     {2505,3504,3010,4000},
                                     {3505,5004,4010,5000}};
    public static final int c[][] = {{0,350,0,500},
                                    {350,750,510,1000},
                                    {750,1150,1010,1500},
                                    {1150,1500,151,2000},
                                    {1500,2500,2010,3000},
                                    {2500,3500,3010,4000},
                                    {3500,5000,4010,5000}};

    public static final int GOOD_PARA = 10;

    public static final int JUSTSOSO_PARA = 5;

    public static final int LIGHT_PARA = 1;

    public static final int Z_PARA = -3;

    public static final int BAD_PARA = -5;

    public static final int BADEST_PARA = -10;

    public static final String RANK = "CITY:AQI:RANK";

    public static final String CITY_UIDS = "CITY:UIDS";
}
