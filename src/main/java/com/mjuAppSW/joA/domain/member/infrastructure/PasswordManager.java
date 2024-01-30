package com.mjuAppSW.joA.domain.member.infrastructure;

public interface PasswordManager {

    String createHashed(String rawPassword, String salt);

    String createSalt();

    String createRandom();

    void validate(String rawPassword);

    void compare(String originalPassword, String InputPassword);
}
