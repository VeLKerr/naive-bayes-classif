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
    public static final String US_PASS = "karina18";
    public static final String US_NAME = "VeLKerr";
    private static final BasicDataSource DATA_SOURCE = setUpDataSource();
//    private static final ComboPooledDataSource cpds = new ComboPooledDataSource();
//    private static DataSource pooled = null;
    
    private static abstract class PoolData{
        private static final int MAX_POOL_SIZE = 100;
        private static final int MAX_STATEMENTS = 10;
        private static final int ACQUISITION_ATTEMPTS = 100;
    }
    
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
    
//    static {
//        try {
            //1-й вар
//            DataSource unpooled = DataSources.unpooledDataSource(DRIVER_URL, US_NAME, US_PASS);
//            Map<String, Object> props = new HashMap<>();
//            props.put("maxPoolSize", PoolData.MAX_POOL_SIZE);
//            props.put("maxStatements", PoolData.MAX_STATEMENTS);
//            props.put("acquireRetryAttempts", PoolData.ACQUISITION_ATTEMPTS);
//            props.put("driverClass", DRIVER_CLASS);
//            pooled = DataSources.pooledDataSource(unpooled, props);
            
//            cpds.setAcquireRetryAttempts(100);
//            cpds.setDriverClass(DRIVER_CLASS);
//            cpds.setJdbcUrl(DRIVER_URL);
//            cpds.setUser(US_NAME);
//            cpds.setPassword(US_PASS);
//            cpds.setMaxStatements(PoolData.MAX_STATEMENTS); //размер кэша, куда записываются использованные PrearedStatement'ы с целью
//            //увеличения быстродействия при их повторном вызове. (Процедура stmt.close() осуществляет кеширование)
//            cpds.setMaxPoolSize(PoolData.MAX_POOL_SIZE);
//        } catch (Exception ex) {
//            Logger.getLogger(ConnectionProperties.class.getName()).log(Level.SEVERE,
//                    "The proposed change to a property represents an unacceptable value", ex);
//        }
//    }
    
    /**
     * Создать соединение.
     * @return объект соединения.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static Connection createConnection() throws ClassNotFoundException, SQLException{
//         Class.forName(DRIVER_CLASS);
//         return DriverManager.getConnection(DRIVER_URL, US_NAME, US_PASS);
//        return cpds.getConnection();
//        return pooled.getConnection();
        return DATA_SOURCE.getConnection();
    }
    
    public void closePool() throws SQLException{
//        cpds.close();
        DATA_SOURCE.close();
    }
}
