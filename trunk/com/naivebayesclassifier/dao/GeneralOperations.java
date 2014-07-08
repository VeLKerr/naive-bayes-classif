package com.naivebayesclassifier.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.naivebayesclassifier.ConnectionProperties;

/**
 * Класс, для описания операций, который проводятся со всеми таблицами БД.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class GeneralOperations {
    private static final String deleteAll = "Delete From ";
    private static final String getAllTableNames = "Select * \n" +
        "From All_Tables\n" +
        "Where Tablespace_Name like 'USERS'"; //запрос для вывода всех пользовательских таблиц
    
    /**
     * Удалить записи из всех пользовательских таблиц БД.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static void deleteAll() throws SQLException, ClassNotFoundException{
        Connection conn = ConnectionProperties.createConnection();
        Statement stmt = conn.createStatement();
        stmt.executeQuery(getAllTableNames);
        ResultSet rs = stmt.getResultSet();
        while(rs.next()){
            stmt.executeUpdate(deleteAll + rs.getString(2));
        }
        rs.close();
        stmt.close();
        conn.close();
    }
    
    /**
     * Очистка одной таблицы БД.
     * @param fromTable название таблицы.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static void deleteAll(String fromTable) throws SQLException, ClassNotFoundException{
        Connection conn = ConnectionProperties.createConnection();
        conn.createStatement().executeUpdate(deleteAll + fromTable);
        conn.close();
    }
}
