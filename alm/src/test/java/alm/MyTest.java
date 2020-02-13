package alm;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dili.AlmApplication;
import com.dili.alm.domain.OnlineDataChange;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.domain.dto.OnlineDataChangeBpmcDtoDto;
import com.dili.alm.service.OnlineDataChangeService;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.bpmc.sdk.rpc.FormRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;
import com.dili.ss.util.BeanConver;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AlmApplication.class)
public class MyTest {

	@Autowired
	private FormRpc formRpc;
	@Autowired
	private TaskRpc taskRpc;
	@Autowired
	private ProjectOnlineApplyService projectOnlineApplyService;
	@Autowired
	private OnlineDataChangeService onlineDataChangeService;

	@Test
	public void test() {
		List<OnlineDataChange> list = this.onlineDataChangeService.list(new OnlineDataChange());
		List<OnlineDataChangeBpmcDtoDto> targetList = BeanConver.copyList(list, OnlineDataChangeBpmcDtoDto.class);
		System.out.println(targetList);
	}

}
