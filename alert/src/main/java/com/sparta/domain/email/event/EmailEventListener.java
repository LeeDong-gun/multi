package com.sparta.domain.email.event;

import com.sparta.domain.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
public class EmailEventListener {
	private final EmailService emailService;

	@Async
	@EventListener
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void eventListener(EmailEvent emailEvent) {
		emailService.mailSender(emailEvent.getSendEmailDto());
	}
}
