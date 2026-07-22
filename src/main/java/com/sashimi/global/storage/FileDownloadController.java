package com.sashimi.global.storage;

import com.sashimi.global.exception.BusinessException;
import com.sashimi.global.exception.ErrorCode;
import com.sashimi.instructorapplication.domain.repository.InstructorApplicationRepository;
import com.sashimi.security.principal.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileDownloadController {

    private final FileStoragePort fileStoragePort;
    private final InstructorApplicationRepository instructorApplicationRepository;

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam String key,
                                            @AuthenticationPrincipal CustomUserPrincipal principal) {
        if (key.contains("..")) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        if (!hasFileAccess(principal, key)) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        byte[] bytes = fileStoragePort.downloadPrivate(key);

        String filename = key.contains("/") ? key.substring(key.lastIndexOf('/') + 1) : key;
        MediaType contentType = resolveContentType(filename);

        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(bytes);
    }

    /**
     * 공개 이미지(썸네일·강사 프로필) 전용 프록시. 비로그인도 접근 가능(SecurityConfig permitAll).
     * 비공개 서류 노출을 막기 위해 images/ 폴더 key만 허용하고, 인라인으로 표시한다.
     */
    @GetMapping("/images")
    public ResponseEntity<byte[]> image(@RequestParam String key) {
        if (key.contains("..") || !key.startsWith("images/")) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        byte[] bytes = fileStoragePort.downloadPrivate(key);

        String filename = key.substring(key.lastIndexOf('/') + 1);
        MediaType contentType = resolveContentType(filename);

        // key가 업로드마다 새로 발급되는 UUID라 같은 key는 항상 같은 내용을 가리킴 -> 영구 캐시 가능
        return ResponseEntity.ok()
                .contentType(contentType)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .body(bytes);
    }

    private boolean hasFileAccess(CustomUserPrincipal principal, String key) {
        boolean isAdmin = principal.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return true;
        }
        return instructorApplicationRepository.findUserIdByFileKey(key)
                .map(ownerId -> ownerId.equals(principal.getId()))
                .orElse(false);
    }

    private MediaType resolveContentType(String filename) {
        String lower = filename.toLowerCase();
        if (lower.endsWith(".pdf"))  return MediaType.APPLICATION_PDF;
        if (lower.endsWith(".png"))  return MediaType.IMAGE_PNG;
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        if (lower.endsWith(".docx")) return MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
