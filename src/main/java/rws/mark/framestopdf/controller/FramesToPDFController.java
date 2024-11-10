package rws.mark.framestopdf.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import rws.mark.framestopdf.service.FramesToPDFService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class FramesToPDFController {

    private final FramesToPDFService framesToPDFService;

    @PostMapping("/process-frames")
    public ResponseEntity<ByteArrayResource> processFrames(
            @RequestParam List<MultipartFile> frames,
            @RequestParam String figFileName) {
        try {
            // Generate PDF and get the result as a byte array
            ByteArrayResource resource = framesToPDFService.createPDF(frames, figFileName);

            // Set up the response headers
            HttpHeaders headers = new HttpHeaders();
            String sanitizedFileName = figFileName.trim().replaceAll("[^a-zA-Z0-9_-]", "_") + ".pdf";
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + sanitizedFileName + "\"");
            headers.setContentType(MediaType.APPLICATION_PDF);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }
}