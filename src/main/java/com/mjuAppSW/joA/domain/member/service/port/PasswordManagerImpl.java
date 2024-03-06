package com.mjuAppSW.joA.domain.member.service.port;

import static com.mjuAppSW.joA.common.exception.BusinessException.*;
import static com.mjuAppSW.joA.common.constant.Constants.Password.*;

import com.mjuAppSW.joA.domain.member.infrastructure.PasswordManager;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordManagerImpl implements PasswordManager {

    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

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
        String allCharacters = UPPER + LOWER + DIGITS + SPECIAL_CHARS;
        StringBuilder builder = new StringBuilder();

        while (builder.length() < MAX_LENGTH) {
            int index = RANDOM.nextInt(allCharacters.length());
            char randomChar = allCharacters.charAt(index);
            builder.append(randomChar);
        }
        return builder.toString();
    }

    @Override
    public void validate(String rawPassword) {
        Pattern regexPattern = Pattern.compile(CONDITION);
        Matcher matcher = regexPattern.matcher(rawPassword);
        if (!matcher.matches()) {
            throw InvalidPasswordException;
        }
    }

    @Override
    public void compare(String originalPassword, String InputPassword) {
        if (!originalPassword.equals(InputPassword)) {
            throw PasswordNotFoundException;
        }
    }
}
