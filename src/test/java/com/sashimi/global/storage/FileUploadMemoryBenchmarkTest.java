package com.sashimi.global.storage;

import com.sun.management.ThreadMXBean;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * FileUploadController.upload()를 image.getBytes() + RequestBody.fromBytes()에서
 * storePrivateStream() (MultipartFile 스트리밍)으로 바꾼 근거를 수치로 확인하기 위한 벤치마크.
 * 실제 HTTP/S3 호출 없이, 두 코드 경로가 힙에 얼마나 할당하는지만 스레드 할당 바이트 기준으로 비교한다.
 */
class FileUploadMemoryBenchmarkTest {

    private static final int FILE_SIZE_BYTES = 10 * 1024 * 1024; // 10MB 이미지 가정

    @Test
    void streamingUploadAllocatesFarLessHeapThanBufferingWholeFile() throws Exception {
        byte[] fileContent = new byte[FILE_SIZE_BYTES];
        new Random(42).nextBytes(fileContent);

        ThreadMXBean threadBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        long threadId = Thread.currentThread().getId();

        // BEFORE: 기존 FileUploadController.upload()가 하던 방식
        long before = threadBean.getThreadAllocatedBytes(threadId);
        InputStream oldSource = new ByteArrayInputStream(fileContent);
        byte[] buffered = oldSource.readAllBytes();
        RequestBody oldBody = RequestBody.fromBytes(buffered);
        long oldAllocated = threadBean.getThreadAllocatedBytes(threadId) - before;

        // AFTER: storePrivateStream()이 하는 방식
        long before2 = threadBean.getThreadAllocatedBytes(threadId);
        InputStream newSource = new ByteArrayInputStream(fileContent);
        RequestBody newBody = RequestBody.fromInputStream(newSource, FILE_SIZE_BYTES);
        long newAllocated = threadBean.getThreadAllocatedBytes(threadId) - before2;

        System.out.printf("%n[BEFORE] getBytes() + RequestBody.fromBytes(): %,d bytes 할당 (파일 크기의 %.1f배)%n",
                oldAllocated, oldAllocated / (double) FILE_SIZE_BYTES);
        System.out.printf("[AFTER]  RequestBody.fromInputStream() 스트리밍: %,d bytes 할당 (파일 크기의 %.3f배)%n",
                newAllocated, newAllocated / (double) FILE_SIZE_BYTES);
        System.out.printf("절감률: %.1f%%%n%n", 100.0 * (1 - newAllocated / (double) oldAllocated));

        assertThat(oldBody.contentLength()).isEqualTo((long) FILE_SIZE_BYTES);
        assertThat(newBody.contentLength()).isEqualTo((long) FILE_SIZE_BYTES);
        assertThat(oldAllocated).isGreaterThanOrEqualTo(FILE_SIZE_BYTES);
        assertThat(newAllocated).isLessThan(oldAllocated / 10);
    }
}
