package com.nway.spring.jdbc.bean.processor;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class RowMapper<T> {

    public abstract T mapRow(ResultSet rs) throws SQLException;

    protected Boolean getBoolean(ResultSet rs, int columnIndex) throws SQLException {
        boolean val = rs.getBoolean(columnIndex);
        return rs.wasNull() ? null : val;
    }

    protected boolean getPrimitiveBoolean(ResultSet rs, int columnIndex) throws SQLException {
        boolean val = rs.getBoolean(columnIndex);
        if (rs.wasNull()) {
            throw new NullPointerException("字段为null，不能转为boolean");
        }
        return val;
    }

    protected Byte getByte(ResultSet rs, int columnIndex) throws SQLException {
        byte val = rs.getByte(columnIndex);
        return rs.wasNull() ? null : val;
    }

    protected byte getPrimitiveByte(ResultSet rs, int columnIndex) throws SQLException {
        byte val = rs.getByte(columnIndex);
        if (rs.wasNull()) {
            throw new NullPointerException("字段为null，不能转为byte");
        }
        return val;
    }

    protected Short getShort(ResultSet rs, int columnIndex) throws SQLException {
        short val = rs.getByte(columnIndex);
        return rs.wasNull() ? null : val;
    }

    protected short getPrimitiveShort(ResultSet rs, int columnIndex) throws SQLException {
        short val = rs.getShort(columnIndex);
        if (rs.wasNull()) {
            throw new NullPointerException("字段为null，不能转为short");
        }
        return val;
    }

    protected Integer getInteger(ResultSet rs, int columnIndex) throws SQLException {
        int val = rs.getInt(columnIndex);
        return rs.wasNull() ? null : val;
    }

    protected int getPrimitiveInteger(ResultSet rs, int columnIndex) throws SQLException {
        int val = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            throw new NullPointerException("字段为null，不能转为int");
        }
        return val;
    }

    protected Long getLong(ResultSet rs, int columnIndex) throws SQLException {
        long val = rs.getLong(columnIndex);
        return rs.wasNull() ? null : val;
    }

    protected long getPrimitiveLong(ResultSet rs, int columnIndex) throws SQLException {
        long val = rs.getLong(columnIndex);
        if (rs.wasNull()) {
            throw new NullPointerException("字段为null，不能转为long");
        }
        return val;
    }

    protected Float getFloat(ResultSet rs, int columnIndex) throws SQLException {
        float val = rs.getFloat(columnIndex);
        return rs.wasNull() ? null : val;
    }

    protected float getPrimitiveFloat(ResultSet rs, int columnIndex) throws SQLException {
        float val = rs.getFloat(columnIndex);
        if (rs.wasNull()) {
            throw new NullPointerException("字段为null，不能转为float");
        }
        return val;
    }

    protected Double getDouble(ResultSet rs, int columnIndex) throws SQLException {
        double val = rs.getDouble(columnIndex);
        return rs.wasNull() ? null : val;
    }

    protected double getPrimitiveDouble(ResultSet rs, int columnIndex) throws SQLException {
        double val = rs.getDouble(columnIndex);
        if (rs.wasNull()) {
            throw new NullPointerException("字段为null，不能转为double");
        }
        return val;
    }

    protected byte[] getByteArr(ResultSet rs, int columnIndex) throws SQLException {
        return rs.getBytes(columnIndex);
    }
}
