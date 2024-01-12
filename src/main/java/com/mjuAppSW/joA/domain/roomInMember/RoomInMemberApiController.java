package com.mjuAppSW.joA.domain.roomInMember;

import com.mjuAppSW.joA.domain.roomInMember.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class RoomInMemberApiController {

    private RoomInMemberService roomInMemberService;

    @Autowired
    public RoomInMemberApiController(RoomInMemberService roomInMemberService){
        this.roomInMemberService = roomInMemberService;
    }

    @GetMapping("/load/roomList")
    public ResponseEntity<List<RoomListDTO>> getRoomList(
            @RequestParam("memberId") Long memberId) {
        log.info("getRoomList : memberId = {}", memberId);
        RoomListVO response = roomInMemberService.getRoomList(memberId);
        if(response.getStatus().equals("0") || response.getStatus().equals("1")){
            log.info("getRoomList Return : OK");
            return ResponseEntity.ok(response.getRoomDTOList());
        }else if(response.getStatus().equals("3")){
            log.warn("getRoomList Return : BAD_REQUEST, member id is not valid / memberId = {}", memberId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        else{
            log.warn("getRoomList Return : BAD_REQUEST, roomId's not correct / memberId = {}", memberId);
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/save/voteResult") // 투표 저장하기
    public ResponseEntity<VoteDTO> saveVoteResult(@RequestBody VoteRequest request){
        log.info("saveVoteResult : roomId = {}, memberId = {}, result = {}", request.getRoomId(), request.getMemberId(), request.getResult());
        Boolean checkRoomIdAndMemberId = roomInMemberService.checkRoomIdAndMemberId(request.getRoomId(), request.getMemberId());
        if(!checkRoomIdAndMemberId){
            log.warn("saveVoteResult Return : BAD_REQUEST, getValue's not correct / roomId = {}, memberId = {}", request.getRoomId(), request.getMemberId());
            return ResponseEntity.badRequest().build();
        }
        VoteDTO voteDTO = roomInMemberService.saveVoteResult(request.getRoomId(), request.getMemberId(), request.getResult());
        if(voteDTO != null){
            log.info("saveVoteResult Return : roomId = {}, memberId = {}, result = {}", voteDTO.getRoomId(), voteDTO.getMemberId(), voteDTO.getResult());
            return ResponseEntity.ok(voteDTO);
        }
        log.warn("saveVoteResult Return : NOT_FOUND, fail to save / roomId = {}, memberId = {}, result = {}", request.getRoomId(), request.getMemberId(), request.getResult());
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/check/voteResult")
    public ResponseEntity<List<CheckVoteDTO>> checkVoteResult(
            @RequestParam("roomId") Long roomId){
        log.info("checkVoteResult : roomId = {}", roomId);
        Boolean checkRoomIdAndMemberId = roomInMemberService.checkRoomId(roomId);
        if(!checkRoomIdAndMemberId){
            log.warn("checkVoteResult Return : BAD_REQUEST, roomId's not found / roomId = {}", roomId);
            return ResponseEntity.badRequest().build();
        }
        List<CheckVoteDTO> list = roomInMemberService.checkVoteResult(roomId);
        if(list.isEmpty() || list == null){
            log.warn("checkVoteResult Return : BAD_REQUEST, In roomInMember's not found / roomId = {}", roomId);
            return ResponseEntity.badRequest().build();
        }
        for(CheckVoteDTO checkVoteDTO : list){
            log.info("checkVoteResult Return : OK, roomId = {}, memberId = {}, result = {}", checkVoteDTO.getRoomId(), checkVoteDTO.getMemberId(), checkVoteDTO.getResult());
        }
        return ResponseEntity.ok(list);
    }

    @PostMapping("/update/expired")
    public HttpStatus updateExpired(@RequestBody updateExpiredRequest request){
        log.info("updateExpired : roomId = {}, memberId = {}, expired = {}", request.getRoomId(), request.getMemberId(), request.getExpired());
        Boolean save = roomInMemberService.updateExpired(request.getRoomId(), request.getMemberId(), request.getExpired());
        if(!save){
            log.warn("updateExpired Return : BAD_REQUEST, getValue's not found / roomId = {}, memberId = {}, expired = {}", request.getRoomId(), request.getMemberId(), request.getExpired());
            return HttpStatus.BAD_REQUEST;
        }
        log.info("updateExpired Return : OK");
        return HttpStatus.OK;
    }

    @PostMapping("/check/roomInMember")
    public ResponseEntity<String> checkRoomInMember(@RequestBody CheckRoomInMemberRequest request){
        log.info("checkRoomInMember : memberId1 = {}, memberId2 = {}", request.getMemberId1(), request.getMemberId2());
        Boolean check = roomInMemberService.checkRoomInMember(request.getMemberId1(), request.getMemberId2());
        if(check){
            log.info("checkRoomInMember Return : OK");
            return new ResponseEntity(HttpStatus.OK);
        }else{
            log.warn("checkRoomInMember Return : BAD_REQUEST, getValue's not found / memberId1 = {}, memberId2 = {}", request.getMemberId1(), request.getMemberId2());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/load/userInfo")
    public ResponseEntity<UserInfoDTO> getUserInfo(@RequestParam("roomId") Long roomId,
                                                   @RequestParam("memberId") Long memberId){
        log.info("getUserInfo : roomId = {}, memberId = {}", roomId, memberId);
        UserInfoDTO userInfoDTO = roomInMemberService.getUserInfo(roomId, memberId);
        if(userInfoDTO.getName() == null){
            log.warn("getUserInfo Return : BAD_REQUEST, name is null / roomId = {}, memberId = {}", roomId, memberId);
            return ResponseEntity.badRequest().body(userInfoDTO);
        }
        log.info("userInfoDTO : name = {}, urlCode = {}, bio = {}", userInfoDTO.getName(),
                userInfoDTO.getUrlCode(), userInfoDTO.getBio());
        return ResponseEntity.ok(userInfoDTO);
    }
}