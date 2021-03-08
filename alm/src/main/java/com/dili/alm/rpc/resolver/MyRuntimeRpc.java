package com.dili.alm.rpc.resolver;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.dili.bpmc.sdk.domain.ProcessInstanceMapping;
import com.dili.bpmc.sdk.dto.StartProcessInstanceDto;
import com.dili.bpmc.sdk.rpc.restful.RuntimeRpc;
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
public class MyRuntimeRpc {

	@Autowired
	RuntimeRpc runtimeRpc;
	
	public BaseOutput<ProcessInstanceMapping> startProcessInstanceByKey(String ProcessDefinitionKey,String businessKey,String userId,Map<String, Object> variables){
        StartProcessInstanceDto startProcessInstanceDto = DTOUtils.newInstance(StartProcessInstanceDto.class);
        startProcessInstanceDto.setProcessDefinitionKey(ProcessDefinitionKey);
        startProcessInstanceDto.setBusinessKey(businessKey);
        startProcessInstanceDto.setUserId(userId);
        variables.put("businessKey", businessKey);
        startProcessInstanceDto.setVariables(variables);
		return runtimeRpc.startProcessInstanceByKey(startProcessInstanceDto);
		
	};
	
}
