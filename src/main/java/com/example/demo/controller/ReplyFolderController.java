package com.example.demo.controller;

import com.example.demo.service.ReplyFolderImportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reply")
public class ReplyFolderController {

	@Autowired
    private ReplyFolderImportService replyFolderImportService;

    @PostMapping("/import-folder/today")
    public ResponseEntity<?> importToday() {
    	replyFolderImportService.importTodayFolder();
        return ResponseEntity.ok("今天資料夾匯入完成");
    }

    @PostMapping("/import-folder")
    public ResponseEntity<?> importByDate(@RequestParam String date) {
    	replyFolderImportService.importByDate(date);
        return ResponseEntity.ok("資料 " + date + " 匯入完成");
    }
}

