package com.example.namoldak.controller;

import com.example.namoldak.util.s3.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

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