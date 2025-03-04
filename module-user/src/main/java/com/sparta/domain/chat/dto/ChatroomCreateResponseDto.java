package com.sparta.domain.chat.dto;

import java.util.List;

import com.sparta.domain.chat.document.Participant;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Builder
@Getter
@RequiredArgsConstructor
public class ChatroomCreateResponseDto {

	private final String chatroomId;
	private final String title;
	private final List<Participant> participants;

}
