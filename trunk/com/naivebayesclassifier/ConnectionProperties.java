package com.naivebayesclassifier;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Класс, описывающий свойства соединения с базой.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class ConnectionProperties {
    public static final String NAME = "jdbc:oracle:thin:@VeLKerr-PC:1521:XE";
    public static final String DRIVER_CLASS = "oracle.jdbc.driver.OracleDriver";
    public static final String US_PASS = "karina18";
    public static final String US_NAME = "VeLKerr";
    public static final ComboPooledDataSource cpds = new ComboPooledDataSource();
    
    static {
        try {
            cpds.setDriverClass(DRIVER_CLASS);
            cpds.setJdbcUrl(NAME);
            cpds.setUser(US_NAME);
            cpds.setPassword(US_PASS);
            cpds.setMaxStatements(10); //размер кэша, куда записываются использованные PrearedStatement'ы с целью
            //увеличения быстродействия при их повторном вызове. (Процедура stmt.close() осуществляет кеширование)
            cpds.setMaxPoolSize(100);
        } catch (PropertyVetoException ex) {
            Logger.getLogger(ConnectionProperties.class.getName()).log(Level.SEVERE, 
                    "The proposed change to a property represents an unacceptable value", ex);
        }
    }
    
    /**
     * Создать соединение.
     * @return объект соединения.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static Connection createConnection() throws ClassNotFoundException, SQLException{
//         Class.forName(DRIVER_CLASS);
//         return DriverManager.getConnection(NAME, US_NAME, US_PASS);
        return cpds.getConnection();
    }
    
    public void closePool(){
        cpds.close();
    }
}
