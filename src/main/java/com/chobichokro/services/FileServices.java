package com.chobichokro.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public interface FileServices {
    String uploadImage(String path, MultipartFile file) throws IOException;
    InputStream getImage(String imagePath) throws IOException;

}
