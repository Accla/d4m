% See http://webdatacommons.org/hyperlinkgraph/
% Counts degrees.

myPrefix = 'DH_';
durability = 'log';

% wget http://data.dws.informatik.uni-mannheim.de/hyperlinkgraph/2012-08/pld-arc.gz
fname = 'example_arcs';
filepath = [pwd filesep fname];
tname = [myPrefix '2012_pld_arc'];
MyDBsetup;
TNadjUU = [tname '_TgraphAdjUU'];
TNadjUUDeg = [tname '_TgraphAdjUU_Deg'];
nl = char(10);

TI = DBaddJavaOps('edu.mit.ll.graphulo.tricount.TriangleIngestor',INSTANCENAME,ZKHOSTS,'root','secret');
%TI.ingestDirectory(filepath, TNadjUU, [], true, true); %upper triangle
TI.doCountDegree(TNadjUUDeg);
TI.ingestCombinedFile(filepath, [], [], false, true);

Tdeg = DB(TNadjUUDeg);
A = Tdeg(:,:);
A = str2num(A);
Assoc2CSV(A, char(10), char(9), [fname '_deg.tsv']);
