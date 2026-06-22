package com.sashimi.member.infrastructure;

import com.sashimi.member.application.port.DocxPort;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class DocxAdapter implements DocxPort {

    @Override
    public List<String> extractMainCareers(byte[] fileBytes) {
        try (XWPFDocument doc = new XWPFDocument(new ByteArrayInputStream(fileBytes))) {
            StringBuilder sb = new StringBuilder();
            for (XWPFParagraph para : doc.getParagraphs()) {
                sb.append(para.getText()).append(" ");
            }

            String text = sb.toString();
            log.info("이력서 docx 추출 텍스트: {}", text);

            int sectionStart = text.indexOf("주요 이력");
            if (sectionStart == -1) {
                sectionStart = text.indexOf("주요이력");
            }
            if (sectionStart == -1) {
                return List.of();
            }
            String careerSection = text.substring(sectionStart);

            List<String> careers = new ArrayList<>();
            Pattern pattern = Pattern.compile("([1-5])\\s+([^1-5위]{5,})");
            Matcher matcher = pattern.matcher(careerSection);
            while (matcher.find() && careers.size() < 5) {
                String content = matcher.group(2).trim();
                if (!content.contains("자격증, 수상, 주요 경험") && !content.contains("내용")) {
                    careers.add(content);
                }
            }

            return careers;

        } catch (Exception e) {
            log.error("이력서 docx 파싱 중 예외 발생", e);
            return List.of();
        }
    }
}
