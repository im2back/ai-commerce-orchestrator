package io.github.froideexplica.service;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import io.github.froideexplica.dto.output.ProductDataToPdf;
import io.github.froideexplica.model.Customer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import org.apache.pdfbox.pdmodel.font.PDType1Font;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class PdfGenerator {

    @Inject
    EmailService emailService;

    public void generatePdf(List<ProductDataToPdf> productsList, Customer customer, String nomeArquivo) {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Cliente: " + customer.getName());
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Documento: " + customer.getDocument());
            contentStream.endText();

            float yPosition = 750;

            for (ProductDataToPdf product : productsList) {
                if (yPosition < 100) {
                    contentStream.close();

                    PDPage newPage = new PDPage(PDRectangle.A4);
                    document.addPage(newPage);

                    contentStream = new PDPageContentStream(document, newPage);
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    yPosition = 800;
                }

                yPosition -= 50;

                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Nome do Produto : " + product.name());
                contentStream.endText();

                yPosition -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Preço Unitario : " + product.price());
                contentStream.endText();

                yPosition -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Quantidade : " + product.quantity());
                contentStream.endText();

                yPosition -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("total : " + product.price().multiply(new BigDecimal(product.quantity())));
                contentStream.endText();

                yPosition -= 20;
                contentStream.beginText();
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText("Data/Hora da compra  : " + product.data());
                contentStream.endText();
            }

            yPosition -= 40;
            contentStream.beginText();
            contentStream.newLineAtOffset(50, yPosition);
            contentStream.showText("Total Da conta  : " + customer.getTotal());
            contentStream.endText();

            contentStream.close();

            emailService.enviarEmailComAnexo(document, customer.getEmail());

            document.save(nomeArquivo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}