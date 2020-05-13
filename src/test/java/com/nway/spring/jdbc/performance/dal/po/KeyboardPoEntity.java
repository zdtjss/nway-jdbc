package com.nway.spring.jdbc.performance.dal.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
                *
        * This class was generated by MyBatis Generator.
        * This class corresponds to the database table t_keyboard
        *
        * @mbg.generated do_not_delete_during_merge
        * @project https://github.com/itfsw/mybatis-generator-plugin
        */
public class KeyboardPoEntity implements Serializable {
    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.id
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private Integer id;

    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.brand
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private String brand;

    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.color
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private String color;

    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.interface_type
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private Integer interfaceType;

    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.is_wireless
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private Boolean isWireless;

    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.model
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private String model;

    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.price
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private Float price;

    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.production_date
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private Date productionDate;

    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.type
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private Integer type;

    /**
                *
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database column t_keyboard.photo
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private byte[] photo;

    /**
                * This field was generated by MyBatis Generator.
                * This field corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    private static final long serialVersionUID = 1L;

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.id
                *
                * @return the value of t_keyboard.id
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public Integer getId() {
        return id;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.id
                *
                * @param id the value for t_keyboard.id
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.brand
                *
                * @return the value of t_keyboard.brand
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public String getBrand() {
        return brand;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.brand
                *
                * @param brand the value for t_keyboard.brand
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.color
                *
                * @return the value of t_keyboard.color
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public String getColor() {
        return color;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.color
                *
                * @param color the value for t_keyboard.color
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setColor(String color) {
        this.color = color;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.interface_type
                *
                * @return the value of t_keyboard.interface_type
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public Integer getInterfaceType() {
        return interfaceType;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.interface_type
                *
                * @param interfaceType the value for t_keyboard.interface_type
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setInterfaceType(Integer interfaceType) {
        this.interfaceType = interfaceType;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.is_wireless
                *
                * @return the value of t_keyboard.is_wireless
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public Boolean getIsWireless() {
        return isWireless;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.is_wireless
                *
                * @param isWireless the value for t_keyboard.is_wireless
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setIsWireless(Boolean isWireless) {
        this.isWireless = isWireless;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.model
                *
                * @return the value of t_keyboard.model
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public String getModel() {
        return model;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.model
                *
                * @param model the value for t_keyboard.model
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setModel(String model) {
        this.model = model;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.price
                *
                * @return the value of t_keyboard.price
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public Float getPrice() {
        return price;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.price
                *
                * @param price the value for t_keyboard.price
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setPrice(Float price) {
        this.price = price;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.production_date
                *
                * @return the value of t_keyboard.production_date
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public Date getProductionDate() {
        return productionDate;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.production_date
                *
                * @param productionDate the value for t_keyboard.production_date
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.type
                *
                * @return the value of t_keyboard.type
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public Integer getType() {
        return type;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.type
                *
                * @param type the value for t_keyboard.type
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method returns the value of the database column t_keyboard.photo
                *
                * @return the value of t_keyboard.photo
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public byte[] getPhoto() {
        return photo;
    }

    /**
                * This method was generated by MyBatis Generator.
                * This method sets the value of the database column t_keyboard.photo
                *
                * @param photo the value for t_keyboard.photo
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    /**
            * This enum was generated by MyBatis Generator.
            * This enum corresponds to the database table t_keyboard
            *
            * @mbg.generated
            * @project https://github.com/itfsw/mybatis-generator-plugin
            */
    public enum Column {
        id("id", "id", "INTEGER", false),
        brand("brand", "brand", "VARCHAR", false),
        color("color", "color", "VARCHAR", false),
        interfaceType("interface_type", "interfaceType", "INTEGER", false),
        isWireless("is_wireless", "isWireless", "BIT", false),
        model("model", "model", "VARCHAR", false),
        price("price", "price", "REAL", false),
        productionDate("production_date", "productionDate", "DATE", false),
        type("type", "type", "INTEGER", false),
        photo("photo", "photo", "LONGVARBINARY", false);

        /**
                    * This field was generated by MyBatis Generator.
                    * This field corresponds to the database table t_keyboard
                    *
                    * @mbg.generated
                    * @project https://github.com/itfsw/mybatis-generator-plugin
                    */
        private static final String BEGINNING_DELIMITER = "\"";

        /**
                    * This field was generated by MyBatis Generator.
                    * This field corresponds to the database table t_keyboard
                    *
                    * @mbg.generated
                    * @project https://github.com/itfsw/mybatis-generator-plugin
                    */
        private static final String ENDING_DELIMITER = "\"";

        /**
                    * This field was generated by MyBatis Generator.
                    * This field corresponds to the database table t_keyboard
                    *
                    * @mbg.generated
                    * @project https://github.com/itfsw/mybatis-generator-plugin
                    */
        private final String column;

        /**
                    * This field was generated by MyBatis Generator.
                    * This field corresponds to the database table t_keyboard
                    *
                    * @mbg.generated
                    * @project https://github.com/itfsw/mybatis-generator-plugin
                    */
        private final boolean isColumnNameDelimited;

        /**
                    * This field was generated by MyBatis Generator.
                    * This field corresponds to the database table t_keyboard
                    *
                    * @mbg.generated
                    * @project https://github.com/itfsw/mybatis-generator-plugin
                    */
        private final String javaProperty;

        /**
                    * This field was generated by MyBatis Generator.
                    * This field corresponds to the database table t_keyboard
                    *
                    * @mbg.generated
                    * @project https://github.com/itfsw/mybatis-generator-plugin
                    */
        private final String jdbcType;

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public String value() {
            return this.column;
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public String getValue() {
            return this.column;
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public String getJavaProperty() {
            return this.javaProperty;
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public String getJdbcType() {
            return this.jdbcType;
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public static Column[] all() {
            return Column.values();
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }

        /**
                * This method was generated by MyBatis Generator.
                * This method corresponds to the database table t_keyboard
                *
                * @mbg.generated
                * @project https://github.com/itfsw/mybatis-generator-plugin
                */
        public String getAliasedEscapedColumnName() {
            return this.getEscapedColumnName();
        }
    }
}