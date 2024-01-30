package com.mjuAppSW.joA.domain.member.infrastructure;

public interface ImageUploader {

    String put(Long memberId, String base64Picture);

    boolean delete(String key);
}
