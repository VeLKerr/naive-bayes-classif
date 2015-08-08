Delete From Words; Commit;
Delete From Messagescount; Commit;
Delete From Classifiedmessages; Commit;

Select * From Words; 
Select * From Messagescount;
Select * From Classifiedmessages;

Select * From Words
Where Spamcnt <> 0; 

Insert All Into Messagescount Values ('spam', 0)
Into Messagescount Values ('ham', 0)
Select Null From Dual;

Update Words
Set Hamcnt = Hamcnt + 1
Where Text = 'how';

update words
set hamcnt = 1 + (
  select hamcnt
  from words
  where text like :text
)
where text like :text;

insert into words values ('how', 1, 1);
insert into words values ('now', 2, 1);

select sum(hamCnt)
from words;

select hamCnt
from words
Where Text Like 'how';

Update Messagescount
Set Counter = 1 + (
  Select Counter
  From Messagescount
  Where Mestype like :type
)
Where Mestype like :type;

Update Messagescount Set Counter = Counter + 1 Where Mestype = 'spam';

Select Counter
From Messagescount
Where Mestype Like 'spam';

Select Count(Text) --|V|
From Words;

Select Sum(Hamcnt)
From Words; --Lc1

Select Sum(Counter)
From Messagescount;

Select Count(Filename)
From Classifiedmessages
Where Expertest = Systemest;

Select Count(Filename)
From Classifiedmessages
Where Expertest = 0 And Systemest = 1;

Insert Into Classifiedmessages Values ('file 1', 1, 0);

Select * From Classifiedmessages;

Select * 
From All_Tables
Where Tablespace_Name like 'USERS';
