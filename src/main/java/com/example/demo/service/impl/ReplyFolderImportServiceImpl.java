package com.example.demo.service.impl;

import com.example.demo.service.ReplyCsvImportService;
import com.example.demo.service.ReplyFolderImportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ReplyFolderImportServiceImpl implements ReplyFolderImportService {

	@Autowired
    private ReplyCsvImportService csvImportService;

    @Value("${app.reply.base-dir}")
    private String baseDir;

    private static final DateTimeFormatter DATE8 = DateTimeFormatter.BASIC_ISO_DATE;

    @Override
    public void importTodayFolder() {
        String today = LocalDate.now(ZoneId.of("Asia/Taipei")).format(DATE8);
        importByDate(today);
    }

    @Override
    public void importByDate(String yyyymmdd) {
        Path folder = Paths.get(baseDir, yyyymmdd);
        String pattern = "PRACTISE_REPLY_" + yyyymmdd + "_*.csv";

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(folder, pattern)) {
            boolean found = false;
            for (Path p : ds) {
                found = true;
                csvImportService.importReplyCsv(p);
            }
            if (!found) {
                throw new RuntimeException("找不到符合檔名格式的檔案：" + folder + "/" + pattern);
            }
        } catch (NoSuchFileException e) {
            throw new RuntimeException("資料夾不存在: " + folder);
        } catch (IOException e) {
            throw new RuntimeException("讀取資料夾失敗: " + e.getMessage(), e);
        }
    }
}
