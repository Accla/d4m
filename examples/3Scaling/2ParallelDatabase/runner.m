
DoGenData = true;
DoDeleteDB = true;
DoPutDB = true;
DoTableMult = true;
%%
DBsetup
if DoGenData
    pDB02_FileTEST
    pDB03_AssocTEST
end
if DoDeleteDB
    deleteForce(Tadj); 
    deleteForce(TadjDeg); 
    deleteForce(Tedge); 
    deleteForce(TedgeDeg);
end
if DoPutDB
    pDB05_SetupTEST
    pDB06_AdjInsertTEST
end
if DoTableMult
    pDB20_AdjMultTEST 
end

