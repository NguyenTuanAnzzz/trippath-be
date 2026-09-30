package com.an.pathtrip.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content) {

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    "UTF-8"
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(buildEmailTemplate(content), true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Không thể gửi email", e);
        }
    }

    private String buildEmailTemplate(String content) {

        return """
                <!DOCTYPE html>
                <html lang="vi">

                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                </head>

                <body style="
                    margin: 0;
                    padding: 0;
                    background-color: #EEF0F2;
                    font-family: Arial, Helvetica, sans-serif;
                ">

                    <div style="
                        max-width: 600px;
                        margin: 40px auto;
                        background-color: #FFFFFF;
                        border-radius: 16px;
                        overflow: hidden;
                        box-shadow: 0 10px 35px rgba(20, 20, 20, 0.15);
                    ">

                        <!-- Header -->
                        <div style="
                            padding: 32px 30px;
                            text-align: center;
                            background-color: #141414;
                        ">

                            <h1 style="
                                margin: 0;
                                color: #EEC643;
                                font-size: 30px;
                                font-weight: 700;
                                letter-spacing: 1px;
                            ">
                                PathTrip
                            </h1>

                            <p style="
                                margin: 8px 0 0;
                                color: #EEF0F2;
                                font-size: 14px;
                            ">
                                Khám phá hành trình của bạn
                            </p>

                        </div>


                        <!-- Content -->
                        <div style="
                            padding: 40px 35px;
                            color: #141414;
                            background-color: #FFFFFF;
                        ">

                            <div style="
                                font-size: 16px;
                                line-height: 1.7;
                            ">
                                %s
                            </div>

                        </div>


                        <!-- Accent -->
                        <div style="
                            height: 5px;
                            background-color: #EEC643;
                        "></div>


                        <!-- Footer -->
                        <div style="
                            padding: 22px;
                            text-align: center;
                            background-color: #141414;
                            color: #EEF0F2;
                            font-size: 12px;
                        ">

                            <p style="
                                margin: 0;
                                color: #EEC643;
                                font-weight: 600;
                            ">
                                © 2026 PathTrip
                            </p>

                            <p style="
                                margin: 6px 0 0;
                                color: #EEF0F2;
                            ">
                                Your journey starts here.
                            </p>

                        </div>

                    </div>

                </body>

                </html>
                """.formatted(content);
    }
}