% DBsetup.m

%INSTANCENAME = 'instance-1.7.0';
%DB = DBserver('localhost:2181','Accumulo',INSTANCENAME,'root','secret');
%G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo',INSTANCENAME,'localhost:2181','root','secret');
[DB,G] = DBsetupLLGrid('classdb54') %displays current tables and Graphulo connection
myName = 'note_events_';

tnEdge = [myName 'Tedge'];
tnEdgeT = [myName 'TedgeT'];
tnEdgeDeg = [myName 'TedgeDeg']; % this is actually the degrees of the columns of Tedge = rows of TedgeT. I would use the name EdgeDegT.
tnTxt = [myName 'TedgeTxt'];

tnFiltered = [myName 'TedgeFiltered'];
tnFilteredT = [myName 'TedgeFilteredT'];
tnFilteredRowDeg = [myName 'TedgeFilteredRowDeg'];
tnTfidf = [myName 'TedgeTfidf'];
tnTfidfT = [myName 'TedgeTfidfT'];

tnW = [myName 'Tw']; 
tnWT = [myName 'TwT'];
tnH = [myName 'Th'];
tnHT = [myName 'ThT'];

%Tedge = DB([myName 'Tedge'],[myName 'TedgeT']);
%TedgeDeg = DB([myName 'TedgeDeg']);
%TedgeTxt = DB([myName 'TedgeTxt']);

%Tw = DB([myName 'Tw'],[myName 'TwT']);
%Th = DB([myName 'Th'],[myName 'ThT']);
