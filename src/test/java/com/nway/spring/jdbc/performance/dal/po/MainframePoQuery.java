package com.nway.spring.jdbc.performance.dal.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class MainframePoQuery {
    /**
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database table t_mainframe
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    protected String orderByClause;

    /**
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database table t_mainframe
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    protected boolean distinct;

    /**
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database table t_mainframe
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    protected List<Criteria> oredCriteria;

    /**
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database table t_mainframe
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    protected Integer offset;

    /**
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database table t_mainframe
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    protected Integer rows;

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public MainframePoQuery() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public boolean isDistinct() {
        return distinct;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
        rows = null;
        offset = null;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public Integer getOffset() {
        return this.offset;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public Integer getRows() {
        return this.rows;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public MainframePoQuery limit(Integer rows) {
        this.rows = rows;
        return this;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public MainframePoQuery limit(Integer offset, Integer rows) {
        this.offset = offset;
        this.rows = rows;
        return this;
    }

    /**
            * This method was generated by MyBatis Generator.
            * This method corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public MainframePoQuery page(Integer page, Integer pageSize) {
        this.offset = page * pageSize;
        this.rows = pageSize;
        return this;
    }

    /**
            * This class was generated by MyBatis Generator.
            * This class corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Integer value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Integer value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Integer value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Integer value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Integer value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Integer> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Integer> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Integer value1, Integer value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Integer value1, Integer value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andBrandIsNull() {
            addCriterion("brand is null");
            return (Criteria) this;
        }

        public Criteria andBrandIsNotNull() {
            addCriterion("brand is not null");
            return (Criteria) this;
        }

        public Criteria andBrandEqualTo(String value) {
            addCriterion("brand =", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandNotEqualTo(String value) {
            addCriterion("brand <>", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandGreaterThan(String value) {
            addCriterion("brand >", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandGreaterThanOrEqualTo(String value) {
            addCriterion("brand >=", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandLessThan(String value) {
            addCriterion("brand <", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandLessThanOrEqualTo(String value) {
            addCriterion("brand <=", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandLike(String value) {
            addCriterion("brand like", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandNotLike(String value) {
            addCriterion("brand not like", value, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandIn(List<String> values) {
            addCriterion("brand in", values, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandNotIn(List<String> values) {
            addCriterion("brand not in", values, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandBetween(String value1, String value2) {
            addCriterion("brand between", value1, value2, "brand");
            return (Criteria) this;
        }

        public Criteria andBrandNotBetween(String value1, String value2) {
            addCriterion("brand not between", value1, value2, "brand");
            return (Criteria) this;
        }

        public Criteria andModelIsNull() {
            addCriterion("model is null");
            return (Criteria) this;
        }

        public Criteria andModelIsNotNull() {
            addCriterion("model is not null");
            return (Criteria) this;
        }

        public Criteria andModelEqualTo(String value) {
            addCriterion("model =", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelNotEqualTo(String value) {
            addCriterion("model <>", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelGreaterThan(String value) {
            addCriterion("model >", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelGreaterThanOrEqualTo(String value) {
            addCriterion("model >=", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelLessThan(String value) {
            addCriterion("model <", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelLessThanOrEqualTo(String value) {
            addCriterion("model <=", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelLike(String value) {
            addCriterion("model like", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelNotLike(String value) {
            addCriterion("model not like", value, "model");
            return (Criteria) this;
        }

        public Criteria andModelIn(List<String> values) {
            addCriterion("model in", values, "model");
            return (Criteria) this;
        }

        public Criteria andModelNotIn(List<String> values) {
            addCriterion("model not in", values, "model");
            return (Criteria) this;
        }

        public Criteria andModelBetween(String value1, String value2) {
            addCriterion("model between", value1, value2, "model");
            return (Criteria) this;
        }

        public Criteria andModelNotBetween(String value1, String value2) {
            addCriterion("model not between", value1, value2, "model");
            return (Criteria) this;
        }

        public Criteria andPriceIsNull() {
            addCriterion("price is null");
            return (Criteria) this;
        }

        public Criteria andPriceIsNotNull() {
            addCriterion("price is not null");
            return (Criteria) this;
        }

        public Criteria andPriceEqualTo(Float value) {
            addCriterion("price =", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceNotEqualTo(Float value) {
            addCriterion("price <>", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceGreaterThan(Float value) {
            addCriterion("price >", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceGreaterThanOrEqualTo(Float value) {
            addCriterion("price >=", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceLessThan(Float value) {
            addCriterion("price <", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceLessThanOrEqualTo(Float value) {
            addCriterion("price <=", value, "price");
            return (Criteria) this;
        }

        public Criteria andPriceIn(List<Float> values) {
            addCriterion("price in", values, "price");
            return (Criteria) this;
        }

        public Criteria andPriceNotIn(List<Float> values) {
            addCriterion("price not in", values, "price");
            return (Criteria) this;
        }

        public Criteria andPriceBetween(Float value1, Float value2) {
            addCriterion("price between", value1, value2, "price");
            return (Criteria) this;
        }

        public Criteria andPriceNotBetween(Float value1, Float value2) {
            addCriterion("price not between", value1, value2, "price");
            return (Criteria) this;
        }

        public Criteria andProductionDateIsNull() {
            addCriterion("production_date is null");
            return (Criteria) this;
        }

        public Criteria andProductionDateIsNotNull() {
            addCriterion("production_date is not null");
            return (Criteria) this;
        }

        public Criteria andProductionDateEqualTo(Date value) {
            addCriterionForJDBCDate("production_date =", value, "productionDate");
            return (Criteria) this;
        }

        public Criteria andProductionDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("production_date <>", value, "productionDate");
            return (Criteria) this;
        }

        public Criteria andProductionDateGreaterThan(Date value) {
            addCriterionForJDBCDate("production_date >", value, "productionDate");
            return (Criteria) this;
        }

        public Criteria andProductionDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("production_date >=", value, "productionDate");
            return (Criteria) this;
        }

        public Criteria andProductionDateLessThan(Date value) {
            addCriterionForJDBCDate("production_date <", value, "productionDate");
            return (Criteria) this;
        }

        public Criteria andProductionDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("production_date <=", value, "productionDate");
            return (Criteria) this;
        }

        public Criteria andProductionDateIn(List<Date> values) {
            addCriterionForJDBCDate("production_date in", values, "productionDate");
            return (Criteria) this;
        }

        public Criteria andProductionDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("production_date not in", values, "productionDate");
            return (Criteria) this;
        }

        public Criteria andProductionDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("production_date between", value1, value2, "productionDate");
            return (Criteria) this;
        }

        public Criteria andProductionDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("production_date not between", value1, value2, "productionDate");
            return (Criteria) this;
        }

        public Criteria andTypeIsNull() {
            addCriterion("type is null");
            return (Criteria) this;
        }

        public Criteria andTypeIsNotNull() {
            addCriterion("type is not null");
            return (Criteria) this;
        }

        public Criteria andTypeEqualTo(Integer value) {
            addCriterion("type =", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotEqualTo(Integer value) {
            addCriterion("type <>", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThan(Integer value) {
            addCriterion("type >", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("type >=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThan(Integer value) {
            addCriterion("type <", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeLessThanOrEqualTo(Integer value) {
            addCriterion("type <=", value, "type");
            return (Criteria) this;
        }

        public Criteria andTypeIn(List<Integer> values) {
            addCriterion("type in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotIn(List<Integer> values) {
            addCriterion("type not in", values, "type");
            return (Criteria) this;
        }

        public Criteria andTypeBetween(Integer value1, Integer value2) {
            addCriterion("type between", value1, value2, "type");
            return (Criteria) this;
        }

        public Criteria andTypeNotBetween(Integer value1, Integer value2) {
            addCriterion("type not between", value1, value2, "type");
            return (Criteria) this;
        }
    }

    /**
            * This class was generated by MyBatis Generator.
            * This class corresponds to the database table t_mainframe
            *
            * @mbg.generated do_not_delete_during_merge
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
            * This class was generated by MyBatis Generator.
            * This class corresponds to the database table t_mainframe
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}