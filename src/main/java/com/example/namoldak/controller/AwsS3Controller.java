package com.example.namoldak.controller;

import com.example.namoldak.util.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
// uploader 호출할 controller
public class AwsS3Controller {

    private final S3Uploader s3Uploader;

    // data로 넘어오는 MultipartFile을 S3Uploader로 전달
    @PostMapping("/upload")
    @ResponseBody
    public String upload(@RequestPart MultipartFile multipartFile) throws IOException {
        return s3Uploader.upload(multipartFile, "static");
        // return ResponseUtil.body...
    }
}
