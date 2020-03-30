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
package com.nway.spring.jdbc;

import java.util.List;

/**
 * @author zdtjss@163.com
 * @since 2014年3月2日
 */
public class Pagination<T> {

    /**
     * 页面数据 *
     */
    private List<T> pageData;
    /**
     * 页面最大条数 *
     */
    private int pageSize;
    /**
     * 符合查询条件的记录总数 *
     */
    private int total;
    /**
     * 总页数 *
     */
    private int pageCount;
    /**
     * 当前页 *
     */
    private int currentPage;

    public Pagination() {
    }

    public Pagination(int pageSize, int currentPage) {
        this.pageSize = pageSize;
        this.currentPage = currentPage;
    }

    /**
     * @param pageData   页面数据
     * @param totalCount 符合查询条件的记录总数
     * @param page       当前页数
     * @param pageSize   页面最大条数
     */
    public Pagination(List<T> pageData, int totalCount, int page, int pageSize) {

        this.pageData = pageData;
        this.total = totalCount;
        this.pageSize = pageSize;
        this.currentPage = page;

        init();
    }

    private void init() {
        if (total > 0) {
            pageCount = (total / pageSize);
            if (total % pageSize > 0) {
                pageCount++;
            }
        }
    }

    public List<T> getPageData() {
        return pageData;
    }

    public void setPageData(List<T> pageData) {
        this.pageData = pageData;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();

        builder.append("Pagination [totalCount=");
        builder.append(total);
        builder.append(", pageCount=");
        builder.append(pageCount);
        builder.append(", currentPage=");
        builder.append(currentPage);
        builder.append(", pageSize=");
        builder.append(pageSize);
        builder.append(", pageData=");
        builder.append(pageData);
        builder.append("]");

        return builder.toString();
    }

}
