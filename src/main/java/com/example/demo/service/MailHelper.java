package com.example.demo.service;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.nio.file.Path;
import java.util.List;


@Component
public class MailHelper {

	// Spring Boot 提供的郵件發送工具（在 application.properties 設定 SMTP 後可用）
    @Autowired
	private JavaMailSender mailSender;

    public void sendWithAttachment(List<String> to, String subject, String text, Path attachment) throws MessagingException {
    	// 建立一封 MIME 郵件（支援附件、HTML 內容等）
    	MimeMessage msg = mailSender.createMimeMessage();
    	// true 表示這封信支援「多部分內容」（multipart），UTF-8 防止中文亂碼
    	MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
    	// 設定收件人（List 轉 String[]）
    	helper.setTo(to.toArray(new String[0]));
    	// 設定郵件主旨
    	helper.setSubject(subject);
    	// 設定郵件內容（false 表示純文字，true 則表示 HTML）
    	helper.setText(text, false);
    	// 加入附件（名稱使用檔案名稱，內容使用檔案本體）
    	helper.addAttachment(attachment.getFileName().toString(), attachment.toFile());
    	// 發送郵件
    	mailSender.send(msg);
    }
}

