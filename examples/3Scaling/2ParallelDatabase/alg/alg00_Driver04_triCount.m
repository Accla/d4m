DELETE_TABLE_TRIGGER = true;

javaMethod('setMagicInsert', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', true);
javaMethod('setMagicInsert2', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);


for SCALE=10; %10:20
for NUMTAB=2; %8:8:40

SPLITS_RATE_LINEAR = 0.70;
SPLITS_RATE_EXP = 1.1;

alg01_Gen00_Driver; 
alg04_triCount00_Driver

deleteForce(DB(tnameTmp));
deleteForce(TadjUU);
pause(3)

end
end