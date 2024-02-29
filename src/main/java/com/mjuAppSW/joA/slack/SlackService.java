package com.mjuAppSW.joA.slack;

import static com.mjuAppSW.joA.common.constant.Constants.SlackService.*;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.mjuAppSW.joA.slack.vo.HttpServletRequestVO;
import com.slack.api.Slack;
import com.slack.api.methods.SlackApiException;
import com.slack.api.model.Attachment;
import com.slack.api.model.block.LayoutBlock;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SlackService {
	@Value(value = "${slack.token}")
	String token;
	@Value(value = "${slack.channel.monitor}")
	String channelName;

	@Async
	public void sendSlackMessageProductError(HttpServletRequestVO httpServletRequestVO, Exception exception) {
		try {
			List<LayoutBlock> layoutBlocks = SlackServiceUtil.createProdErrorMessage(httpServletRequestVO, exception);
			List<Attachment> attachments = SlackServiceUtil.createAttachments(ERROR_COLOR,
				layoutBlocks);
			Slack.getInstance().methods(token).chatPostMessage(request ->
				request.channel(channelName)
					.attachments(attachments)
					.text(ERROR_MESSAGE_TITLE));
		} catch (SlackApiException | IOException | TaskRejectedException e) {
			log.error(e.getMessage(), e);
		}
	}
}
