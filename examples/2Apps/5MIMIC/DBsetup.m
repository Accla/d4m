% DBsetup.m

%INSTANCENAME = 'instance-1.7.0';
%DB = DBserver('localhost:2181','Accumulo',INSTANCENAME,'root','secret');
%G = DBaddJavaOps('edu.mit.ll.graphulo.MatlabGraphulo',INSTANCENAME,'localhost:2181','root','secret');
[DB,G] = DBsetupLLGrid('class-db05'); %displays current tables and Graphulo connection
myName = 'note_events_';

tnEdge = [myName 'Tedge'];
tnEdgeT = [myName 'TedgeT'];
tnEdgeDeg = [myName 'TedgeDeg'];
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
