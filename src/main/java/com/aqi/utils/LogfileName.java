package com.aqi.utils;

public enum LogfileName {

    BIZ_IP("ipLog"),
    BIZ_LOCATION("locationLog"),
    BIZ_IP_CLIENT("ipLogClient");

    private String logFileName;

    LogfileName(String fileName) {
        this.logFileName = fileName;
    }

    public String getLogfileName() {
        return logFileName;
    }

    public void setLogfileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public static LogfileName getAwardTypeEnum(String value) {
        LogfileName[] arr = values();
        for (LogfileName item : arr) {
            if (null != item && item.logFileName != null) {
                return item;
            }
        }
        return null;
    }
}
