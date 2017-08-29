
myPrefix = 'DH_';
durability = 'log';

% wget http://data.dws.informatik.uni-mannheim.de/hyperlinkgraph/2012-08/pld-arc.gz

fname = 'example_arcs';
filepath = [pwd filesep fname];
tname = [myPrefix '2012_pld_arc'];
MyDBsetup;
TNadjUU = [tname '_TgraphAdjUU'];
TNedge = [tname '_TgraphEdge'];
nl = char(10);

TI = DBaddJavaOps('edu.mit.ll.graphulo.tricount.TriangleIngestor',INSTANCENAME,ZKHOSTS,'root','secret');
%TI.ingestDirectory(filepath, TNadjUU, [], true, true); %upper triangle
TI.ingestCombinedFile(filepath, TNadjUU, TNedge, false, false);

for NUMTAB=4; %8:8:40

  tnameTmp = [TNadjUU '_triCount_tmpA'];
  LSDB = ls(DB);
  if StrSearch(LSDB,[tnameTmp ' ']) >= 1
      Ttmp = DB(tnameTmp);
      deleteForce(Ttmp);
  end

  TadjUU = DB(TNadjUU);
  Tedge = DB(TNedge); 
  Ttmp = DB(tnameTmp);
  numEntries = nnz(TadjUU);
  
  splitPoints = G.findEvenSplits(TNadjUU, NUMTAB-1, numEntries / NUMTAB, 1, 1); % split point modifiers
  putSplits(TadjUU, splitPoints);
  G.Compact(TNadjUU); % force new splits
  [splitPointsA,splitSizesA] = getSplits(TadjUU);
  fprintf('A splitsSizes %s\n', splitSizesA);
  splitPoints = G.findEvenSplits(TNedge, NUMTAB-1, numEntries / NUMTAB, 1, 1); % split point modifiers
  putSplits(Tedge, splitPoints);
  G.Compact(TNedge); % force new splits
  [splitPointsE,splitSizesE] = getSplits(Tedge);
  fprintf('E splitsSizes %s\n', splitSizesE);

  Ttmp = DB(tnameTmp);
  % splitPoints = G.findEvenSplits(TNadjUU, NUMTAB-1, numEntries / NUMTAB, 1.0, SPLITS_RATE_EXP_INV);
  putSplits(Ttmp, splitPoints);
  G.Compact(tnameTmp);

  specialLongList = javaObject('java.util.ArrayList');
  tic;
  triangles = G.triCountAdjEdge(TNadjUU, TNedge, [], [], durability, specialLongList);
  triCountTime = toc; fprintf('Graphulo TriCountEdge Time: %f\n',triCountTime);
  fprintf('Triangles: %f\n',triangles);
  numpp = specialLongList.get(0); clear specialLongList;

  row = [tname 'AdjEdge__nt' num2str(NUMTAB)];
  Ainfo = Assoc('','','');
  Ainfo = Ainfo + Assoc(row,['triCountGraphulo' nl],[num2str(triCountTime) nl]);
  Ainfo = Ainfo + Assoc(row,['numpp' nl],[num2str(numpp) nl]);
  Ainfo = Ainfo + Assoc(row,['triangles' nl],[num2str(triangles) nl]);
  Ainfo = Ainfo + Assoc(row,['adjnnz' nl],[num2str(numEntries) nl]);
  Ainfo = Ainfo + Assoc(row,['splitSizesE' nl],[splitSizesE nl]);
  Ainfo = Ainfo + Assoc(row,['splitSizesA' nl],[splitSizesA nl]);
  util_UpdateInfo(Ainfo);


deleteForce(DB(tnameTmp));
deleteForce(TadjUU);
deleteForce(Tedge);
pause(3)

end
