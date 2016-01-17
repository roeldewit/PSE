package com.pse.fotoz.helpers;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * Helper class handling the sending of basic html emails.
 * @author Robert
 */
public class EmailHelper {
    private final Properties properties = new Properties();
    private final Authenticator authenticator;
    
    private EmailHelper(String protocol, String host, String port, 
            String username, String password) {        
        properties.put("mail.transport.protocol", protocol);
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable","true");
        authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
    }
    
    /**
     * Creates an EmailHelper from a configuration file.
     * The following existing values are assumed: 
     * <i>email.outgoing.server.protocol, email.outgoing.server.host, 
     * email.outgoing.server.port, email.outgoing.server.username, 
     * email.outgoing.server.password</i>
     * @param config The configuration file.
     * @return EmailHelper with the given configuration.
     */
    public static EmailHelper fromConfig(XMLConfiguration config) {
        String protocol = config.getString("email.outgoing.server.protocol");
        String host = config.getString("email.outgoing.server.host");
        String port = config.getString("email.outgoing.server.port");
        String username = config.getString("email.outgoing.server.username");
        String password = config.getString("email.outgoing.server.password");
        
        return new EmailHelper(protocol, host, port, username, password);
    }
    
    /**
     * Creates an EmailHelper from configuration values.
     * @param protocol The mail protocol to use (e.g. SMTP).
     * @param host The server host name.
     * @param port The server port.
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @return EmailHelper with the given configuration.
     */
    public static EmailHelper from(String protocol, String host, String port, 
            String username, String password) {
        return new EmailHelper(protocol, host, port, username, password);
    }
    
    /**
     * Sends an html email.
     * @param html The html body of the email.
     * @param subject The subject line.
     * @param to A single recipient.
     * @param from The originating email address.
     * @throws MessagingException If a configuration or transport error 
     * occurred.
     */
    public void sendEmailHTML(String html, String subject, String to, 
            String from) throws MessagingException {
        sendEmailHTML(html, subject, Arrays.asList(to), from);
    }
    
    /**
     * Sends an html email.
     * @param html The html body of the email.
     * @param subject The subject line.
     * @param to The list of recipients.
     * @param from The originating email address.
     * @throws MessagingException If a configuration or transport error 
     * occurred.
     */
    public void sendEmailHTML(String html, String subject, List<String> to, 
            String from) throws MessagingException {
        Session session = Session.getInstance(properties, authenticator);      
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        for (String s : to) {
            message.addRecipient(Message.RecipientType.TO, 
                    new InternetAddress(s));
        }
        message.setSubject(subject);
        message.setContent(html, "text/html");
        
        Transport.send(message);
    }
}
