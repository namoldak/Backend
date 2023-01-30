package com.example.namoldak.util.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.namoldak.domain.ImageFile;
import com.example.namoldak.domain.Member;
import com.example.namoldak.domain.Post;
import com.example.namoldak.repository.ImageFileRepository;
import com.example.namoldak.util.GlobalResponse.CustomException;
import com.example.namoldak.util.GlobalResponse.code.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.namoldak.util.GlobalResponse.code.StatusCode.FILE_CONVERT_FAILED;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class AwsS3Service {

    private final AmazonS3Client amazonS3Client;
    private final ImageFileRepository imageFileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloud_front.file_url_format}")
    private String cloudfront;

    // multipartFile 전달 받고 S3에 전달할 수 있도록 multiPartFile을 File로 전환
    // S3에 multipartFile은 전송 안됨
    public void upload(List<MultipartFile> multipartFilelist, String dirName, Post post, Member member) {
        for (MultipartFile multipartFile : multipartFilelist) {
            if (multipartFile != null) {
                File uploadFile = convert(multipartFile).orElseThrow(() -> new IllegalArgumentException("파일 전환 실패"));
                ImageFile imageFile = new ImageFile(upload(uploadFile, dirName), member, post); //url, user, post 정보 저장
                imageFileRepository.save(imageFile);
            }
        }
    }

    private String upload(File uploadFile, String dirName) { // dirName이란 S3에 생성된 디렉토리
        String fileName = dirName + "/" + UUID.randomUUID(); // 파일 이름(디렉토리명 + / + 랜덤 + 파일명)
//        String uploadImageUrl = putS3(uploadFile, fileName); // 업로드 image url
        putS3(uploadFile, fileName);
//        String newUrl = "https://" + cloudfront + "/" + fileName;
        String newUrl = "https://" + bucket + "/" + fileName;
        removeNewFile(uploadFile); // 로컬에 생성된 File 삭제
        return newUrl; // 업로드된 파일의 S3 URL 주소 반환
    }

    // 전환된 File을 S3에 public 읽기 권한으로 put
    // 외부에서 정적 파일을 읽을 수 있도록 하기 위함
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 생성된 File 삭제
    // 삭제 시 전체 경로 다 반환
    // multipartFile -> File로 전환되면서 로컬에 파일 생성된 것 삭제
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            log.info("파일 삭제");
            return;
        }
        log.info("파일 삭제 실패");
    }

    private Optional<File> convert(MultipartFile multipartFile) {
        try {
            File convertFile = new File(multipartFile.getOriginalFilename());
            if (convertFile.createNewFile()) {
                try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                    fos.write(multipartFile.getBytes());
                }
                return Optional.of(convertFile);
            }
            return Optional.empty();
        } catch (IOException e) {
            throw new CustomException(FILE_CONVERT_FAILED);
        }
    }

    // find image from s3
    public String getThumbnailPath(String path) {
        return amazonS3Client.getUrl(bucket, path).toString();
    }

    //remove s3 object
    public void deleteFile(String fileName){
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, fileName);
        amazonS3Client.deleteObject(request);
    }
}
