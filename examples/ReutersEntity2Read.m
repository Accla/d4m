function A=ReutersEntity2Read()

% Reads in Reuters Entity data and constructs
% an Associative array.

% Set data dir.
dataDir = 'reuters_entities2/';


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Doc/Time Links.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
tic;
  [tDocIDstr tEntIDstr tChunkIDstr tLenStr tPosStr tSourceStr] = ParseFile([dataDir 'doc_time_links.csv'],',');
  disp(['Doc/time links: ' num2str(NumStr(tEntIDstr))]);

  % Combine tChunkIDstr tLenStr tPosStr into 1 str.
  tPosFull = sscanf(tChunkIDstr,'%d,',inf) + (sscanf(tPosStr,'%d,',inf) ./ sscanf(tLenStr,'%d,',inf));
%  tPosFullStr = CatStr(CatStr(tChunkIDstr,'/',tLenStr),'/',tPosStr);
  clear('tChunkIDstr','tLenStr','tPosStr','tSourceStr');
disp(['doc_time_links.csv parse time: ' num2str(toc)]); tic;
  AdocTime = Assoc(tDocIDstr,tEntIDstr,tPosFull);
  clear('tDocIDstr','tEntIDstr','tPosFull');
disp(['Doc/time assoc time: ' num2str(toc)]);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Doc Nodes.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
tic;
  [DocIDstr DocNameStr DocTypeStr] = ParseFile([dataDir 'doc_nodes.csv'],',');
  disp(['Doc nodes: ' num2str(NumStr(DocIDstr))]);

  % Combine DocNameStr DocTypeStr.
  DocFullStr = CatStr(DocTypeStr,'/',DocNameStr);
  clear('DocTypeStr','DocNameStr');

disp(['doc_nodes.csv parse time: ' num2str(toc)]); tic;
  AdocNodes = Assoc(DocIDstr,1,DocFullStr);
  clear('DocIDstr','DocFullStr');
disp(['Doc nodes assoc time: ' num2str(toc)]);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Time Nodes.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
tic;
  [TimeIDstr TimeNameStr TimeTypeStr] = ParseFile([dataDir 'time_nodes.csv'],',');
  disp(['Time nodes: ' num2str(NumStr(TimeIDstr))]);

  % Combine TimeNameStr TimeTypeStr.
  TimeFullStr = CatStr(TimeTypeStr,'/',TimeNameStr);
  clear('TimeTypeStr','TimeNameStr');
disp(['time_nodes.csv parse time: ' num2str(toc)]); tic;
  AtimeNodes = Assoc(TimeIDstr,1,TimeFullStr);
  clear('TimeIDstr','TimeFullStr');
disp(['Time nodes assoc time: ' num2str(toc)]);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Convert Doc/Time labels.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
tic;
%  AdocTime0 = AdocTime;
  % Convert AdocTime DocID rows.
  [r c v] = find(AdocNodes(Row(AdocTime),1));
  AdocTime = AdocTime(r,':');
  AdocTime = putRow(AdocTime,v);
  clear('r','c','v');
disp(['Doc/time row time: ' num2str(toc)]); tic;
  % Convert AdocTime TimeID cols.
  [r c v] = find(AtimeNodes(Col(AdocTime),1));
  clear('AtimeNodes');
  AdocTime = putCol(AdocTime,v);
  clear('r','c','v');
disp(['Doc/time col time: ' num2str(toc)]);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Doc/Ent Links.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
tic;
  [eDocIDstr eEntIDstr eChunkIDstr eLenStr ePosStr eSourceStr] = ParseFile([dataDir 'doc_ent_links.csv'],',');
  disp(['Doc/ent links: ' num2str(NumStr(eEntIDstr))]);

  % Combine eChunkIDstr eLenStr ePosStr into 1 value.
  ePosFull = sscanf(eChunkIDstr,'%d,',inf) + (sscanf(ePosStr,'%d,',inf) ./ sscanf(eLenStr,'%d,',inf));
%  ePosFullStr = CatStr(CatStr(eChunkIDstr,'/',eLenStr),'/',ePosStr);
  clear('eChunkIDstr','eLenStr','ePosStr','eSourceStr');
disp(['doc_ent_links.csv parse time: ' num2str(toc)]); tic;
  AdocEnt = Assoc(eDocIDstr,eEntIDstr,ePosFull);
  clear('eDocIDstr','eEntIDstr','ePosFull');
disp(['Doc/ent assoc time: ' num2str(toc)]);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Ent Nodes.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
tic;
  [EntIDstr EntNameStr EntTypeStr] = ParseFile([dataDir 'ent_nodes.csv'],',');
  disp(['Ent nodes: ' num2str(NumStr(EntIDstr))]);

  % Combine EntNameStr EntTypeStr.
  EntFullStr = CatStr(EntTypeStr,'/',EntNameStr);
  clear('EntTypeStr','EntNameStr');
disp(['ent_nodes.csv parse time: ' num2str(toc)]); tic;
  AentNodes = Assoc(EntIDstr,1,EntFullStr);
  clear('EntIDstr','EntFullStr');
disp(['Ent nodes assoc time: ' num2str(toc)]);


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Convert Doc/Ent labels.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
tic;
  % Convert AdocEnt DocID rows.
%  AdocEnt0 = AdocEnt;
  [r c v] = find(AdocNodes(Row(AdocEnt),1));
  clear('AdocNodes');
  AdocEnt = AdocEnt(r,':');
  AdocEnt = putRow(AdocEnt,v);
  clear('r','c','v');
disp(['Doc/ent row time: ' num2str(toc)]); tic;
  % Convert AdocEnt EntID cols.
  [r c v] = find(AentNodes(Col(AdocEnt),1));
  clear('AentNodes');
  AdocEnt = putCol(AdocEnt,v);
  clear('r','c','v');
disp(['Doc/ent col time: ' num2str(toc)]);

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Combine Doc/Time and Doc/Ent.
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
tic;
  [rT cT vT] = find(AdocTime);  clear('AdocTime');
  [rE cE vE] = find(AdocEnt);   clear('AdocEnt');
  A = Assoc([rT rE],[cT cE],[vT;vE]);
  clear('rT','cT','vT','rE','cE','vE');
disp(['Doc/time + Doc/ent assoc time: ' num2str(toc)]);


% Save.
%save('ReutersEntity2','A');

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Mr. William Smith (william.smith@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
