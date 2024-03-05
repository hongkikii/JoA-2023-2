package com.mjuAppSW.joA.domain.member.service.port;

import static com.mjuAppSW.joA.common.constant.Constants.Cache.ID;
import static com.mjuAppSW.joA.common.exception.BusinessException.*;
import static com.mjuAppSW.joA.common.constant.Constants.LoginId.*;

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
        if (id.length() < MIN_LENGTH || id.length() > MAX_LENGTH) {
            throw InvalidLoginIdException;
        }
        Pattern pattern = Pattern.compile(CONDITION);
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
