package io.github.froideexplica.service;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class EmailService {

    @Inject
    Mailer mailer;

    public void enviarEmailComAnexo(PDDocument anexo, String destinatario) throws IOException {
        if (destinatario == null || destinatario.isBlank()) {
            return;
        }

        byte[] pdfBytes = getBytesFromPDDocument(anexo);

        Mail mail = Mail.withText(
                destinatario,
                "Conta Detalhada",
                "Caro cliente, segue em anexo um PDF contendo sua conta detalhada."
        );

        mail.addAttachment(
                "NotaDetalhada.pdf",
                pdfBytes,
                "application/pdf"
        );

        mailer.send(mail);
    }

    private byte[] getBytesFromPDDocument(PDDocument document) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }
}