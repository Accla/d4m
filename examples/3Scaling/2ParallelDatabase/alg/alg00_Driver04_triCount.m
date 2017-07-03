DELETE_TABLE_TRIGGER = true;

SPLITS_RATE_LINEAR = 0.75;
SPLITS_RATE_EXP = 1.2;
javaMethod('setMagicInsert', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', true);
javaMethod('setMagicInsert2', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);


for SCALE=12; %10:20
for NUMTAB=6; %8:8:40

alg01_Gen00_Driver; 
alg04_triCount00_Driver

deleteForce(DB(tnameTmp));
deleteForce(TadjUU);
pause(5)

end
end