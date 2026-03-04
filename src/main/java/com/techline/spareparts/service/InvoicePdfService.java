package com.techline.spareparts.service;

import com.techline.spareparts.entity.Sale;
import com.techline.spareparts.entity.SaleItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generateInvoicePdf(Sale sale) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            document.add(new Paragraph("TechLine Computers - Invoice", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Invoice #: " + sale.getId()));
            document.add(new Paragraph("Date: " + sale.getSaleDate().format(DATE_FORMAT)));
            if (sale.getCustomer() != null) {
                document.add(new Paragraph("Customer: " + sale.getCustomer().getName()));
                if (sale.getCustomer().getEmail() != null) document.add(new Paragraph("Email: " + sale.getCustomer().getEmail()));
            }
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell(new PdfPCell(new Phrase("Product")));
            table.addCell(new PdfPCell(new Phrase("Code")));
            table.addCell(new PdfPCell(new Phrase("Qty")));
            table.addCell(new PdfPCell(new Phrase("Unit Price")));
            table.addCell(new PdfPCell(new Phrase("Total")));

            for (SaleItem item : sale.getItems()) {
                table.addCell(item.getProduct().getName());
                table.addCell(item.getProduct().getCode());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(item.getUnitPrice().toString());
                table.addCell(item.getTotalPrice().toString());
            }
            document.add(table);
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total: " + sale.getTotalAmount().toString(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));

            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}
