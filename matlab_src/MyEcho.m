function command = MyEcho(command)
% Echo back the given command
  Q = '''';
  command(command == '$') = Q;
  disp(command);
end
