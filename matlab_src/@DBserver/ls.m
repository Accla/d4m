function [tableValueStr] = ls(DB)
%LS lists tables in a DB.

if exist('OCTAVE_VERSION','builtin')
  info=java_new('ll.mit.edu.d4m.db.cloud.D4mDbInfo',DB.host);
  tableValueStr = char(info.getTableList());
else
  import java.util.*;
  import ll.mit.edu.d4m.db.cloud.*;

  info = D4mDbInfo(DB.host);
  tableValueStr = char(info.getTableList());
end

end
