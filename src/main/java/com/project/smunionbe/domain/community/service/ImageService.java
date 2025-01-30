package com.project.smunionbe.domain.community.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.project.smunionbe.domain.community.exception.ImageUploadErrorCode;
import com.project.smunionbe.domain.community.exception.ImageUploadException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ImageService{
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName; //버킷 이름
    private String changedImageName(String originName) { //이미지 이름 중복 방지를 위해 랜덤으로 생성
        String random = UUID.randomUUID().toString();
        return random+originName;
    }

    public String uploadImageToS3(MultipartFile image) { //이미지를 S3에 업로드하고 이미지의 url을 반환
        String originName = image.getOriginalFilename(); //원본 이미지 이름
        String ext = originName.substring(originName.lastIndexOf(".")); //확장자
        String changedName = changedImageName(originName); //새로 생성된 이미지 이름
        ObjectMetadata metadata = new ObjectMetadata(); //메타데이터
        metadata.setContentType("image/"+ext);
        try {
            PutObjectResult putObjectResult = amazonS3.putObject(new PutObjectRequest(
                    bucketName, changedName, image.getInputStream(), metadata
            ));

        } catch (IOException e) {
            throw new ImageUploadException(ImageUploadErrorCode.S3_UPLOAD_FAILED);
        }
        return amazonS3.getUrl(bucketName, changedName).toString(); //데이터베이스에 저장할 이미지가 저장된 주소

    }


    public List<String> uploadImages(List<MultipartFile> images){
        // 이미지가 없는 경우 빈 리스트 반환
        if (images == null || images.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> uploadedUrls = new ArrayList<>();

        for (MultipartFile image : images) {
            String storedImagePath = uploadImageToS3(image);
            uploadedUrls.add(storedImagePath);
        }
        return uploadedUrls;
    }

    // 이미지 삭제
    public void deleteImageFromS3(String imageUrl) {
        try {
            // S3에서 삭제할 파일의 key(파일명) 추출
            String fileName = extractFileNameFromUrl(imageUrl);

            // S3에서 파일 삭제
            amazonS3.deleteObject(bucketName, fileName);

        } catch (AmazonServiceException e) {
            throw new ImageUploadException(ImageUploadErrorCode.S3_DELETE_FAILED);
        }
    }


    // 파일 여러개 삭제
    public void deleteMultipleImagesFromS3(List<String> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }

        // S3에서 삭제할 파일 이름(Key) 추출
        String[] objectKeys = imageUrls.stream()
                .map(this::extractFileNameFromUrl)
                .toArray(String[]::new);

        try {
            DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName).withKeys(objectKeys);

            amazonS3.deleteObjects(deleteObjectsRequest);
        } catch (AmazonServiceException e) {
            throw new ImageUploadException(ImageUploadErrorCode.S3_DELETE_FAILED);
        }
    }

    // S3 URL에서 파일명 추출
    private String extractFileNameFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String filePath = url.getPath();
            String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            return URLDecoder.decode(fileName, StandardCharsets.UTF_8.name());
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new ImageUploadException(ImageUploadErrorCode.S3_DELETE_FAILED);
        }
    }



}