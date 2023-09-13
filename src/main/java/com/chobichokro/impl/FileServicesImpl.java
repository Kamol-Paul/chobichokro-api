package com.chobichokro.impl;

import com.chobichokro.services.FileServices;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileServicesImpl implements FileServices {
    @Override
    public String uploadImage(String path, MultipartFile file) {
        // File name
        String name = file.getOriginalFilename();
        // creating the path to store the file

        String randomID = UUID.randomUUID().toString();
        assert name != null;
        String fileName1 = randomID.concat(name.substring(name.lastIndexOf(".")));
        // create folder if not exist
        String filePath = path + File.separator + fileName1;

        File f = new File(path);

        // copy the file
        if (!f.exists()) {
            boolean ok = f.mkdir();
        }
        try {
            Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return filePath; // return the file name
    }

    @Override
    public InputStream getImage(String imagePath) throws IOException {
//        String filePath = path + File.separator + imagePath;
        return Files.newInputStream(Paths.get(imagePath));

    }
}
