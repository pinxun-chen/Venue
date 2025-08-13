package com.example.demo.service;

import java.nio.file.Path;

public interface ReplyCsvImportService {
    
    //從檔案路徑匯入 PRACTISE_REPLY CSV
    Integer importReplyCsv(Path path);
}