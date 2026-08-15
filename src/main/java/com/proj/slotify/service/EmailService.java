package com.proj.slotify.service;

import com.proj.slotify.entity.BookingEntity;
import com.proj.slotify.entity.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender javaMailSender;

    public void sendBookingConfirmation(BookingEntity booking, UserEntity host) throws MessagingException{
        String subject = "Booking Confirmed - "+booking.getBookingId();
        String content = buildBookingConfirmationHtml(booking, host);

        sendHtmlEmail(booking.getGuestEmail(), subject, content);
        logger.info("[EmailService] Booking confirmation sent to {} for booking {}", booking.getGuestEmail(), booking.getBookingId());
    }

    public void sendBookingCancellation(BookingEntity booking, UserEntity host) throws MessagingException{
        String subject = "Booking Cancelled - "+booking.getBookingId();
        String content = buildBookingCancellationHtml(booking, host);

        sendHtmlEmail(booking.getGuestEmail(), subject, content);
        logger.info("[EmailService] Booking cancellation sent to {} for booking {}", booking.getGuestEmail(), booking.getBookingId());
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException{
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }

    private String buildBookingConfirmationHtml(BookingEntity booking, UserEntity host){
        return "<html><body>"+
                "<h2>Booking Confirmed!</h2>" +
                "<p>Hello " + escapeHtml(booking.getGuestName()) + ",</p>" +
                "<p>Your booking with <strong>" + escapeHtml(host.getName()) + "</strong> has been confirmed.</p>" +
                "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse;'>" +
                "<tr><th>Booking ID</th><td>" + escapeHtml(booking.getBookingId()) + "</td></tr>" +
                "<tr><th>Status</th><td>" + booking.getStatus().name() + "</td></tr>" +
                "<tr><th>Start Time</th><td>" + booking.getStartTime() + "</td></tr>" +
                "<tr><th>End Time</th><td>" + booking.getEndTime() + "</td></tr>" +
                "</table>" +
                "<p>If you need to cancel, please contact the host.</p>" +
                "</body></html>";
    }

    private String buildBookingCancellationHtml(BookingEntity booking, UserEntity host){
        return "<html><body>" +
                "<h2>Booking Cancelled</h2>" +
                "<p>Hello " + escapeHtml(booking.getGuestName()) + ",</p>" +
                "<p>Your booking with <strong>" + escapeHtml(host.getName()) + "</strong> has been cancelled.</p>" +
                "<table border='1' cellpadding='8' cellspacing='0' style='border-collapse:collapse;'>" +
                "<tr><th>Booking ID</th><td>" + escapeHtml(booking.getBookingId()) + "</td></tr>" +
                "<tr><th>Status</th><td>CANCELLED</td></tr>" +
                "</table>" +
                "</body></html>";
    }
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
