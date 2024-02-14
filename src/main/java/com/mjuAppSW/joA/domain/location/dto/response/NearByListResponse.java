package com.mjuAppSW.joA.domain.location.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Schema(description = "주변 사람 목록 Response")
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class NearByListResponse {
    private final List<NearByInfo> nearByList;

    public static NearByListResponse of(List<NearByInfo> nearByList) {
        return NearByListResponse.builder()
                .nearByList(nearByList)
                .build();
    }
}
