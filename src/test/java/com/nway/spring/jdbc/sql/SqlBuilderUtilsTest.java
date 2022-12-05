package com.nway.spring.jdbc.sql;

import com.nway.spring.jdbc.ExampleEntity;
import com.nway.spring.jdbc.sql.meta.EntityInfo;
import com.nway.spring.jdbc.sql.meta.MultiValueColumnInfo;
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

    @Test
    public void multiTest() {

        MultiValueColumnInfo mv2 = SqlBuilderUtils.getEntityInfo(ExampleEntity.class)
                .getMultiValue()
                .stream().filter(entity -> entity.getColumnName().equals("mv2")).findFirst().get();

        Assertions.assertEquals(mv2.getKey(), "pk_id_mv2");

        MultiValueColumnInfo mv3 = SqlBuilderUtils.getEntityInfo(ExampleEntity.class)
                .getMultiValue()
                .stream().filter(entity -> entity.getColumnName().equals("mv3")).findFirst().get();

        Assertions.assertEquals(mv3.getTable(), "t_nway_mvc");
        Assertions.assertEquals(mv3.getKey(), "pk_id");
        Assertions.assertEquals(mv3.getFk(), "foreign_key");
        Assertions.assertEquals(mv3.getIdx(), "seq");
    }
}
