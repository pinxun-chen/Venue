package com.example.demo.controller;

import com.example.demo.service.ReplyFolderImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reply")
@RequiredArgsConstructor
public class ReplyFolderController {

    private final ReplyFolderImportService folderService;

    @PostMapping("/import-folder/today")
    public ResponseEntity<?> importToday() {
        folderService.importTodayFolder();
        return ResponseEntity.ok("今天資料夾匯入完成");
    }

    @PostMapping("/import-folder")
    public ResponseEntity<?> importByDate(@RequestParam String date) {
        folderService.importByDate(date);
        return ResponseEntity.ok("資料夾 " + date + " 匯入完成");
    }
}

