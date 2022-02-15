package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.ExampleEntity;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SqlBuilderUtilsTest {

    @Test
    public void getColumnTest() {

        String column = SqlBuilderUtils.getColumn(ExampleEntity::getId);
        Assertions.assertEquals(column, "pk_id");
    }

    @Test
    public void getEntityInfoTest() {
        EntityInfo entityInfo = SqlBuilderUtils.getEntityInfo(ExampleEntity::getId);
        Assertions.assertEquals(entityInfo.getTableName(), "t_nway");
    }
}
