package com.mjuAppSW.joA.domain.member.service;

import static com.mjuAppSW.joA.common.constant.Constants.EMPTY_STRING;

import com.mjuAppSW.joA.domain.heart.repository.HeartRepository;
import com.mjuAppSW.joA.domain.member.Member;
import com.mjuAppSW.joA.domain.member.dto.request.BioRequest;
import com.mjuAppSW.joA.domain.member.dto.response.MyPageResponse;
import com.mjuAppSW.joA.domain.member.dto.request.PictureRequest;
import com.mjuAppSW.joA.domain.member.dto.response.SettingPageResponse;
import com.mjuAppSW.joA.domain.member.dto.response.ChattingPageResponse;
import com.mjuAppSW.joA.domain.member.exception.MemberNotFoundException;
import com.mjuAppSW.joA.domain.member.infrastructure.ImageUploader;
import com.mjuAppSW.joA.domain.member.vo.UserInfoVO;
import com.mjuAppSW.joA.domain.room.Room;
import com.mjuAppSW.joA.domain.room.RoomRepository;
import com.mjuAppSW.joA.domain.room.exception.RoomNotFoundException;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMember;
import com.mjuAppSW.joA.domain.roomInMember.RoomInMemberRepository;
import com.mjuAppSW.joA.domain.roomInMember.exception.RoomInMemberNotFoundException;
import com.mjuAppSW.joA.domain.vote.VoteRepository;
import com.mjuAppSW.joA.domain.member.dto.response.VotePageResponse;
import com.mjuAppSW.joA.domain.member.dto.response.LocationPageResponse;
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

    private final MemberService memberService;
    private final ImageUploader imageUploader;
    private final HeartRepository heartRepository;
    private final VoteRepository voteRepository;
    private final RoomRepository roomRepository;
    private final RoomInMemberRepository roomInMemberRepository;

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

    public ChattingPageResponse getUserInfo(Long roomId, Long memberId){
        Room room = findByRoomId(roomId);
        Member member = memberService.getBySessionId(memberId);
        RoomInMember roomInMember = findByRoomAndMember(room, member);

        UserInfoVO userInfoVO = findOpponentUserInfoByRoomAndMember(roomInMember.getRoom(), roomInMember.getMember());
        return ChattingPageResponse.of(userInfoVO.getName(), userInfoVO.getUrlCode(), userInfoVO.getBio());
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
        if (isNotBasicPicture(member.getUrlCode())){
            imageUploader.delete(member.getUrlCode());
        }
        String newUrlCode = imageUploader.put(member.getId(), request.getBase64Picture());
        member.updateUrlCode(newUrlCode);
    }

    @Transactional
    public void deletePicture(Long sessionId) {
        Member member = memberService.getNormalBySessionId(sessionId);
        if(isNotBasicPicture(member.getUrlCode())) {
            imageUploader.delete(member.getUrlCode());
            member.deleteUrlCode();
        }
    }

    private Room findByRoomId(Long roomId){
        return roomRepository.findById(roomId)
            .orElseThrow(RoomNotFoundException::new);
    }

    private RoomInMember findByRoomAndMember(Room room, Member member){
        return roomInMemberRepository.findByRoomAndMember(room, member)
            .orElseThrow(RoomInMemberNotFoundException::new);
    }

    private UserInfoVO findOpponentUserInfoByRoomAndMember(Room room, Member member){
        return roomInMemberRepository.findOpponentUserInfoByRoomAndMember(room, member)
            .orElseThrow(MemberNotFoundException::new);
    }

    private boolean isNotBasicPicture(String urlCode) {
        return !urlCode.equals(EMPTY_STRING);
    }
}
