/*Операции, которые желательно выполнять только зайдя в систему как SYSDBA*/

-- Увеличение допустимого кол-ва транзакций, сеансов и процессов в Oracle DataBase. При чём: 
Alter System Set Processes=500 Scope=Spfile Sid='*'; -- если обозначим Processes = X, то: 
Alter System Set Sessions=555 Scope=Spfile Sid='*'; -- Sessions = Y = [1,1 *Х] + 6;
Alter System Set Transactions=611 Scope=Spfile Sid='*'; -- Transactions = [Y] + 1.
-- Увеличение максимального допустимого кол-ва открытых курсоров (по умолчанию открытых курсоров в Oracle Express 10g 300)
alter system set open_cursors=1000 scope=Spfile Sid='*';

alter system set transactions_per_rollback_segment=6 scope=Spfile sid='*';

Select Count(*) From V$session; -- посмотреть кол-во открытых сеансов.

-- Вывести допустимое кол-во транзакций, сеансов и процессов в Oracle DataBase.
Show Parameter Sessions;
Show Parameter Processes;
Show Parameter Transactions;
-- Вывести максимальное допустимое кол-во открытых курсоров
Show Parameter open_cursors; 

Select * 
From All_Tables
Where tablespace_name like 'USERS' AND Owner like 'VELKERR';