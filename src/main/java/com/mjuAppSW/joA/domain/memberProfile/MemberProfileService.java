package com.mjuAppSW.joA.domain.memberProfile;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;
import static com.mjuAppSW.joA.common.constant.Constants.S3Uploader.ERROR;

import com.mjuAppSW.joA.common.auth.MemberChecker;
import com.mjuAppSW.joA.domain.heart.HeartRepository;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.memberProfile.dto.request.BioRequest;
import com.mjuAppSW.joA.domain.memberProfile.dto.response.MyPageResponse;
import com.mjuAppSW.joA.domain.memberProfile.dto.request.PictureRequest;
import com.mjuAppSW.joA.domain.memberProfile.dto.response.SettingPageResponse;
import com.mjuAppSW.joA.domain.memberProfile.exception.InvalidS3Exception;
import com.mjuAppSW.joA.domain.vote.VoteRepository;
import com.mjuAppSW.joA.common.storage.S3Uploader;
import com.mjuAppSW.joA.domain.memberProfile.dto.response.VotePageResponse;
import com.mjuAppSW.joA.domain.memberProfile.dto.response.LocationPageResponse;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final HeartRepository heartRepository;
    private final VoteRepository voteRepository;
    private final MemberChecker memberChecker;
    private final S3Uploader s3Uploader;

    public SettingPageResponse getSettingPage(Long sessionId) {
        Member member = memberChecker.findBySessionId(sessionId);
        return SettingPageResponse.of(member);
    }

    public MyPageResponse getMyPage(Long sessionId) {
        Member member = memberChecker.findBySessionId(sessionId);

        int todayHeart = heartRepository.countTodayHeartsById(LocalDate.now(), member.getId());
        int totalHeart = heartRepository.countTotalHeartsById(member.getId());
        List<String> voteTop3 = voteRepository.findVoteCategoryById(member.getId(), PageRequest.of(0, 3));

        return MyPageResponse.of(member, todayHeart, totalHeart, voteTop3);
    }

    public VotePageResponse getVotePage(Long sessionId) {
        return VotePageResponse.of(memberChecker.findBySessionId(sessionId));
    }

    public LocationPageResponse getLocationPage(Long sessionId) {
        return LocationPageResponse.of(memberChecker.findBySessionId(sessionId));
    }

    @Transactional
    public void transBio(BioRequest request) {
        Member member = memberChecker.findBySessionId(request.getId());
        member.changeBio(request.getBio());
    }

    @Transactional
    public void deleteBio(Long sessionId) {
        Member member = memberChecker.findBySessionId(sessionId);
        member.changeBio(EMPTY_STRING);
    }

    @Transactional
    public void transPicture(PictureRequest request) {
        Member member = memberChecker.findBySessionId(request.getId());

        if (!isBasic(member.getUrlCode())){
            s3Uploader.deletePicture(member.getUrlCode());
        }
        String newUrlCode = s3Uploader.putPicture(member.getId(), request.getBase64Picture());
        if(newUrlCode.equals(ERROR)) {
            throw new InvalidS3Exception();
        }
        member.changeUrlCode(newUrlCode);
    }

    private boolean isBasic(String urlCode) {
        if(urlCode.equals(EMPTY_STRING)) {
            return true;
        }
        return false;
    }

    @Transactional
    public void deletePicture(Long sessionId) {
        Member member = memberChecker.findBySessionId(sessionId);
        if(isBasic(member.getUrlCode())) {
            return;
        }
        if (s3Uploader.deletePicture(member.getUrlCode())) {
            member.changeUrlCode(EMPTY_STRING);
            return;
        }
        throw new InvalidS3Exception();
    }
}
