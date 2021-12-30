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
package com.nway.spring.jdbc.pagination;

public final class OraclePaginationSupport implements PaginationSupport {

	@Override
	public PageDialect buildPaginationSql(String sql, int page, int pageSize) {

		if (page <= 0 || pageSize <= 0) {

			throw new IllegalArgumentException("页数或页面数据量应该大于零");
		}

		int offset = (page - 1) * pageSize;

		String pageSql = "SELECT * FROM ( SELECT TMP.*, ROWNUM RN FROM ( " + sql + " ) TMP WHERE ROWNUM <= ? ) WHERE RN > ?";

		return new PageDialect(pageSql, offset, pageSize);
	}
	
}
