package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;
import static com.mjuAppSW.joA.common.constant.Constants.S3Uploader.ERROR;

import com.mjuAppSW.joA.domain.heart.HeartRepository;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.MemberEntity;
import com.mjuAppSW.joA.domain.member.dto.request.BioRequest;
import com.mjuAppSW.joA.domain.member.dto.response.MyPageResponse;
import com.mjuAppSW.joA.domain.member.dto.request.PictureRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SettingPageResponse;
import com.mjuAppSW.joA.domain.member.exception.InvalidS3Exception;
import com.mjuAppSW.joA.domain.member.infrastructure.ImageUploader;
import com.mjuAppSW.joA.domain.vote.VoteRepository;
import com.mjuAppSW.joA.domain.member.dto.response.VotePageResponse;
import com.mjuAppSW.joA.domain.member.dto.response.LocationPageResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfoService {

    private final MemberService memberService;
    private final ImageUploader imageUploader;
    private final HeartRepository heartRepository;
    private final VoteRepository voteRepository;

    public SettingPageResponse getSettingPage(Long sessionId) {
        Member member = memberService.findNormalBySessionId(sessionId);
        return SettingPageResponse.of(member);
    }

    public MyPageResponse getMyPage(Long sessionId) {
        Member member = memberService.findNormalBySessionId(sessionId);

        int todayHeart = heartRepository.countTodayHeartsById(member.getId());
        int totalHeart = heartRepository.countTotalHeartsById(member.getId());
        List<String> voteTop3 = voteRepository.findVoteCategoryById(member.getId(), PageRequest.of(0, 3));

        return MyPageResponse.of(member, todayHeart, totalHeart, voteTop3);
    }

    public VotePageResponse getVotePage(Long sessionId) {
        return VotePageResponse.of(memberService.findNormalBySessionId(sessionId));
    }

    public LocationPageResponse getLocationPage(Long sessionId) {
        return LocationPageResponse.of(memberService.findNormalBySessionId(sessionId));
    }

    @Transactional
    public void transBio(BioRequest request) {
        Member member = memberService.findNormalBySessionId(request.getId());
        memberService.updateBio(member, request.getBio());
    }

    @Transactional
    public void deleteBio(Long sessionId) {
        Member member = memberService.findNormalBySessionId(sessionId);
        memberService.updateBio(member, EMPTY_STRING);
    }

    @Transactional
    public void transPicture(PictureRequest request) {
        Member member = memberService.findNormalBySessionId(request.getId());

        if (!isBasicPicture(member.getUrlCode())){
            imageUploader.delete(member.getUrlCode());
        }
        String newUrlCode = imageUploader.put(member.getId(), request.getBase64Picture());
        if(newUrlCode.equals(ERROR)) {
            throw new InvalidS3Exception();
        }
        memberService.updateUrlCode(member, newUrlCode);
    }

    private boolean isBasicPicture(String urlCode) {
        return urlCode.equals(EMPTY_STRING);
    }

    @Transactional
    public void deletePicture(Long sessionId) {
        Member member = memberService.findNormalBySessionId(sessionId);
        if(isBasicPicture(member.getUrlCode())) {
            return;
        }
        if (imageUploader.delete(member.getUrlCode())) {
            memberService.updateUrlCode(member, EMPTY_STRING);
            return;
        }
        throw new InvalidS3Exception();
    }
}
