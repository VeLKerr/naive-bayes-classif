package com.naivebayesclassifier;

import com.naivebayesclassifier.dao.Words;
import com.naivebayesclassifier.dao.MessageCounts;
import java.sql.SQLException;
import java.util.List;

/**
 * <font color="blue">Реализация алгоритма НБК.</font>
 * @author Kirius VeLKerr (Ivchenko Oleg)
 */
public class NaiveBayes {
    //Коэффициент размытия Лапласа
    private static final int LAPLASIAN = 1;
    //Пороговое значение вероятности P(SPAM), выше которой сообщение
    //классифицируется как спам.
    private static double HAM_LIMIT = 0.52;
    //спиок слов сообщения
    private final List<String> words;
    
    /**
     * коллекция слов, ещё не встречавшихся в обучающей выборке
     */
    private final UniqueWords uniqueWords;
    
    public NaiveBayes(List<String> words) {
        this.words = words;
        this.uniqueWords = new UniqueWords();
    }
    
    /**
     * Получить слова, ещё не встречавшиеся в обучающей выборке.
     * @return слова.
     */
    public UniqueWords getUniqueWords() {
        return uniqueWords;
    }
    
    /**
     * Установаить пороговое значение вероятности P(SPAM), выше которой сообщение
     * классифицируется как спам. По умолчанию <code>hamLimit = 0.52</code>.
     * @param hamLimit новое значение.
     */
    public static void setHamLimit(double hamLimit){
        HAM_LIMIT = hamLimit;
    }
    
    /**
     * Рассчёт вероятности класса. Т.е. реализация формулы 
     * <code> <center>log(Dc/D)</center></code>,<br/>
     * <ul>
     * <li>где <code>Dc</code> - кол-во сообщений в тренировочном наборе,
     * относящихся к класу С;</li>
     * <li><code>D</code> - общее кол-во сообщений в тренировочном наборе.</li>
     * </ul>
     * @param isSpam определяет, для какого класса расчитівается вероятность.
     * @return значение вероятности.
     * @throws ClassNotFoundException при ошибке нахождения JDBC-драйвера
     * @throws SQLException при ошибке выполнения запроса.
     */
    private static double getClassProbability(boolean isSpam) throws SQLException, ClassNotFoundException {
        int spamCnt = MessageCounts.getCounter(true);
        int hamCnt = MessageCounts.getCounter(false);
        int numerator = 0;
        if (isSpam) {
            numerator = spamCnt;
        } else {
            numerator = hamCnt;
        }
        return Math.log((double) numerator / ((double) hamCnt + (double) spamCnt));
    }
    
    /**
     * Рассчёт вероятности того, что сообщение принадлежит к классу С
     * (без учёта константы вероятности класса).
     * Рассчёт производится по формуле:
     * <code> <center>foreach(word) { log( (Wc+LAPLASIAN)/(V+Lc) ) }</center></code>,<br/>
     * <ul>
     *  <li><code>V</code> - общее количество слов во всех документах обучающей выборки
     * (повторения не учитываются);</li>
     *  <li><code>Lc</code>суммарное количество слов в документах класса C в обучающей выборке </li>
     *  <li><code>Wc</code> - сколько раз слово встречалось в документах класса C в обучающей выборке</li>
     * </ul>
     * <i><u>Примечание: </u></i> выражение <code>V + Lc</code> будет одинаковым для всех 
     * слов сообщения.
     * @see getClassProbability(boolean isSpam)
     * @param isSpam если <code>true</code>, С = SPAM. Иначе С = HAM.
     * @return значение вероятности.
     */
    private double getProbabilityInClass(boolean isSpam){
        double prob = 0;
        try{
            int denominator = LAPLASIAN * Words.countUniqueWords() + Words.countSum(isSpam);
            for(String word: words){
                int cnt = Words.getCount(word, isSpam);
                if(cnt < 0){
                    cnt = 0;
                    uniqueWords.add(word);
                }
                prob += Math.log((double)(cnt + LAPLASIAN) / denominator);
            }
            prob += getClassProbability(isSpam);
        }
        catch(ClassNotFoundException | SQLException ex){
            ex.printStackTrace();
        }
        return prob;
    }
    
    /**
     * Рассчёт вероятности того, что сообщение принадлежит к классу С.
     * @param isSpam если <code>true</code>, С = SPAM. Иначе С = HAM.
     * @return значение вероятности.
     */
    private double getGeneralProbability(boolean isSpam){
        double probNumer = Math.exp(getProbabilityInClass(isSpam));
        double otherProb = Math.exp(getProbabilityInClass(!isSpam));
        return probNumer / (probNumer + otherProb);
    }
    
    /**
     * Принятие окончательного решения, является ли письмо спамом.
     * @return <code>true</code> если сообщение является спамом. Иначе
     * <code>false</code>.
     */
    public boolean isSpam(){
        return getGeneralProbability(true) > HAM_LIMIT;
    }
}
