/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nway.spring.jdbc.bean;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.ResultSetExtractor;

/**
 *
 * @author zdtjss@163.com
 * @param <T> List元素类型
 *
 * @since 2014-03-28
 */
public final class BeanListHandler<T> implements ResultSetExtractor<List<T>> {

    private final Class<T> type;
    
    /**
     * 考虑本类可能在不同 ClassLoader中使用，这里不应该是静态的 *
     */
    private final BeanProcessor beanProcessor;

    public BeanListHandler(Class<T> type, BeanProcessor beanProcessor) {
        this.type = type;
        this.beanProcessor = beanProcessor;
    }

	@Override
	public List<T> extractData(ResultSet rs) throws SQLException {

		return beanProcessor.toBeanList(rs, type);
	}

}
