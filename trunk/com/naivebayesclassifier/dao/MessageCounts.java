package com.naivebayesclassifier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.naivebayesclassifier.ConnectionProperties;

/**
 * Класс, описывающий методы доступа к таблице кол-ва сообщений определённого
 * класса <b>в обучающей выборке</b>.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class MessageCounts {
    
    private static abstract class Queries{
        private static final String createTypes = "Insert All Into Messagescount Values (\'spam\', 0)\n" +
"Into Messagescount Values (\'ham\', 0)\n" +
"Select null From Dual";
        private static final String increment = "Update Messagescount Set Counter = Counter + 1 Where Mestype = ?";
        private static final String getCounter = "Select Counter From Messagescount Where Mestype like ?";
        private static final String countAll = "Select Sum(Counter) From Messagescount";
    }
    
    private static String buildParameter(boolean isSpam){
        if(isSpam){
            return "spam";
        }
        return "ham";
    }
    
    /**
     * Создание записей с классами сообщений. Количества сообщений, относящихся
     * к классам заполняются нулями.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static void createMessageTypes() throws ClassNotFoundException, SQLException {
        Connection conn = ConnectionProperties.createConnection();
        conn.createStatement().executeUpdate(Queries.createTypes);
        conn.close();
    }
//    public static void createMessageTypes() throws ClassNotFoundException, SQLException {
//        Connection conn = ConnectionProperties.createConnection();
//        PreparedStatement ps = conn.prepareStatement(Queries.createTypes);
//        ps.setString(1, "spam");
//        ps.executeUpdate();
//        ps.setString(1, "ham");
//        ps.executeUpdate();
//        ps.close();
//        conn.close();
//    }
    
    /**
     * Увеличить счётчик сообщений определённого класса.
     * @param isSpam если <code>true</code>, счётчик увеличивается для
     * спам-сообщений. Иначе - для нормальных.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static void incrementCounter(boolean isSpam) throws SQLException, ClassNotFoundException{
        Connection conn = ConnectionProperties.createConnection();
        PreparedStatement ps = conn.prepareStatement(Queries.increment);
        String parameter = buildParameter(isSpam);
        ps.setString(1, parameter);
        ps.executeUpdate();
        ps.close();
        conn.close();
    }
    
    /**
     * Получить текущее значение счётчика сообщений определённого класса.
     * @param isSpam если <code>true</code>, для спам-сообщений. Иначе -
     * для нормальных.
     * @return значение счётчика.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static int getCounter(boolean isSpam) throws ClassNotFoundException, SQLException{
        int count = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        conn = ConnectionProperties.createConnection();
        ps = conn.prepareStatement(Queries.getCounter);
        ps.setString(1, buildParameter(isSpam));
        ResultSet rs = ps.executeQuery();
        if(rs.next()){
            count = rs.getInt(1);
        }
        rs.close();
        ps.close();
        conn.close();
        return count;
    }
}
