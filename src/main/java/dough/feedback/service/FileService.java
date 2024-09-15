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
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file, Long questId) {
        File uploadFile = convert(file, questId);
        String imageUrl = uploadToS3(uploadFile, questId);
        removeNewFile(uploadFile);
        return imageUrl;
    }

    public File convert(MultipartFile multipartFile, Long questId) {
        String uniqueFileName = UUID.randomUUID().toString() + "-" +
                questId + "-" +
                Objects.requireNonNull(multipartFile.getOriginalFilename());

        File file = new File(uniqueFileName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException | FileNotFoundException e) {
            throw new RuntimeException();
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private String uploadToS3(File uploadFile, Long questId) {
        String s3Key = "images/" + questId + "/" + uploadFile.getName();
        amazonS3.putObject(bucket, s3Key, uploadFile);
        return amazonS3.getUrl(bucket, s3Key).toString();
    }

    private void removeNewFile(File uploadFile) {
        if (!uploadFile.delete()) {
            throw new RuntimeException("Failed to delete temporary file");
        }
    }
}