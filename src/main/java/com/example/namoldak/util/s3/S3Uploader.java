package com.example.namoldak.util.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // multipartFile 전달 받고 S3에 전달할 수 있도록 multiPartFile을 File로 전환
    // S3에 multipartFile은 전송 안됨
    public String upload(MultipartFile multipartFile, String dirName) throws IOException {
        File uploadFile = convert(multipartFile).orElseThrow( // 파일 변환
                () -> new IllegalArgumentException("MultipartFile -> File로 전환 실패")
        );
        return upload(uploadFile, dirName);
    }

    public String upload(File uploadFile, String dirName) { // dirName이란 S3에 생성된 디렉토리
        String fileName = dirName + "/" + uploadFile.getName(); // 파일 이름(디렉토리명 + / + 파일명)
        String uploadImageUrl = putS3(uploadFile, fileName); // 업로드 image url
        removeNewFile(uploadFile); // 로컬에 생성된 File 삭제
        return uploadImageUrl; // 업로드된 파일의 S3 URL 주소 반환
    }

    // 전환된 File을 S3에 public 읽기 권한으로 put
    // 외부에서 정적 파일을 읽을 수 있도록 하기 위함
    public String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 생성된 File 삭제
    // 삭제 시 전체 경로 다 반환
    // multipartFile -> File로 전환되면서 로컬에 파일 생성된 것 삭제
    public void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일 삭제");
        } else {
            log.info("파일 삭제 실패");
        }
    }

    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename());
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
}
