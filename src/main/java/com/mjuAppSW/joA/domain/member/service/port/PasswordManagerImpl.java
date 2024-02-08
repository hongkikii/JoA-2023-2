package com.mjuAppSW.joA.domain.member.service.port;

import com.mjuAppSW.joA.domain.member.exception.InvalidPasswordException;
import com.mjuAppSW.joA.domain.member.exception.PasswordNotFoundException;
import com.mjuAppSW.joA.domain.member.infrastructure.PasswordManager;
import java.security.SecureRandom;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordManagerImpl implements PasswordManager {

    @Override
    public String createHashed(String rawPassword, String salt) {
        return BCrypt.hashpw(rawPassword, salt);
    }

    @Override
    public String createSalt() {
        return BCrypt.gensalt();
    }

    @Override
    public String createRandom() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = upper.toLowerCase();
        String digits = "0123456789";
        String specialChars = "!@#$%^&*()-_+=<>?";

        String allCharacters = upper + lower + digits + specialChars;
        int maxLength = 16;
        Random random = new SecureRandom();
        StringBuilder builder = new StringBuilder();

        while (builder.length() < maxLength) {
            int index = random.nextInt(allCharacters.length());
            char randomChar = allCharacters.charAt(index);
            builder.append(randomChar);
        }
        return builder.toString();
    }

    @Override
    public void validate(String rawPassword) {
        String pattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=]).{8,16}$";
        Pattern regexPattern = Pattern.compile(pattern);
        Matcher matcher = regexPattern.matcher(rawPassword);
        if (!matcher.matches()) {
            throw new InvalidPasswordException();
        }
    }

    @Override
    public void compare(String originalPassword, String InputPassword) {
        if (!originalPassword.equals(InputPassword)) {
            throw new PasswordNotFoundException();
        }
    }
}
