package com.naivebayesclassifier.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import com.naivebayesclassifier.ConnectionProperties;
import com.naivebayesclassifier.ParameterQueryBuilder;

/**
 * Класс, описывающий методы доступа к таблице слов в обучающей выборке
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class Words{
    
    private static abstract class Queries{
        private static final String findByText = "SELECT * FROM Words WHERE text = ?";
        private static final String countUniqueWords = "Select Count(Text) From Words";
    }
    
    /**
     * Рассчитать кол-во уникальных слов в обучающей выборке.
     * @return кол-во уникальных слов в обучающей выборке
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static int countUniqueWords() throws ClassNotFoundException, SQLException{
        int count = 0;
        Connection conn = ConnectionProperties.createConnection();
        Statement st = conn.createStatement();
        st.executeQuery(Queries.countUniqueWords);
        ResultSet rs = st.getResultSet();
        if(rs.next()){
            count = rs.getInt(1);
        }
        rs.close();
        st.close();
        conn.close();
        return count;
    }
    
    /**
     * Рассчитать общее кол-во слов в сообщениях определённого типа.
     * @param isSpam если <code>true</code>, рассчитываем для спам-сообщений. Иначе - для нормальных.
     * @return общее кол-во слов в сообщениях определённого типа в обучающей выборке.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static int countSum(boolean isSpam) throws ClassNotFoundException, SQLException{
        int count = 0;
        Connection conn = ConnectionProperties.createConnection();
        Statement st = conn.createStatement();
        st.executeQuery(new ParameterQueryBuilder(isSpam).buildCount());
        ResultSet rs = st.getResultSet();
        if(rs.next()){
            count = rs.getInt(1);
        }
        rs.close();
        st.close();
        conn.close();
        return count;
    }
    
    /**
     * Добавления сообщения в обучающую выборку.
     * @param words список слов сообщения.
     * @param isSpam является ли сообщение спасмом.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static void addAll(List<String> words, boolean isSpam) throws ClassNotFoundException, SQLException{
        Connection conn = ConnectionProperties.createConnection();
        PreparedStatement ps = null;
        ParameterQueryBuilder pqb = new ParameterQueryBuilder(isSpam);
        for(String word: words){
            ps = conn.prepareStatement(Queries.findByText);
            ps.setString(1, word);
            ps.executeQuery();
            ResultSet rs = ps.getResultSet();
            if(rs.next()){
                ps = conn.prepareStatement(pqb.buildUpdate());
                ps.setString(1, word);
                ps.setString(2, word);
            }
            else{
                ps = conn.prepareStatement(pqb.buildInsert());
                ps.setString(1, word);
            }
            ps.executeUpdate();
            rs.close();
            ps.close();
        }
        conn.close();
    }
    
    /**
     * Добавление слова в таблицу.
     * @param word слово
     * @param isSpam <code>true</code> если слово принадлежит спам-сообщению.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static void add(String word, boolean isSpam) throws ClassNotFoundException, SQLException{
        Connection conn = ConnectionProperties.createConnection();
        PreparedStatement ps = conn.prepareStatement(Queries.findByText);
        ps.setString(1, word);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        ParameterQueryBuilder pqb = new ParameterQueryBuilder(isSpam);
        if(rs.next()){
            ps = conn.prepareStatement(pqb.buildUpdate());
            ps.setString(1, word);
            ps.setString(2, word);
        }
        else{
            ps = conn.prepareStatement(pqb.buildInsert());
            ps.setString(1, word);
        }
        ps.executeUpdate();
        rs.close();
        ps.close();
        conn.close();
    }
    
    /**
     * Подсчитать, сколько раз встречалось данное слово в сообщениях обучающей выборки.
     * @param word слово 
     * @param isSpam если <code>true</code>, подсчёт ведётся только в спам-сообщениях. Иначе - только в нормальных.
     * @return кол-во повторений данного слова.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    public static int getCount(String word, boolean isSpam) throws ClassNotFoundException, SQLException{
        int count = 0;
        Connection conn = ConnectionProperties.createConnection();
        PreparedStatement ps = conn.prepareStatement(Queries.findByText);
        ps.setString(1, word);
        ps.executeQuery();
        ResultSet rs = ps.getResultSet();
        if(rs.next()){
            if(isSpam){
                count = rs.getInt(2);
            }
            else{
                count = rs.getInt(3);
            }
        }
        rs.close();
        ps.close();
        conn.close();
        return count;
    }
    
}
