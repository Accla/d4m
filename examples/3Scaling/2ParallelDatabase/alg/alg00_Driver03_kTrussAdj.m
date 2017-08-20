DELETE_TABLE_TRIGGER = true;

SPLITS_RATE_LINEAR = 0.75;
SPLITS_RATE_EXP = 1.2;
javaMethod('setIntEncodeValueAndDropEmpty', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);
javaMethod('setIntEncodeKeyAndPrependLastByteRev', 'edu.mit.ll.d4m.db.cloud.D4mDbInsert', false);


for SCALE=12; %10:15
alg01_Gen00_Driver; 
for NUMTAB=2; %8:8:40
  alg03_kTrussAdj00_Driver
  pause(5)
end
deleteForce(TadjUU);
pause(5)
end
