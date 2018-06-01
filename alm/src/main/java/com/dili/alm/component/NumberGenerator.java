package com.dili.alm.component;

public interface NumberGenerator {

	/**
	 * 获取自增序列
	 * 
	 * @return 序列 xxxx
	 */
	String get();

	/**
	 * 当容器关闭时同步到数据库
	 */
	void persist();

	/**
	 * 从数据库里初始化数据
	 */
	void init();

}