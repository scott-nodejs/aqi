package com.aqi.service;

public interface CronService {
    void onlyPm25();

    void cronKeyword();

    void cronscanCity();

    void cronscanArea();

    void cronupdateTime();

    void cronSycnWaqi();

    void cronSycnWaqi(long vtime);
}
