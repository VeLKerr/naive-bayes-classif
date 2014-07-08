/*Операции, которые желательно выполнять только зайдя в систему как SYSDBA*/

-- Увеличение допустимого кол-ва транзакций, сеансов и процессов в Oracle DataBase. При чём: 
Alter System Set Processes=500 Scope=Spfile Sid='*'; -- если обозначим Processes = X, то: 
Alter System Set Sessions=555 Scope=Spfile Sid='*'; -- Sessions = Y = [1,1 *Х] + 6;
alter system set transactions=611 scope=Spfile sid='*'; -- Transactions = [Y] + 1.

alter system set transactions_per_rollback_segment=6 scope=Spfile sid='*';

Select Count(*) From V$session; -- посмотреть кол-во открытых сеансов.

-- Вывести допустимое кол-во транзакций, сеансов и процессов в Oracle DataBase.
Show Parameter Sessions;
Show Parameter Processes;
show parameter transactions;