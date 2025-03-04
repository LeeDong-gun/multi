package com.sparta.domain.email.event;

import com.sparta.domain.email.dto.request.SendEmailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailEvent {
	SendEmailDto sendEmailDto;
}
