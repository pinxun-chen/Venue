package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class DailyBillingScheduler {

	@Autowired
    private BillingExportService exportService;
	@Autowired
    private MailHelper mailHelper;

    @Value("${report.recipient}") 
    private String recipient; // 逗號可分多位

    // 每天下午 5 點（台北時區）跑
    @Scheduled(cron = "0 0 17 * * *", zone = "Asia/Taipei")
    public void runDaily() {
    	// 取得今天日期（台北時區）
        LocalDate todayTpe = LocalDate.now(ZoneId.of("Asia/Taipei"));
        try {
        	// 生成 CSV 報表內容
            byte[] csv = exportService.generateCsvFor(todayTpe);
            // 將 CSV 存成檔案
            Path file = exportService.writeCsvToFile(todayTpe, csv);
            // 處理收件人清單（去除空白與空字串）
            List<String> tos = Arrays.stream(recipient.split(","))
                                     .map(String::trim)
                                     .filter(s -> !s.isBlank())
                                     .toList();
            // 設定信件標題與內容
            String subject = "[日結報表] " + todayTpe.format(DateTimeFormatter.BASIC_ISO_DATE);
            String body = "附件為今日（" + todayTpe + "）的結算 CSV 報表。";
            // 寄出帶附件的報表
            mailHelper.sendWithAttachment(tos, subject, body, file);

            System.out.println("Daily billing sent: " + file);
        } catch (Exception e) {
            // 這裡可接 Slack/Line 通知或重試
            e.printStackTrace();
        }
    }
}
