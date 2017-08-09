function command = MyEcho(command)
%MyEcho: Echo back the given command. Use $ for quoting strings. Useful for demos.
%Associative array utility function internal.
%  Usage:
%    command = MyEcho(command)
%  Inputs:
%    command = set of commands to echo
%  Outputs:
%    command = same as input
%  Example:
%    eval(MyEcho('A($Bob,Alice,$,:);   % Show rows Bob and Alice.'));

  Q = '''';
  command(command == '$') = Q;
  disp(command);
end
