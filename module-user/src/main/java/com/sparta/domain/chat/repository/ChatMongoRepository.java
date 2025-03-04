package com.sparta.domain.chat.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sparta.domain.chat.document.ChatMessage;

public interface ChatMongoRepository extends MongoRepository<ChatMessage, String> {
	List<ChatMessage> findByChatroomId(String chatroomId);
}
