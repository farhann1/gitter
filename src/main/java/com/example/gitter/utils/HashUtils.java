package com.example.gitter.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.example.gitter.constants.Constants.HASH_ALGORITHM;
import static com.example.gitter.constants.Messages.ERROR_HASH_ALGORITHM_NOT_FOUND;

public class HashUtils {
    
    public static String hashFile(Path filePath) throws IOException {
        byte[] fileBytes = Files.readAllBytes(filePath);
        return hashBytes(fileBytes);
    }
    
    public static String hashBytes(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(bytes);
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(String.format(ERROR_HASH_ALGORITHM_NOT_FOUND, HASH_ALGORITHM), e);
        }
    }
}
