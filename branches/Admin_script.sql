/*��������, ������� ���������� ��������� ������ ����� � ������� ��� SYSDBA*/

-- ���������� ����������� ���-�� ����������, ������� � ��������� � Oracle DataBase. ��� ���: 
Alter System Set Processes=500 Scope=Spfile Sid='*'; -- ���� ��������� Processes = X, ��: 
Alter System Set Sessions=555 Scope=Spfile Sid='*'; -- Sessions = Y = [1,1 *�] + 6;
alter system set transactions=611 scope=Spfile sid='*'; -- Transactions = [Y] + 1.

alter system set transactions_per_rollback_segment=6 scope=Spfile sid='*';

Select Count(*) From V$session; -- ���������� ���-�� �������� �������.

-- ������� ���������� ���-�� ����������, ������� � ��������� � Oracle DataBase.
Show Parameter Sessions;
Show Parameter Processes;
show parameter transactions;