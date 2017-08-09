function display(obj)

t = struct(obj);
db = struct(t.DB);
disp('DBTable object');
disp(['Type : ' db.type]);
disp(['Schema : ' t.name]);


