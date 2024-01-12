package com.mjuAppSW.joA.geography.location.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NearByInfo {
    private final Long id;
    private final String name;
    private final String urlCode;
    private final String bio;
    private final Boolean isLiked;
}
