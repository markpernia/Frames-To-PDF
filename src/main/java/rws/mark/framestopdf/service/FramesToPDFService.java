package rws.mark.framestopdf.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

@Service
public class FramesToPDFService {

    public ByteArrayResource createPDF(List<MultipartFile> frames, String figFileName) {
        try (PDDocument document = new PDDocument()) {
            for (MultipartFile file : frames) {
                try (InputStream inputStream = file.getInputStream()) {
                    PDImageXObject image = PDImageXObject.createFromByteArray(document, inputStream.readAllBytes(), null);
                    PDPage page = new PDPage();
                    document.addPage(page);

                    try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                        contentStream.drawImage(image, 0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                    }
                }
            }

            // Write the document to a ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            document.close();

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create PDF", e);
        }
    }
}
