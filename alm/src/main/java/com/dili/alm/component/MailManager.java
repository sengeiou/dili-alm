package com.dili.alm.component;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.dili.alm.domain.Files;
import com.dili.ss.retrofitful.annotation.Restful;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Component
public class MailManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailManager.class);

	@Autowired
	private JavaMailSender mailSender;
	@Value("${alm.contextPath:http://alm.diligrp.com}")
	private String almContextPath;

	@Async
	public void sendMail(String from, String to, String content, String subject, Collection<File> attachments) throws MessagingException {
		this.sendMail(from, to, content, false, subject, attachments);

	}

	@Async
	public void sendMail(String from, String to, String content, boolean html, String subject, Collection<File> attachments) throws MessagingException {
		MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(content, html);
		if (CollectionUtils.isNotEmpty(attachments)) {
			for (File file : attachments) {
				helper.addAttachment(file.getName(), file);
			}
		}
		mailSender.send(mimeMessage);
	}

	@Async
	public void sendRemoteAttachementMail(String from, String to, String[] cc, String content, boolean html, String subject, Collection<Files> attachments) throws MessagingException {
		MimeMessage mimeMessage = this.mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setFrom(from);
		helper.setTo(to);
		if (cc != null) {
			helper.setCc(cc);
		}
		helper.setSubject(subject);
		helper.setText(content, html);
		if (CollectionUtils.isNotEmpty(attachments)) {
			for (Files file : attachments) {
				byte[] bytes = this.requestAttachement(file.getId());
				InputStreamSource iss = new ByteArrayResource(bytes);
				helper.addAttachment(file.getName(), iss);
			}
		}
		mailSender.send(mimeMessage);
	}

	private byte[] requestAttachement(Long id) {
		String url = this.almContextPath + "/files/download?id=" + id;
		OkHttpClient okHttpClient = new OkHttpClient();
		Request request = new Request.Builder().url(url).build();
		Call call = okHttpClient.newCall(request);
		try {
			Response response = call.execute();
			return response.body().bytes();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			return null;
		}
	}
}
