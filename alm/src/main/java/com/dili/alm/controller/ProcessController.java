package com.dili.alm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.bpmc.sdk.domain.ActForm;
import com.dili.bpmc.sdk.rpc.FormRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.uap.sdk.session.SessionContext;

@RequestMapping("/process")
@Controller
public class ProcessController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProcessController.class);

	@Autowired
	private FormRpc formRpc;
	@Autowired
	private TaskRpc taskRpc;

	@ResponseBody
	@RequestMapping("/getTaskUrl")
	public BaseOutput<ActForm> getTaskUrl(String taskId, String formKey, @RequestParam(defaultValue = "false") Boolean isNeedClaim) {
		if (isNeedClaim) {
			String userId = SessionContext.getSessionContext().getUserTicket().getId().toString();
			BaseOutput<String> output = this.taskRpc.claim(taskId, userId);
			if (!output.isSuccess()) {
				LOGGER.error(output.getMessage());
				return BaseOutput.failure("签收任务失败");
			}
		}
		BaseOutput<ActForm> fo = this.formRpc.getByKey(formKey);
		if (!fo.isSuccess()) {
			LOGGER.error(fo.getMessage());
			return BaseOutput.failure("获取表单信息失败");
		}
		return fo;
	}
}
