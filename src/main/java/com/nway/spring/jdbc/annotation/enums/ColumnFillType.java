package com.nway.spring.jdbc.annotation.enums;

public enum ColumnFillType {

	NONE,
	/**
	 * 全局唯一ID (idWorker)
	 */
	ID_WORKER,
	/**
	 * 全局唯一ID (UUID)
	 */
	UUID,
	/**
	 * 字符串全局唯一ID (idWorker 的字符串表示)
	 */
	ID_WORKER_STR,
	/**
	 * 本地时间（毫秒）
	 */
	LOCAL_DATE_TIME,
	/**
	 * 详见 {@link ColumnFillStrategy}
	 */
	CUSTOM;

}
