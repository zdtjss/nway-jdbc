package com.nway.spring.jdbc.bean.processor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public interface BeanProcessor {

	<T> T toBean(ResultSet rs, Class<T> type) throws SQLException;

	<T> List<T> toBeanList(ResultSet rs, Class<T> type) throws SQLException;
}
