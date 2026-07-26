package com.task.ecommerce.service;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.task.ecommerce.service.dto.InvoiceData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final TemplateEngine templateEngine;

    /**
     * Renders the invoice.html Thymeleaf template with the given data
     * and returns the resulting HTML as a string.
     */
    public String renderHtml(InvoiceData invoice) {
        Context context = new Context();
        context.setVariable("invoice", invoice);
        return templateEngine.process("invoice", context);
    }

    /**
     * Converts a rendered HTML string into PDF bytes using openhtmltopdf.
     * Pass the same HTML you plan to store/send (e.g. the Bill.htmlSnapshot),
     * so the PDF and the emailed HTML body always match.
     */
    public byte[] renderPdf(String html) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(os);
            builder.run();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate invoice PDF", e);
        }
    }
}