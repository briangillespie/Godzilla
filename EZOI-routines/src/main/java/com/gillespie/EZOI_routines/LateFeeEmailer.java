package com.gillespie.EZOI_routines;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class LateFeeEmailer {

    private static final String USERNAME = "NUSeaGodzilla@northeastern.edu";
    private static final String FROM = USERNAME;
    private static final String PASSWORD = "@EZoffice123";

    public void sendMail(String to){
        String subject = "Late Fee for Non-returned Item";
        String body = "This is an auto-generated message from " + USERNAME + ".";
        subject = "DO NOT REPLY: " + subject;
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "outlook.office365.com");
        props.put("mail.smtp.host", "outlook.office365.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM, "NU-Seattle Student Resources"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to, "Someone"));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);

        } catch (AddressException e) {
            System.out.println("Invalid sending or receiving address.");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("Error sending message.");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unable to send this message.");
            e.printStackTrace();
        }
    }


    public void sendMail(String to, String subject, String body){

        subject = "DO NOT REPLY: " + subject;
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "outlook.office365.com");
        props.put("mail.smtp.host", "outlook.office365.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USERNAME, PASSWORD);
                    }
                });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(FROM, "NU-Seattle Student Resources"));
            msg.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(to, "Someone"));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);

        } catch (AddressException e) {
            System.out.println("Invalid sending or receiving address.");
            e.printStackTrace();
        } catch (MessagingException e) {
            System.out.println("Error sending message.");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            System.out.println("Unable to send this message.");
            e.printStackTrace();
        }
    }
    public static void main( String[] args )
    {
        LateFeeEmailer lfe = new LateFeeEmailer();
        String[] recipients = {"bng1290@gmail.com"};
        String subj = "Late Fee for Non-returned Item";
        String msgBody = "This is an auto-generated message from " + USERNAME + ".";
        for (String recipient : recipients){
            System.out.println("Sending mail to " + recipient +"...");
            lfe.sendMail(recipient, subj, msgBody);
            System.out.println("Mail sent!");
        }
    }
}
