package com.dili.alm.component;

import java.io.File;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class MailManager {

	@Autowired
	private JavaMailSender mailSender;

	public void sendMail(String from, String to, String content, String subject, Collection<File> attachments)
			throws MessagingException {
		MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(content);
		if (CollectionUtils.isNotEmpty(attachments)) {
			for (File file : attachments) {
				helper.addAttachment(file.getName(), file);
			}
		}
		mailSender.send(mimeMessage);
	}
}
