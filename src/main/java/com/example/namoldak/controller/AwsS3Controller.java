package com.example.namoldak.controller;

import com.example.namoldak.util.s3.AwsS3Service;
import com.example.namoldak.util.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
// uploader 호출할 controller
public class AwsS3Controller {

    private final AwsS3Service awsS3Service;

    // data로 넘어오는 MultipartFile을 AwsS3Service로 전달

//     @PostMapping(value = "/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//     public String uploadImage(@RequestPart List<MultipartFile> imagelist,
//                               @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException{
//         awsS3Service.upload(imagelist,"static",userDetails.getMember());
//        return "success";
//     }
}
