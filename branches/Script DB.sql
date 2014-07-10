-- Удаление старых версий таблиц перед прогоном скрипта.
Drop Table Words Cascade Constraints Purge;
Drop Table Messagescount Cascade Constraints Purge;
Drop Table Classifiedmessages Cascade Constraints Purge;

-- Таблица, описывающая слова в обучающей выборке.
Create Table Words(
  Text Varchar2(40), -- слово (уникатьное поле)
  Spamcnt Number(5), -- кол-во повторений в спам-сообщениях
  hamCnt number(5) -- кол-во повторений в нормальных сообщениях
);
-- создание первичного ключа
Alter Table Words Add Constraint Xpkwords Primary Key (Text);

-- Таблица, описывающая кол-во сообщений определённого класса в обучающей выборке.
-- Данная таблица может иметь только 2 записи: для класса spam и для класса ham.
Create Table Messagescount(
  Mestype Varchar2(10), -- класс сообщения. Может принимать значения из множества {spam, ham}
  counter number(5) -- кол-во сообщений в выборке
);
-- создание первичного ключа
Alter Table Messagescount Add Constraint Xpkmessagescount Primary Key (Mestype);

-- Таблица, описывающая классифицированные сообщения
Create Table Classifiedmessages(
  Filename Varchar2(30), -- имя файла с сообщением (уникатьное поле)
  Expertest Number(1), -- экспертная оценка (на основе имени файла)
  Systemest Number(1) -- оценка с помощью НБК
  /*оценки ExpertEst и SystemEst, по существу, являются булевскими переменными,
  но т.к. Oracle не поддерживает boolean, они выражены числами. Эти
  переменные могут принимать значения из множества {0;1}, где 0 - означает
  принадлежность к классу HAM, а 1 - к SPAM.*/
);
-- создание первичного ключа
Alter Table Classifiedmessages Add Constraint Xpkclassifiedmessages Primary Key (Filename);
Commit;