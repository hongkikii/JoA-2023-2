package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;

import com.mjuAppSW.joA.domain.heart.HeartRepository;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.dto.request.BioRequest;
import com.mjuAppSW.joA.domain.member.dto.response.MyPageResponse;
import com.mjuAppSW.joA.domain.member.dto.request.PictureRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SettingPageResponse;
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
        Member member = memberService.getNormalBySessionId(sessionId);
        return SettingPageResponse.of(member);
    }

    public MyPageResponse getMyPage(Long sessionId) {
        Member member = memberService.getNormalBySessionId(sessionId);

        int todayHeart = heartRepository.countTodayHeartsById(member.getId());
        int totalHeart = heartRepository.countTotalHeartsById(member.getId());
        List<String> voteTop3 = voteRepository.findVoteCategoryById(
                member.getId(), PageRequest.of(0, 3));

        return MyPageResponse.of(member, todayHeart, totalHeart, voteTop3);
    }

    public VotePageResponse getVotePage(Long sessionId) {
        return VotePageResponse.of(memberService.getNormalBySessionId(sessionId));
    }

    public LocationPageResponse getLocationPage(Long sessionId) {
        return LocationPageResponse.of(memberService.getNormalBySessionId(sessionId));
    }

    @Transactional
    public void transBio(BioRequest request) {
        Member member = memberService.getNormalBySessionId(request.getId());
        member.updateBio(request.getBio());
    }

    @Transactional
    public void deleteBio(Long sessionId) {
        Member member = memberService.getNormalBySessionId(sessionId);
        member.deleteBio();
    }

    @Transactional
    public void transPicture(PictureRequest request) {
        Member member = memberService.getNormalBySessionId(request.getId());

        if (!isBasicPicture(member.getUrlCode())){
            imageUploader.delete(member.getUrlCode());
        }
        String newUrlCode = imageUploader.put(member.getId(), request.getBase64Picture());
        member.updateUrlCode(newUrlCode);
    }

    private boolean isBasicPicture(String urlCode) {
        return urlCode.equals(EMPTY_STRING);
    }

    @Transactional
    public void deletePicture(Long sessionId) {
        Member member = memberService.getNormalBySessionId(sessionId);
        if(isBasicPicture(member.getUrlCode())) {
            return;
        }
        imageUploader.delete(member.getUrlCode());
        member.deleteUrlCode();
    }
}
