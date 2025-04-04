package com.function.GraphQL;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class SendEmailDataFetcher implements DataFetcher<String> {
    @Override
    public String get(DataFetchingEnvironment environment) {
        String correo = environment.getArgument("correo");
        String nombreMascota = environment.getArgument("nombreMascota");

        if (correo == null || correo.isEmpty() || nombreMascota == null || nombreMascota.isEmpty()) {
            return "Error: correo y nombreMascota son obligatorios.";
        }

        try {
            String subject = "Agendamiento de mascota: " + nombreMascota;
            String body = "Se ha realizado un nuevo agendamiento para la mascota: " + nombreMascota;

            String host = "smtp.gmail.com";
            String from = "kotesepulveda28@gmail.com"; // Tu correo
            String password = "ieou biui ilct wvqg"; // Tu contraseña de aplicación Gmail

            Properties properties = new Properties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(from, password);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(correo));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            return "Correo enviado correctamente";
        } catch (MessagingException e) {
            return "Error al enviar correo: " + e.getMessage();
        }
    }
}
