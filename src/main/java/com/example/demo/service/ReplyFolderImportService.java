package com.example.demo.service;

public interface ReplyFolderImportService {

    // 匯入今天的 PRACTISE_REPLY CSV
    void importTodayFolder();

    // 匯入指定 yyyyMMdd 的 PRACTISE_REPLY CSV 
    void importByDate(String yyyymmdd);
}
