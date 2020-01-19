package com.dili.sysadmin.sdk.component.beetl;

import java.io.IOException;
import java.util.Map;

import org.beetl.core.tag.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dili.sysadmin.sdk.domain.UserTicket;
import com.dili.sysadmin.sdk.redis.UserResourceRedis;
import com.dili.sysadmin.sdk.session.SessionContext;

/**
 * 用户资源权限检查标签
 * Created by asiamaster on 2017/7/21 0021.
 */
@Component("resource")
public class resourceTag extends Tag {

	@Autowired
	UserResourceRedis userResourceRedis;
	//标签自定义属性
	private final String CODE_FIELD = "code";

	@Override
	public void render() {
		Map<String, Object> argsMap = (Map)this.args[1];
		String code = (String) argsMap.get(CODE_FIELD);
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if(userTicket == null) {
			try {
				ctx.byteWriter.writeString("用户未登录");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		if(userResourceRedis.checkUserResourceRight(userTicket.getId(), code)){
			try {
				ctx.byteWriter.write(getBodyContent());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
