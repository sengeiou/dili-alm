/*package com.dili.alm.handler;

import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.session.SessionContext;

public class assignmentHandler implements  TaskListener {

	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegateTask) {

		
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		
		delegateTask.setAssignee(user.getId()+","+user.getRealName()+"");
		
	}

	
}*/