function enumobj = JavaInnerEnum(topobj, icname, enumname)
% Returns an enum object buried within an inner enum member of a class.
% Params:
%   topobj is a Java object for the parent class holding the enum. We get a class loader from it. 
%   icname is the name of the inner enum class
%   enumname is the name of the particular enum object
% Examples: 
%   JavaInnerEnum(edu.mit.ll.graphulo.reducer.GatherReducer(), 'KeyPart', 'ROW')
%   JavaInnerEnum(edu.mit.ll.graphulo.simplemult.MathTwoScalar(), 'ScalarType', 'LONG')


aclass = topobj.getClass();
aclassname = char(aclass.getName());
classloader = aclass.getClassLoader();
ic = java.lang.Class.forName([aclassname '$' icname], true, classloader);

classArray = javaArray('java.lang.Class',1);
classArray(1) = java.lang.String().getClass();
stringArray = javaArray('java.lang.String',1);
stringArray(1) = java.lang.String(enumname);
enumobj = ic.getMethod(java.lang.String('valueOf'),classArray).invoke([],stringArray);

end