package com.dili.alm.component;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class NumberGeneratorListener implements ApplicationListener<ApplicationEvent> {

	@Autowired(required = false)
	private List<NumberGenerator> numberGenerators;

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (this.numberGenerators == null) {
			return;
		}
		if (event instanceof ContextRefreshedEvent) {
			this.numberGenerators.forEach(ng -> ng.init());
		}
		if (event instanceof ContextClosedEvent) {
			this.numberGenerators.forEach(ng -> ng.persist());
		}
	}

}
