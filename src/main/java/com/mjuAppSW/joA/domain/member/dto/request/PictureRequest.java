package com.mjuAppSW.joA.domain.member.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "프로필 사진 변경 Request")
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class PictureRequest {
    @Schema(description = "사용자 세션 id")
    @NotNull
    private final Long id;
    @Schema(description = "이미지를 base 64로 인코딩한 문자열")
    @NotBlank
    private final String base64Picture;
}
