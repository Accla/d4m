function T = subsref(DB, s)
%(),subsref: Create DBtable object binding to a specific database table; creates tables if they don't exist.
%Database user function.
%  Usage:
%    T = DB(table)
%    T = DB(table,tableTranspose)
%  Inputs:
%    DB = database object with a binding to a specific database
%    table = name of table in database
%    tableTranspose = name of table transpose in database
% Outputs:
%    T = database table or table pair object

subs = s.subs;

if (numel(subs) == 1)
    table = subs{1};
    % Check if table is in DB.
    if strcmp(DB.type,'BigTableLike') || strcmp(DB.type,'Accumulo')
        if isempty( StrSubsref(ls(DB),[table ' ']) )
            disp(['Creating ' table ' in ' DB.host ' ' DB.type]);
            DBcreate(DB.instanceName,DB.host,table,DB.user,DB.pass,DB.type);  % Create table.
        end
    end
    if strcmp(DB.type,'sqlserver')
        if (strcmpi(table(1:7),'select ')) % changed strcmpi(lower()) to strcmpi() - sid
            disp('Binding to query.');
        elseif isempty( strfind(ls(DB),[table ',']) )
            disp([table ' not in ' DB.host ' ' DB.type]);
        end
    end
    if strcmp(DB.type,'scidb')
        % NOTE: ls(DB) does not work reliably when list of tables
        % is large.         
        [tableName, tableSchema] = SplitSciDBstr(table);
        %{
        [~, tableList] = ls(DB);
    
        % consider table: {0} 'm2',2,2,'m2<grayval:uint8> [i=0:*,1000,10]',true,false
        pattern1 = ',''\S.*\[\S.*\]'''; % matches : ,'m2<grayval:uint8> [i=0:*,1000,10]'
        pattern2 = '<\S*>\s*[\S*\]'; % matches : <grayval:uint8> [i=0:*,1000,10]
        pattern3 = '''\S*<'; % matches: 'm2<

        namesAndSchemas = cellfun(@(INPUT1) regexp(INPUT1, pattern1, 'match', 'freespacing', 'once'), tableList, 'UniformOutput', false);        
        schemas = cellfun(@(INPUT2) regexp(INPUT2, pattern2, 'match', 'freespacing', 'once'), tableList, 'UniformOutput', false);        
        names = cellfun(@(INPUT3) regexp(INPUT3, pattern3, 'match', 'freespacing', 'once'), namesAndSchemas, 'UniformOutput', false);
        names = cellfun(@(INPUT4) INPUT4(2:end-1), names, 'UniformOutput', false);
        
        matchID = cellfun(@(INPUT5) isequal(INPUT5, strtrim(table)), names);
        if nnz(matchID)
        %}
        schemaFound = getTable(DB, tableName);
        if ~isempty(schemaFound)
            disp(['Binding to table: ' table]);
            %table = [names{matchID} schemas{matchID}];
            table = [tableName schemaFound];
        else
            if isempty(tableSchema);
                %disp('Binding to Table - need schema to create SciDB table.');
                % error out because we cannot create a table without a schema
                error('Binding to Table - need schema to create SciDB table.');
            else
                disp(['Creating ' table ' in ' DB.host ' ' DB.type]);
                urlport = DB.host;

                cmd = sprintf('wget -q -O - "%snew_session" --http-user=%s --http-password=%s', ...
                    urlport, DB.user, DB.pass);
                [stat, sessionID] = system(cmd);
                sessionID = deblank(sessionID);
                
                if stat>0
                    error('Unable to establish new SciDB session');
                end
                
                % using sprintf so it's easier to see the command being created
                cmd = sprintf('wget -q -O - "%sexecute_query?id=%s&query=create array %s %s&release=1" --http-user=%s --http-password=%s', ...
                    urlport, sessionID, tableName, tableSchema, DB.user, DB.pass);
                
                [stat, queryID] = system(cmd);
                if stat>0
                    error('Unable to create new table. Server response :\n%s\n', queryID);
                end

            end
        end
    end
    T = DBtable(DB,table);
end

if (numel(subs) == 2)
    table1 = subs{1};
    % Check if tables is in DB.
    if isempty( StrSubsref(ls(DB),[table1 ' ']) )
        disp(['Creating ' table1 ' in ' DB.host ' ' DB.type]);
        DBcreate(DB.instanceName,DB.host,table1,DB.user,DB.pass, DB.type);  % Create table.
    end
    table2 = subs{2};
    % Check if tables is in DB.
    if isempty( StrSubsref(ls(DB),[table2 ' ']) )
        disp(['Creating ' table2 ' in ' DB.host ' ' DB.type]);
        DBcreate(DB.instanceName,DB.host,table2,DB.user,DB.pass, DB.type);  % Create table.
    end
    T = DBtablePair(DB,table1,table2);
end

end

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% D4M: Dynamic Distributed Dimensional Data Model
% Architect: Dr. Jeremy Kepner (kepner@ll.mit.edu)
% Software Engineer: Dr. Jeremy Kepner (kepner@ll.mit.edu), Dr. Vijay
% Gadepally (vijayg@ll.mit.edu), Dr. Siddharth Samsi (sid@ll.mit.edu)
% MIT Lincoln Laboratory
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% (c) <2010> Massachusetts Institute of Technology
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
