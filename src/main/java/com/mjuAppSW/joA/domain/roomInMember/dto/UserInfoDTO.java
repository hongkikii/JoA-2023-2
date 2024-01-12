package com.mjuAppSW.joA.domain.roomInMember.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    private String name;
    private String urlCode;
    private String bio;

    public UserInfoDTO(String name, String urlCode, String bio) {
        this.name = name;
        this.urlCode = urlCode;
        this.bio = bio;
    }
}
