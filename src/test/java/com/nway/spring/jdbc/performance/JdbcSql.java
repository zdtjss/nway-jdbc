package com.nway.spring.jdbc.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JdbcSql
{
    private StringBuilder sql = new StringBuilder();
    
    private List<Object> condition = new ArrayList<>();
    
    public StringBuilder getSql()
    {
        return sql;
    }
    
    public StringBuilder appendSql(String sql)
    {
        return this.sql.append(sql);
    }
    
    public List<Object> getCondition()
    {
        return condition;
    }
    
    public List<Object> addCondition(Object condition)
    {
        this.condition.add(condition);
        
        return this.condition;
    }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("JdbcSql [sql=")
                .append(sql)
                .append(", param=")
                .append(Arrays.toString(condition.toArray(new Object[condition.size()])))
                .append("]");
        
        return builder.toString();
    }
    
}
