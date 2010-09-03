function queryJSONCSV = D4MwebQueryResponse(queryJSONCSV)

  J = parseJSON(queryJSONCSV);

  Aq = JSONCSV2assoc(queryJSONCSV);

  qName = Val(Aq('QueryName,','Col1,'));
  qName = qName(1:end-1);

  if strcmp(qName,'GetTrackNames')

    MinLength = Val(Aq('MinLength,','Col1,'));
    MinLength = str2num(MinLength(1:end-1));
    Ar = D4MqueryGetTrackNames(MinLength);

  elseif strcmp(qName,'MHtrack')

    EntityName = Val(Aq('EntityName,','Col1,'));    
    Ar = D4MqueryMHtrack(EntityName);

  elseif strcmp(qName,'TrackPointDocList')

    EntityName = Val(Aq('EntityName,','Col1,'));
    EntityTime = Val(Aq('EntityTime,','Col1,'));
    EntityLocation = Val(Aq('EntityLocation,','Col1,'));
    Ar = D4MqueryTrackPointDocList(EntityName, EntityTime, EntityLocation);

  elseif strcmp(qName,'TrackPointDocList')

    EntityTime = Val(Aq('EntityTime,','Col1,'));
    EntityLocation = Val(Aq('EntityLocation,','Col1,'));
    Ar = D4MqueryTrackWindow(EntityTime,EntityLocation);

  end


  queryJSONCSV = Assoc2JSONCSV(Ar,J.rowSeparator,J.columnSeparator,'QueryResponse');
  %queryJSONCSV = Ar;

end

