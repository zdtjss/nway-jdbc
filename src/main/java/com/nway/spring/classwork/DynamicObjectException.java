/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nway.spring.classwork;

import org.springframework.dao.DataAccessException;

/**
 * 动态类或对象相关异常
 *
 * @author zdtjss@163.com
 *
 * @since 2014-1-13
 */
@SuppressWarnings("serial")
public class DynamicObjectException extends DataAccessException {

	public DynamicObjectException(String message) {
		super(message);
	}

	public DynamicObjectException(String message, Throwable cause) {
		super(message, cause);
	}
}
