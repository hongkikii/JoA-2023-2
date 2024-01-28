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
import com.mjuAppSW.joA.domain.memberProfile.dto.response.UserInfoResponse;
import com.mjuAppSW.joA.domain.memberProfile.exception.InvalidS3Exception;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.room.RoomRepository;
import com.mjuAppSW.joA.domain.room.exception.RoomNotFoundException;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import com.mjuAppSW.joA.domain.roomInMember.exception.RoomInMemberNotFoundException;

import com.mjuAppSW.joA.domain.memberProfile.vo.UserInfoVO;
import com.mjuAppSW.joA.domain.vote.VoteRepository;
import com.mjuAppSW.joA.common.storage.S3Uploader;
import com.mjuAppSW.joA.domain.memberProfile.dto.response.VotePageResponse;
import com.mjuAppSW.joA.domain.memberProfile.dto.response.LocationPageResponse;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberProfileService {

    private final HeartRepository heartRepository;
    private final VoteRepository voteRepository;
    private final RoomRepository roomRepository;
    private final RoomInMemberRepository roomInMemberRepository;
    private final MemberChecker memberChecker;
    private final S3Uploader s3Uploader;

    public SettingPageResponse getSettingPage(Long sessionId) {
        Member member = memberChecker.findFilterBySessionId(sessionId);
        return SettingPageResponse.of(member);
    }

    public MyPageResponse getMyPage(Long sessionId) {
        Member member = memberChecker.findFilterBySessionId(sessionId);

        int todayHeart = heartRepository.countTodayHeartsById(member.getId());
        int totalHeart = heartRepository.countTotalHeartsById(member.getId());
        List<String> voteTop3 = voteRepository.findVoteCategoryById(member.getId(), PageRequest.of(0, 3));

        return MyPageResponse.of(member, todayHeart, totalHeart, voteTop3);
    }

    public VotePageResponse getVotePage(Long sessionId) {
        return VotePageResponse.of(memberChecker.findFilterBySessionId(sessionId));
    }

    public LocationPageResponse getLocationPage(Long sessionId) {
        return LocationPageResponse.of(memberChecker.findFilterBySessionId(sessionId));
    }

    public UserInfoResponse getUserInfo(Long roomId, Long memberId){
        Room room = roomRepository.findById(roomId).orElseThrow(RoomNotFoundException::new);
        Member member = memberChecker.findBySessionId(memberId);
        RoomInMember roomInMember = roomInMemberRepository.findByRoomAndMember(room, member).orElseThrow(
            RoomInMemberNotFoundException::new);

        UserInfoVO userInfoVO = roomInMemberRepository.getUserInfo(roomInMember.getRoom(), roomInMember.getMember());
        return UserInfoResponse.of(userInfoVO.getName(), userInfoVO.getUrlCode(), userInfoVO.getBio());
    }

    @Transactional
    public void transBio(BioRequest request) {
        Member member = memberChecker.findFilterBySessionId(request.getId());
        member.changeBio(request.getBio());
    }

    @Transactional
    public void deleteBio(Long sessionId) {
        Member member = memberChecker.findFilterBySessionId(sessionId);
        member.changeBio(EMPTY_STRING);
    }

    @Transactional
    public void transPicture(PictureRequest request) {
        Member member = memberChecker.findFilterBySessionId(request.getId());

        if (!isBasicPicture(member.getUrlCode())){
            s3Uploader.deletePicture(member.getUrlCode());
        }
        String newUrlCode = s3Uploader.putPicture(member.getId(), request.getBase64Picture());
        if(newUrlCode.equals(ERROR)) {
            throw new InvalidS3Exception();
        }
        member.changeUrlCode(newUrlCode);
    }

    private boolean isBasicPicture(String urlCode) {
        return urlCode.equals(EMPTY_STRING);
    }

    @Transactional
    public void deletePicture(Long sessionId) {
        Member member = memberChecker.findFilterBySessionId(sessionId);
        if(isBasicPicture(member.getUrlCode())) {
            return;
        }
        if (s3Uploader.deletePicture(member.getUrlCode())) {
            member.changeUrlCode(EMPTY_STRING);
            return;
        }
        throw new InvalidS3Exception();
    }
}
