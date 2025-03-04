package com.sparta.domain.email.event;

import com.sparta.domain.email.dto.request.SendEmailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmailEventPublisher {
	private final ApplicationEventPublisher applicationEventPublisher;

	public void publisher(SendEmailDto sendEmailDto) {
		applicationEventPublisher.publishEvent(new EmailEvent(sendEmailDto));
	}
}
