package com.sparta.domain.email.service;

import com.sparta.domain.email.dto.request.SendEmailDto;

public interface EmailService {

	void mailSender(SendEmailDto sendEmailDto);

}