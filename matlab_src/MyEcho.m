function command = MyEcho(command)
% Echo back the given command. Use $ for quoting strings.
  Q = '''';
  command(command == '$') = Q;
  disp(command);
end
