package com.mjuAppSW.joA.slack;

import static com.mjuAppSW.joA.common.constant.Constants.SlackServiceUtil.*;
import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.mjuAppSW.joA.slack.vo.HttpServletRequestVO;
import com.slack.api.model.Attachment;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.TextObject;

public class SlackServiceUtil {

	public static List<Attachment> createAttachments(String color, List<LayoutBlock> data) {
		List<Attachment> attachments = new ArrayList<>();
		Attachment attachment = new Attachment();
		attachment.setColor(color);
		attachment.setBlocks(data);
		attachments.add(attachment);
		return attachments;
	}

	public static List<LayoutBlock> createProdErrorMessage(HttpServletRequestVO request, Exception exception) {
		StackTraceElement[] stacks = exception.getStackTrace();

		List<LayoutBlock> layoutBlockList = new ArrayList<>();

		List<TextObject> sectionInFields = new ArrayList<>();
		sectionInFields.add(markdownText(ERROR_MESSAGE + exception.getMessage()));
		sectionInFields.add(markdownText(ERROR_STACK + exception));
		sectionInFields.add(markdownText(ERROR_URL + request.getRequestURL()));
		sectionInFields.add(markdownText(ERROR_METHOD + request.getMethod()));
		sectionInFields.add(markdownText(ERROR_DATE + formatDate(LocalDateTime.now())));
		layoutBlockList.add(section(section -> section.fields(sectionInFields)));

		layoutBlockList.add(divider());
		layoutBlockList.add(section(section -> section.text(markdownText(filterErrorStack(stacks)))));
		return layoutBlockList;
	}

	private static String filterErrorStack(StackTraceElement[] stacks) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(POINTER);
		for (StackTraceElement stack : stacks) {
			if (stack.toString().contains(FILTER_STRING)) {
				stringBuilder.append(stack).append(NEW_LINE);
			}
		}
		stringBuilder.append(POINTER);
		return stringBuilder.toString();
	}

	private static String formatDate(LocalDateTime now){
		DateTimeFormatter dateForm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return now.format(dateForm);
	}
}
