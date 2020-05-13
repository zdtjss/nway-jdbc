package com.nway.spring.jdbc.performance.dal.mapper;

import com.nway.spring.jdbc.performance.dal.po.SoftwarePoEntity;
import com.nway.spring.jdbc.performance.dal.po.SoftwarePoQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository("SoftwareMapperGe")
public interface SoftwareMapper {
    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    long countByExample(SoftwarePoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int deleteByExample(SoftwarePoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int deleteByPrimaryKey(Integer id);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int insert(SoftwarePoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int insertSelective(SoftwarePoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    List<SoftwarePoEntity> selectByExample(SoftwarePoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    SoftwarePoEntity selectByPrimaryKey(Integer id);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByExampleSelective(@Param("record") SoftwarePoEntity record, @Param("example") SoftwarePoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByExample(@Param("record") SoftwarePoEntity record, @Param("example") SoftwarePoQuery example);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByPrimaryKeySelective(SoftwarePoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int updateByPrimaryKey(SoftwarePoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int batchInsert(@Param("list") List<SoftwarePoEntity> list);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int batchInsertSelective(@Param("list") List<SoftwarePoEntity> list, @Param("selective") SoftwarePoEntity.Column ... selective);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int upsert(SoftwarePoEntity record);

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_software
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    int upsertSelective(SoftwarePoEntity record);
}