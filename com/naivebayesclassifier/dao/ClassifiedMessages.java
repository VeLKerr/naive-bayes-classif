package com.naivebayesclassifier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.naivebayesclassifier.ConnectionProperties;
import static com.naivebayesclassifier.Main.SPAM_FILE_FEATURE;

/**
 * Класс, описывающий методы доступа к таблице классифицированных сообщений.
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class ClassifiedMessages {
    private static abstract class Queries{
        private static final String countRelevant = "select count(Filename) from Classifiedmessages where Expertest = systemEst";
        private static final String count = "Select Count(Filename)\n" +
        "From Classifiedmessages\n" +
        "Where Expertest = ? And Systemest = ?";
        private static final String add = "Insert Into Classifiedmessages Values (?, ?, ?)";
        private static final String deleteAll = "DELETE FROM ClassifiedMessages";
    }
    
    /**
     * Добавить сообщение в таблицу.
     * @param filename название файла сообщения
     * @param isSystemSpam флажок, устанавливающий, определила ли система
     * это сообщение как спам.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static void add(String filename, boolean isSystemSpam) throws SQLException, ClassNotFoundException{
        Connection conn = ConnectionProperties.createConnection();
        PreparedStatement ps = conn.prepareStatement(Queries.add);
        ps.setString(1, filename);
        if(filename.contains(SPAM_FILE_FEATURE)){
            ps.setInt(2, 1);
        }
        else{
            ps.setInt(2, 0);
        }
        if(isSystemSpam){
            ps.setInt(3, 1);
        }
        else{
            ps.setInt(3, 0);
        }
        ps.executeUpdate();
        ps.close();
        conn.close();
    }
    
    /**
     * Получить кол-во релевантных сообщений (т.е. тех, для которых классификация
     * по НБК совпадает с экпертным выодом).
     * @return кол-во релевантных сообщений.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера.
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static int getRelevantCount() throws SQLException, ClassNotFoundException{
        int counter = 0;
        Connection conn = ConnectionProperties.createConnection();
        Statement st = conn.createStatement();
        st.executeQuery(Queries.countRelevant);
        ResultSet rs = st.getResultSet();
        if(rs.next()){
            counter = rs.getInt(1);
        }
        rs.close();
        st.close();
        conn.close();
        return counter;
    }
    
    /**
     * Получить кол-во сообщений с заданными результатами классификации по НБК
     * и экспертными оценками.
     * @param conn соединение с БД.
     * @param isExpertSpam если <code>true</code>, выбираются сообщения, для
     * которых экспертная оценка - SPAM. Иначе - HAM.
     * @param isSystemSpam если <code>true</code>, выбираются сообщения, которые
     * отнесены к спаму с помощью НБК.
     * @return кол-во сообщений.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера.
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static int count(Connection conn, boolean isExpertSpam, boolean isSystemSpam) throws SQLException, ClassNotFoundException{
        int counter = 0;
        PreparedStatement ps = conn.prepareStatement(Queries.count);
        if(isExpertSpam){
            ps.setInt(1, 1);
        }
        else{
            ps.setInt(1, 0);
        }
        if(isSystemSpam){
            ps.setInt(2, 1);
        }
        else{
            ps.setInt(2, 0);
        }
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        if(rs.next()){
            counter = rs.getInt(1);
        }
        rs.close();
        ps.close();
        return counter;
    }    
}
