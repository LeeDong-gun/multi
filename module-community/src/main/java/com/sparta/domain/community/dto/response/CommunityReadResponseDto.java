package com.sparta.domain.community.dto.response;

import com.sparta.domain.community.document.CommunityDocument;
import com.sparta.domain.community.entity.CommunityEntity;
import com.sparta.domain.game.entity.GameEntity;
import com.sparta.domain.user.entity.UserEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommunityReadResponseDto {
	private final String communityId;
	private final String title;
	private final String author; //글을 생성한 사용자의 email
	private final String game; // 글이 포함된 game의 name;

	public static CommunityReadResponseDto from(CommunityDocument communityDocument) {
		return new CommunityReadResponseDto(communityDocument.getId(), communityDocument.getTitle(), communityDocument.getUserEmail(),
			communityDocument.getGameName());
	}

	public static CommunityReadResponseDto of(CommunityEntity community, UserEntity user, GameEntity game) {
		return new CommunityReadResponseDto(String.valueOf(community.getId()), community.getTitle(), user.getEmail(), game.getName());
	}
}
