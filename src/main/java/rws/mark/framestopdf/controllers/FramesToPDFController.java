package rws.mark.framestopdf.controllers;

import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/")
public class FramesToPDFController {

    @PostMapping("create-pdf")
    public ResponseEntity<?> createPDF(
            @RequestParam("file") List<MultipartFile> frames,
            @RequestParam(value = "fileName", defaultValue = "Combined_Frames") String fileName) {

        try (PDDocument document = new PDDocument()) {
            for (MultipartFile file : frames) {
                InputStream inputStream = file.getInputStream();
                PDImageXObject image = PDImageXObject.createFromByteArray(document, inputStream.readAllBytes(), null);
                PDPage page = new PDPage();
                document.addPage(page);

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.drawImage(image, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);

            HttpHeaders headers = new HttpHeaders();
            String sanitizedFileName = fileName.trim().replaceAll("[^a-zA-Z0-9_-]", "_");
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + sanitizedFileName + ".pdf");
            headers.add(HttpHeaders.CONTENT_TYPE, "application/pdf");

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
