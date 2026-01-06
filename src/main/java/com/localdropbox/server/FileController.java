package com.localdropbox.server;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private Path userDir(Authentication auth) throws Exception {
        Path dir = Paths.get("storage", auth.getName());
        Files.createDirectories(dir);
        return dir;
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         Authentication auth) throws Exception {

        Path dir = userDir(auth);

        Files.copy(file.getInputStream(),
                   dir.resolve(file.getOriginalFilename()),
                   StandardCopyOption.REPLACE_EXISTING);

        return "uploaded";
    }

    @GetMapping("/files")
    public List<String> listFiles(Authentication auth) throws Exception {

        return Files.list(userDir(auth))
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename,
                                             Authentication auth) throws Exception {

        Path file = userDir(auth).resolve(filename).normalize();
        @SuppressWarnings("null")
        Resource resource = new UrlResource(file.toUri());

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
