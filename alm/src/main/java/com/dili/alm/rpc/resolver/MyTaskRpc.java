package com.dili.alm.rpc.resolver;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.bpmc.sdk.dto.TaskCompleteDto;
import com.dili.bpmc.sdk.rpc.restful.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.retrofitful.annotation.VOBody;

/**
 * <B>Description</B>
 * 本软件源代码版权归农丰时代及其团队所有,未经许可不得任意复制与传播
 * <B>农丰时代科技有限公司</B>
 *
 * @Description 接口参数转换
 * @author yangfan
 * @date 2021年3月4日
 */
@Component
public class MyTaskRpc {

	@Autowired
	private TaskRpc taskRpc;
	
	public BaseOutput<String> complete(String taskId, Map<String,Object> variables){
		TaskCompleteDto dto = DTOUtils.newInstance(TaskCompleteDto.class);
		dto.setTaskId(taskId);
		dto.setVariables(variables);
		return taskRpc.complete(dto);
	};
	
}
