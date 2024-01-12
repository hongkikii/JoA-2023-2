package com.mjuAppSW.joA.domain.report.message;

import com.mjuAppSW.joA.domain.report.message.dto.CheckMessageReportRequest;
import com.mjuAppSW.joA.domain.report.message.dto.ReportRequest;
import com.mjuAppSW.joA.domain.report.message.dto.StatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MessageReportApiController {

    private MessageReportService messageReportService;

    @Autowired
    public MessageReportApiController(MessageReportService messageReportService){
        this.messageReportService = messageReportService;
    }

    @PostMapping("/report/message")
    public ResponseEntity<String> messageReport(@RequestBody ReportRequest request){
        log.info("messageReport : messageId = {}, categoryId = {}, content = {}", request.getMessageId(), request.getCategoryId(), request.getContent());
        String save = messageReportService.messageReport(request.getMessageId(), request.getCategoryId(), request.getContent());
        if(save.equals("0")){
            log.info("messageReport Return : OK, success to report");
            return ResponseEntity.ok().build();
        }else if(save.equals("1")){
            log.warn("messageReport Return : BAD_REQUEST, getValue's not correct / messageId = {}, categoryId = {}", request.getMessageId(), request.getCategoryId());
            return ResponseEntity.badRequest().build();
        }else{
            log.info("messageReport Return : BAD_REQUEST, existed MessageReport / messageId = {}", request.getMessageId());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/report/admin/delete")
    public void deleteMessageReportAdmin(@RequestParam("id") Long id){
        log.info("deleteMessageReportAdmin : id = {}", id);
        messageReportService.deleteMessageReportAdmin(id);
    }

    @PostMapping("/check/messageReport")
    public ResponseEntity<StatusResponse> checkMessageReport(@RequestBody CheckMessageReportRequest request){
        log.info("checkMessageReport : memberId1 = {}, memberId2 = {}", request.getMemberId1(), request.getMemberId2());
        StatusResponse check = messageReportService.checkMessageReport(request.getMemberId1(), request.getMemberId2());
        if(check.getStatus() == 0){
            log.warn("checkMessageReport Return : BAD_REQUEST, memberId1 = {}, memberId2 = {}", request.getMemberId1(), request.getMemberId2());
            return ResponseEntity.ok(check);
        }
        else if(check.getStatus() == 1){
            log.info("checkMessageReport Return : BAD_REQUEST Reported, memberId1 = {}, memberId2 = {}", request.getMemberId1(), request.getMemberId2());
            return ResponseEntity.ok(check);
        }else if(check.getStatus() == 2){
            log.info("checkMessageReport Return : BAD_REQUEST Report, memberId1 = {}, memberId2 = {}", request.getMemberId1(), request.getMemberId2());
            return ResponseEntity.ok(check);
        }
        log.info("checkMessageReport Return : OK, memberId1 = {}, memberId2 = {}", request.getMemberId1(), request.getMemberId2());
        return ResponseEntity.ok(check);
    }
}
