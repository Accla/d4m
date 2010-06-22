function docList = D4MqueryTrackPointDocList(x,t,l);
% Returns docs that support a particular track point.
  global D4MqueryGlobal

  DbA = D4MqueryGlobal.DbA;

  % Find docs that have person
  DocIDwPer = Row(DbA(:,x));

  Ax = DbA(DocIDwPer,:);

  % Find docs that have person and location.
  DocIDwPerLoc = Row(Ax(DocIDwPer,l));

  % Find docs that have person, location and time.
  DocIDwPerLocTime = Row(Ax(DocIDwPerLoc,t));

  docList = DocIDwPerLocTime;

end

