package com.aqi.schduel;

import com.aqi.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class keywordTask {

//    @Autowired
//    private CronService cronService;
//
//    /**
//     * 可以在keyword表中输入城市 会自动扫描keyword表 拿到城市
//     */
//    @Scheduled(cron = "0 0/2 * * * ?")
//    public void keyWord(){
//        cronService.cronKeyword();
//    }
//
//    /**
//     * 扫描城市表 向消息队列发送任务
//     */
//    @Scheduled(cron = "0 0/5 * * * ?")
//    public void scanCity(){
//        cronService.cronscanCity();
//    }
//
//
//    /**
//     * 扫描区域表 向消息队列发送任务
//     */
//    @Scheduled(cron = "0 30,35,40,45,50,55 * * * ?")
//    public void scanArea(){
//       cronService.cronscanArea();
//    }
//
//    /**
//     * 定时更新updatetime字段
//     */
//    @Scheduled(cron = "0 0 * * *  ?")
//    public void updateTime(){
//        cronService.cronupdateTime();
//    }
//
//    /**
//     * 随机爬取附近节点的pm2.5
//     */
//    @Scheduled(cron = "0 0/10 * * * ?")
//    public void rand(){
//        cronService.onlyPm25();
//    }
//
//    /**
//     * 随机爬取附近节点的pm2.5
//     */
//    @Scheduled(cron = "0 30 * * * ?")
//    public void sycnWaqi(){
//        cronService.cronSycnWaqi();
//    }
//
//    /**
//     * 随机爬取附近节点的pm2.5
//     */
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void MouthAqi(){
//        cronService.cronMouthAqi();
//    }
//
//    /**
//     * 生成排行榜
//     */
//    @Scheduled(cron = "0 30 * * * ?")
//    public void genRank(){
//        cronService.crongenRank();
//    }
//
//    /**
//     * 定期存储一天的排行榜
//     */
//    @Scheduled(cron = "0 55 23 * * ?")
//    public void insertRank(){
//        cronService.cronInsterRank();
//    }
}
