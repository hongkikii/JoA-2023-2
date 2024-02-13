package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;

import com.mjuAppSW.joA.domain.heart.repository.HeartRepository;
import com.mjuAppSW.joA.domain.member.entity.Member;
import com.mjuAppSW.joA.domain.member.dto.request.BioRequest;
import com.mjuAppSW.joA.domain.member.dto.response.MyPageResponse;
import com.mjuAppSW.joA.domain.member.dto.request.PictureRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SettingPageResponse;
import com.mjuAppSW.joA.domain.member.dto.response.ChattingPageResponse;
import com.mjuAppSW.joA.domain.member.infrastructure.ImageUploader;
import com.mjuAppSW.joA.domain.member.vo.UserInfoVO;
import com.mjuAppSW.joA.domain.room.entity.Room;
import com.mjuAppSW.joA.domain.room.service.RoomService;
import com.mjuAppSW.joA.domain.roomInMember.entity.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.service.RoomInMemberService;
import com.mjuAppSW.joA.domain.member.dto.response.VotePageResponse;
import com.mjuAppSW.joA.domain.member.dto.response.LocationPageResponse;
import com.mjuAppSW.joA.domain.vote.repository.VoteRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Builder
@RequiredArgsConstructor
public class InfoService {

    private final MemberQueryService memberQueryService;
    private final RoomService roomService;
    private final RoomInMemberService roomInMemberService;
    private final ImageUploader imageUploader;
    private final HeartRepository heartRepository;
    private final VoteRepository voteRepository;

    public SettingPageResponse getSettingPage(Long sessionId) {
        return SettingPageResponse.of(memberQueryService.getNormalBySessionId(sessionId));
    }

    public MyPageResponse getMyPage(Long sessionId) {
        Member member = memberQueryService.getNormalBySessionId(sessionId);
        int todayHeart = heartRepository.countTodayHeartsById(member.getId());
        int totalHeart = heartRepository.countTotalHeartsById(member.getId());
        List<String> voteTop3 = voteRepository.findVoteCategoryById(
                member.getId(), PageRequest.of(0, 3));

        return MyPageResponse.of(member, todayHeart, totalHeart, voteTop3);
    }

    public ChattingPageResponse getChattingPage(Long roomId, Long memberId){
        Room room = roomService.findByRoomId(roomId);
        Member member = memberQueryService.getBySessionId(memberId);
        RoomInMember roomInMember = roomInMemberService.findByRoomAndMember(room, member);

        UserInfoVO userInfoVO = roomInMemberService.findOpponentUserInfoByRoomAndMember(roomInMember.getRoom(), roomInMember.getMember());
        return ChattingPageResponse.of(userInfoVO.getName(), userInfoVO.getUrlCode(), userInfoVO.getBio());
    }

    public VotePageResponse getVotePage(Long sessionId) {
        return VotePageResponse.of(memberQueryService.getNormalBySessionId(sessionId));
    }

    public LocationPageResponse getLocationPage(Long sessionId) {
        return LocationPageResponse.of(memberQueryService.getNormalBySessionId(sessionId));
    }

    @Transactional
    public void transBio(BioRequest request) {
        Member member = memberQueryService.getNormalBySessionId(request.getId());
        member.updateBio(request.getBio());
    }

    @Transactional
    public void deleteBio(Long sessionId) {
        Member member = memberQueryService.getNormalBySessionId(sessionId);
        member.deleteBio();
    }

    @Transactional
    public void transPicture(PictureRequest request) {
        Member member = memberQueryService.getNormalBySessionId(request.getId());
        if (isNotBasicPicture(member.getUrlCode())){
            imageUploader.delete(member.getUrlCode());
        }
        String newUrlCode = imageUploader.put(member.getId(), request.getBase64Picture());
        member.updateUrlCode(newUrlCode);
    }

    @Transactional
    public void deletePicture(Long sessionId) {
        Member member = memberQueryService.getNormalBySessionId(sessionId);
        if(isNotBasicPicture(member.getUrlCode())) {
            imageUploader.delete(member.getUrlCode());
            member.deleteUrlCode();
        }
    }

    private boolean isNotBasicPicture(String urlCode) {
        return !urlCode.equals(EMPTY_STRING);
    }
}
