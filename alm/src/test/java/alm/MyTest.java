package alm;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.dili.AlmApplication;
import com.dili.bpmc.sdk.domain.TaskMapping;
import com.dili.bpmc.sdk.dto.TaskDto;
import com.dili.bpmc.sdk.rpc.FormRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AlmApplication.class)
public class MyTest {

	@Autowired
	private FormRpc formRpc;
	@Autowired
	private TaskRpc taskRpc;

	@Test
	public void test() {
		TaskDto taskDto = DTOUtils.newDTO(TaskDto.class);
		taskDto.setProcessInstanceIds(Arrays.asList("202001161650246520000000"));
		BaseOutput<List<TaskMapping>> output = this.taskRpc.listTaskMapping(taskDto);
		System.out.println(JSON.toJSONString(output));
	}

}
