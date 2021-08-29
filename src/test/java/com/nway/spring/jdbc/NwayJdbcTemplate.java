package com.nway.spring.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NwayJdbcTemplate extends JdbcTemplate {

    protected final Log logger = LogFactory.getLog(NwayJdbcTemplate.class);

    public NwayJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    @Nullable
    public <T> T query(
            PreparedStatementCreator psc, @Nullable final PreparedStatementSetter pss, final ResultSetExtractor<T> rse)
            throws DataAccessException {

        Assert.notNull(rse, "ResultSetExtractor must not be null");
        logger.debug("Executing prepared SQL query");

        return execute(psc, new PreparedStatementCallback<T>() {
            @Override
            @Nullable
            public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
                ResultSet rs = null;
                try {
                    if (pss != null) {
                        pss.setValues(ps);
                    }
                    long begin = System.currentTimeMillis();
                    rs = ps.executeQuery();
                    logger.info("executeQuery : " + (System.currentTimeMillis() - begin));
                    begin = System.currentTimeMillis();
                    T data = rse.extractData(rs);
                    logger.info("extractData : " + (System.currentTimeMillis() - begin));
                    return data;
                } finally {
                    JdbcUtils.closeResultSet(rs);
                    if (pss instanceof ParameterDisposer) {
                        ((ParameterDisposer) pss).cleanupParameters();
                    }
                }
            }
        }, true);
    }


    @Nullable
    private <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action, boolean closeResources)
            throws DataAccessException {

        Assert.notNull(psc, "PreparedStatementCreator must not be null");
        Assert.notNull(action, "Callback object must not be null");
        if (logger.isDebugEnabled()) {
            String sql = getSql(psc);
            logger.debug("Executing prepared SQL statement" + (sql != null ? " [" + sql + "]" : ""));
        }

        long begin = System.currentTimeMillis();
        Connection con = DataSourceUtils.getConnection(obtainDataSource());
        logger.info("getConnection : " + (System.currentTimeMillis() - begin));
        PreparedStatement ps = null;
        try {
            ps = psc.createPreparedStatement(con);
            applyStatementSettings(ps);
            T result = action.doInPreparedStatement(ps);
            begin = System.currentTimeMillis();
            handleWarnings(ps);
            logger.info("handleWarnings : " + (System.currentTimeMillis() - begin));
            return result;
        } catch (SQLException ex) {
            // Release Connection early, to avoid potential connection pool deadlock
            // in the case when the exception translator hasn't been initialized yet.
            if (psc instanceof ParameterDisposer) {
                ((ParameterDisposer) psc).cleanupParameters();
            }
            String sql = getSql(psc);
            psc = null;
            JdbcUtils.closeStatement(ps);
            ps = null;
            DataSourceUtils.releaseConnection(con, getDataSource());
            con = null;
            throw translateException("PreparedStatementCallback", sql, ex);
        } finally {
            if (closeResources) {
                if (psc instanceof ParameterDisposer) {
                    ((ParameterDisposer) psc).cleanupParameters();
                }
                JdbcUtils.closeStatement(ps);
                DataSourceUtils.releaseConnection(con, getDataSource());
            }
        }
    }

    @Nullable
    private static String getSql(Object sqlProvider) {
        if (sqlProvider instanceof SqlProvider) {
            return ((SqlProvider) sqlProvider).getSql();
        } else {
            return null;
        }
    }

}
