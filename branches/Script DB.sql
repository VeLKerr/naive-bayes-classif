-- �������� ������ ������ ������ ����� �������� �������.
Drop Table Words Cascade Constraints Purge;
Drop Table Messagescount Cascade Constraints Purge;
Drop Table Classifiedmessages Cascade Constraints Purge;

-- �������, ����������� ����� � ��������� �������.
Create Table Words(
  Text Varchar2(40), -- ����� (���������� ����)
  Spamcnt Number(3), -- ���-�� ���������� � ����-����������
  hamCnt number(3) -- ���-�� ���������� � ���������� ����������
);
-- �������� ���������� �����
Alter Table Words Add Constraint Xpkwords Primary Key (Text);

-- �������, ����������� ���-�� ��������� ������������ ������ � ��������� �������.
-- ������ ������� ����� ����� ������ 2 ������: ��� ������ spam � ��� ������ ham.
Create Table Messagescount(
  Mestype Varchar2(10), -- ����� ���������. ����� ��������� �������� �� ��������� {spam, ham}
  counter number(3) -- ���-�� ��������� � �������
);
-- �������� ���������� �����
Alter Table Messagescount Add Constraint Xpkmessagescount Primary Key (Mestype);

-- �������, ����������� ������������������ ���������
Create Table Classifiedmessages(
  Filename Varchar2(30), -- ��� ����� � ���������� (���������� ����)
  Expertest Number(1), -- ���������� ������ (�� ������ ����� �����)
  Systemest Number(1) -- ������ � ������� ���
  /*������ ExpertEst � SystemEst, �� ��������, �������� ���������� �����������,
  �� �.�. Oracle �� ������������ boolean, ��� �������� �������. ���
  ���������� ����� ��������� �������� �� ��������� {0;1}, ��� 0 - ��������
  �������������� � ������ SPAM, � 1 - � HAM.*/
);
-- �������� ���������� �����
Alter Table Classifiedmessages Add Constraint Xpkclassifiedmessages Primary Key (Filename);
Commit;