package com.sparta.domain.user.dto.request;



import static com.sparta.domain.user.dto.UserValidMessage.EMAIL_NOT_VALID;

import com.fasterxml.jackson.annotation.JsonProperty;


import com.sparta.config.annotaion.FormToJson;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@FormToJson
public class ResetPasswordDto {

	@NotBlank
	@JsonProperty(value = "nickName")
	private String nickName;

	@JsonProperty(value = "email")
	@Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = EMAIL_NOT_VALID)
	private String email;

}
