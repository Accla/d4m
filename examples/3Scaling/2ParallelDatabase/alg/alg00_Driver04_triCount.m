DELETE_TABLE_TRIGGER = true;

javaMethod('setMagicInsert', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', true);
javaMethod('setMagicInsert2', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);


for SCALE=15; %10:20
for NUMTAB=4; %8:8:40

SPLITS_RATE_LINEAR = 0.72;
SPLITS_RATE_EXP = 1.35;
SPLITS_RATE_EXP_INV = 0.88;

alg01_Gen00_Driver; 
alg04_triCount00_Driver

deleteForce(DB(tnameTmp));
deleteForce(TadjUU);
pause(3)

end
end