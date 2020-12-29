package com.nway.spring.jdbc.performance.dal.mapper;

import com.nway.spring.jdbc.performance.dal.po.KeyboardPoEntity;
import com.nway.spring.jdbc.performance.dal.po.KeyboardPoQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository("KeyboardMapperGe")
public interface KeyboardMapper {
    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    long countByExample(KeyboardPoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int deleteByExample(KeyboardPoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int deleteByPrimaryKey(Integer id);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int insert(KeyboardPoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int insertSelective(KeyboardPoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    List<KeyboardPoEntity> selectByExampleWithBLOBs(KeyboardPoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    List<KeyboardPoEntity> selectByExample(KeyboardPoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    KeyboardPoEntity selectByPrimaryKey(Integer id);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByExampleSelective(@Param("record") KeyboardPoEntity record, @Param("example") KeyboardPoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByExampleWithBLOBs(@Param("record") KeyboardPoEntity record, @Param("example") KeyboardPoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByExample(@Param("record") KeyboardPoEntity record, @Param("example") KeyboardPoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByPrimaryKeySelective(KeyboardPoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByPrimaryKeyWithBLOBs(KeyboardPoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByPrimaryKey(KeyboardPoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int batchInsert(@Param("list") List<KeyboardPoEntity> list);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int batchInsertSelective(@Param("list") List<KeyboardPoEntity> list, @Param("selective") KeyboardPoEntity.Column ... selective);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int upsert(KeyboardPoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int upsertSelective(KeyboardPoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int upsertWithBLOBs(KeyboardPoEntity record);
}