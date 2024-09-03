package dough.feedback.service;

import com.amazonaws.services.s3.AmazonS3;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {
        File uploadFile = convert(file);
        String imageUrl = uploadToS3(uploadFile);
        removeNewFile(uploadFile);
        return imageUrl;
    }

    public File convert(MultipartFile multipartFile) {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException | FileNotFoundException e) {
            throw new RuntimeException();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private String uploadToS3(File uploadFile) {
        amazonS3.putObject(bucket, uploadFile.getName(), uploadFile);
        return amazonS3.getUrl(bucket, uploadFile.getName()).toString();
    }

    private void removeNewFile(File uploadFile) {
        uploadFile.delete();
    }
}