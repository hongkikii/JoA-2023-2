package com.mjuAppSW.joA.domain.member.service.port;

import static com.mjuAppSW.joA.common.constant.Constants.Cache.ID;
import static com.mjuAppSW.joA.common.exception.BusinessException.*;

import com.mjuAppSW.joA.common.exception.BusinessException;
import com.mjuAppSW.joA.domain.member.infrastructure.CacheManager;
import com.mjuAppSW.joA.domain.member.infrastructure.LoginIdManager;
import com.mjuAppSW.joA.domain.member.service.MemberQueryService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Builder
@RequiredArgsConstructor
public class LoginIdManagerImpl implements LoginIdManager {

    private final MemberQueryService memberQueryService;
    private final CacheManager cacheManager;

    @Override
    public void validate(String id) {
        if (id.length() < 5 || id.length() > 20) {
            throw InvalidLoginIdException;
        }
        String regex = "^[a-z0-9-_]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(id);
        if(!matcher.matches()){
            throw InvalidLoginIdException;
        }
    }

    @Override
    public void checkNotCache(Long key, String loginId) {
        if(!cacheManager.compare(ID + key, loginId)){
            throw LoginIdNotAuthorizedException;
        }
    }

    @Override
    public void checkInCache(Long key, String loginId) {
        if (cacheManager.isExistedValue(ID, loginId)) {
            if(!cacheManager.compare(ID + key, loginId)) {
                throw LoginIdAlreadyExistedException;
            }
        }
    }

    @Override
    public void checkInDb(String loginId) {
        memberQueryService.validateNoExistedLoginId(loginId);
    }

}
