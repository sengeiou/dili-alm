package alm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.dili.AlmApplication;
import com.dili.alm.domain.ProjectOnlineApply;
import com.dili.alm.service.ProjectOnlineApplyService;
import com.dili.bpmc.sdk.rpc.FormRpc;
import com.dili.bpmc.sdk.rpc.TaskRpc;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AlmApplication.class)
public class MyTest {

	@Autowired
	private FormRpc formRpc;
	@Autowired
	private TaskRpc taskRpc;
	@Autowired
	private ProjectOnlineApplyService projectOnlineApplyService;

	@Test
	public void test() {
		ProjectOnlineApply apply = this.projectOnlineApplyService.getBySerialNumber("2002044801");
		System.out.println(apply.getExecutorId());
	}

}
