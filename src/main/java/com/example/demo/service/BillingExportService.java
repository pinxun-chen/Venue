package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import java.sql.Timestamp;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class BillingExportService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// 從 application.properties 讀取設定值
	@Value("${report.merchant-code}")
	private String merchantCode;

	@Value("${report.output-dir}")
	private String outputDir; // 輸出檔案的資料夾路徑

	// 日期格式：yyyyMMdd
	private static final DateTimeFormatter D8 = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

	// 產生指定日期的 CSV 報表（傳回 byte[] 格式）
	public byte[] generateCsvFor(LocalDate billDate) throws IOException {
		// 查詢範圍：當天 00:00 ~ 隔天 00:00
		LocalDate start = billDate;
		LocalDate end = billDate.plusDays(1);

		// 交易明細查詢（只取成功）
		String detailSql = """
				    SELECT pay_datetime, pay_amount
				    FROM payments
				    WHERE pay_datetime >= ? AND pay_datetime < ?
				      AND (
				           pay_status = 2
				        OR pay_status = '2'
				        OR pay_status_desc = N'繳費成功'
				        OR pay_status = 'SUCCESS'
				      )
				    ORDER BY pay_datetime, id
				""";

		LocalDate buildDate = LocalDate.now(ZoneId.of("Asia/Taipei"));
		String headerYmd = buildDate.format(D8);

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF }); // UTF-8 BOM（Excel 相容）

		try (OutputStreamWriter osw = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
				PrintWriter pw = new PrintWriter(osw)) {

			// H,<檔案日期>,<請款日期>
			pw.printf("H,%s,%s%n", headerYmd, headerYmd);

			// 逐筆輸出 D，同時計算總筆數與總金額
			final int[] totalCount = { 0 };
			final long[] totalAmt = { 0 };

			jdbcTemplate.query(detailSql, ps -> {
				ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
				ps.setTimestamp(2, Timestamp.valueOf(end.atStartOfDay()));
			}, rs -> {
				String txnDate = rs.getTimestamp("pay_datetime").toLocalDateTime().toLocalDate().format(D8);

				long amount = rs.getLong("pay_amount");
				long discount = 0L;
				long actual = amount - discount;

				// D,<交易日期>,<商店代號>,<交易序號留空>,<交易金額>,<折扣金額0>,<實際交易金額>
				pw.printf("D,%s,%s,,%d,%d,%d%n", txnDate, merchantCode, amount, discount, actual);

				totalCount[0]++;
				totalAmt[0] += actual; // 折扣都 0，= amount
			});

			// F,<總筆數>,<總金額>,<處理日期留空>,<入帳日期留空>
			pw.printf("F,%d,%d,,", totalCount[0], totalAmt[0]);
		}

		return baos.toByteArray();
	}

	/** 把 CSV bytes 落地到檔案，回傳完整路徑 */
	public Path writeCsvToFile(LocalDate billDate, byte[] csvBytes) throws IOException {

		LocalDate buildDate = LocalDate.now(ZoneId.of("Asia/Taipei"));
		String ymd = buildDate.format(D8);

		// 取商店主碼（P10012-1 -> P10012），若沒設就 fallback 成 P10012
		String merchantForFile = (merchantCode != null && !merchantCode.isBlank()) ? merchantCode.split("-")[0]
				: "P10012";

		// 最終檔案名稱
		String fname = String.format("PRACTISE_%s_%s.csv", ymd, merchantForFile);

		// 子資料夾名稱：日期
		Path dir = Paths.get(outputDir, ymd);
		Files.createDirectories(dir);

		// 寫入檔案（若存在則覆蓋）
		Path file = dir.resolve(fname);
		Files.write(file, csvBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

		return file;
	}

	
	public record DailySummary(int totalCount, long totalAmount) {
	}

	public DailySummary getSummary(LocalDate billDate) {
		LocalDate start = billDate;
		LocalDate end = billDate.plusDays(1);

		String sql = """
				    SELECT COUNT(*) AS total_count,
				           COALESCE(SUM(pay_amount), 0) AS sum_before
				    FROM payments
				    WHERE pay_datetime >= ? AND pay_datetime < ?
				      AND (
				           pay_status = 2
				        OR pay_status = '2'
				        OR pay_status_desc = N'繳費成功'
				        OR pay_status = 'SUCCESS'
				      )
				""";

		return jdbcTemplate.query(sql, ps -> {
			ps.setTimestamp(1, Timestamp.valueOf(start.atStartOfDay()));
			ps.setTimestamp(2, Timestamp.valueOf(end.atStartOfDay()));
		}, rs -> {
			if (rs.next()) {
				return new DailySummary(rs.getInt("total_count"), rs.getLong("sum_before"));
			}
			return new DailySummary(0, 0L);
		});
	}

}
