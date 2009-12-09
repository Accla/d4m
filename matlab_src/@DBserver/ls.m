function [tableValueStr] = ls(DB)
%LS lists tables in a DB.

  import java.util.*;
  import ll.mit.edu.d4m.db.cloud.*;

  info = D4mDbInfo(DB.host);
  tableValueStr = char(info.getTableList());

end
