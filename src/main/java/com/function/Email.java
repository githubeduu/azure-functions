package com.function;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class Email {

    @FunctionName("SendEmail")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<EmailRequest> request,
            final ExecutionContext context) {

        // Extraer los parámetros directamente del cuerpo de la solicitud
        EmailRequest emailRequest = request.getBody();
        
        if (emailRequest == null || emailRequest.getCorreo() == null || emailRequest.getNombreMascota() == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Por favor, proporciona un correo y el nombre de la mascota en la solicitud.")
                    .build();
        }

        String to = emailRequest.getCorreo();
        String nombreMascota = emailRequest.getNombreMascota();
        String subject = "Agendamiento de mascota: " + nombreMascota;
        String body = "Se ha realizado un nuevo agendamiento para la mascota: " + nombreMascota;

        // Configuración de la cuenta SMTP (ejemplo con Gmail)
        String host = "smtp.gmail.com";  // SMTP de Gmail
        String from = "kotesepulveda28@gmail.com";  // Reemplaza con tu correo
        String password = "ieou biui ilct wvqg";  // Reemplaza con tu contraseña

        // Configuración de las propiedades del correo
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        try {
            // Crear sesión de correo
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            // Crear mensaje
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            // Enviar correo
            Transport.send(message);

            context.getLogger().info("Correo enviado correctamente");

            // Retornar respuesta
            return request.createResponseBuilder(HttpStatus.OK).body("Correo enviado correctamente").build();
        } catch (MessagingException e) {
            context.getLogger().severe("Error al enviar correo: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al enviar el correo").build();
        }
    }
}
