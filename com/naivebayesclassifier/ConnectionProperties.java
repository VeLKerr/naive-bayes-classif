package com.naivebayesclassifier;

import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * Класс, описывающий свойства соединения с базой.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class ConnectionProperties {
    public static final String DRIVER_URL = "jdbc:oracle:thin:@VeLKerr-PC:1521:XE";
    public static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
    public static final String US_PASS = "PASSWORD";
    public static final String US_NAME = "LOGIN";
    private static final BasicDataSource DATA_SOURCE = setUpDataSource();
    
    /**
     * Провести первоначальную конфигурацию Connection Pool.
     * @return сконфигурированный Connection Pool.
     */
    private static BasicDataSource setUpDataSource(){
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName(DRIVER_CLASS);
        ds.setUrl(DRIVER_URL);
        ds.setUsername(US_NAME);
        ds.setPassword(US_PASS);
        ds.setMaxActive(-1);
        ds.setPoolPreparedStatements(true);
        return ds;
    }
    
    /**
     * Создать соединение.
     * @return объект соединения.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static Connection createConnection() throws ClassNotFoundException, SQLException{
        Connection conn = DATA_SOURCE.getConnection();
        conn.setAutoCommit(true);
        return conn;
    }
    
    /**
     * Закрыть Connection Pool.
     */
    public void closePool() throws SQLException{
        DATA_SOURCE.close();
    }
}
