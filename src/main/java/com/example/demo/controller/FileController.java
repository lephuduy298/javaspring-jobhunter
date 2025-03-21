package com.example.demo.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.domain.dto.file.ResUploadFileDTO;
import com.example.demo.service.FileService;
import com.example.demo.util.annotation.ApiMessage;
import com.example.demo.util.error.StorageException;

@RestController
@RequestMapping("/api/v1")
public class FileController {

    private final AuthController authController;

    @Value("${lephuduy.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    public FileController(FileService fileService, AuthController authController) {
        this.fileService = fileService;
        this.authController = authController;
    }

    @PostMapping("/files")
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> upload(
            @RequestParam("folder") String folder,
            @RequestParam(name = "file", required = false) MultipartFile file)
            throws URISyntaxException, IOException, StorageException {

        // validate
        // validate empty file
        if (file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload a file");
        }

        // validate extension not valid
        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValidExtension = allowedExtensions.stream()
                .anyMatch(item -> fileName.toLowerCase().endsWith("." + item));

        if (!isValidExtension) {
            throw new StorageException("Invalid file extension. Only allow " + allowedExtensions.toString());
        }

        // create folder
        this.fileService.createDirectory(baseURI + folder);

        // save file
        String currentFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(currentFile, Instant.now());
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/files")
    @ApiMessage("download a file")
    public ResponseEntity<Resource> handleDownloadFile(
            @RequestParam(name = "folder", required = false) String folder,
            @RequestParam(name = "fileName", required = false) String fileName)
            throws StorageException, URISyntaxException, FileNotFoundException {

        if (fileName == null || folder == null) {
            throw new StorageException("Missing required params : (fileName or folder) in query params.");
        }

        // check file exist (and not a directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if (fileLength == 0) {
            throw new StorageException("File with name = " + fileName + " not found.");
        }

        // download a file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
