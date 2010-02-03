#! /usr/bin/env python
#
# Hide the magic of starting the proxy, and getting the port it is running on.
#
#

# required for creating an auth value
import os, sys

# hunt around for thrift
home = os.environ['HOME']
try:
    import thrift
except ImportError:
    for location in [home, os.path.join(home, 'local'), '/usr', '/usr/local']:
        for lib in 'lib', 'lib64':
            path = os.path.join(location, os.path.join(lib, 'python2.4/site-packages'))
            orig = sys.path
            sys.path = [path] + orig
            try:
                try:
                    import thrift
                    break
                except ImportError:
                    continue
            finally:
                sys.path = orig
        else:
            continue
        break

# Get the generated API's
CLOUDBASE_HOME=os.environ['CLOUDBASE_HOME']
path = os.path.join(CLOUDBASE_HOME, 'gen-py')
if not os.path.isdir(path):
    path = os.path.join(CLOUDBASE_HOME, 'cloudbase-core', 'target', 'gen-py')
sys.path.append(path)

import clientproxy.ClientProxy
from data.ttypes import AuthInfo

# establish a close method on the client interface
class CloseMixin:
    def close(self):
       self._iprot.trans.close()
class ClientProxy(clientproxy.ClientProxy.Client, CloseMixin):pass

# kill any proxies when this process exits
child_processes = []
def kill():
    import signal
    for job in child_processes:
        os.kill(job.pid, signal.SIGKILL)
import atexit
atexit.register(kill)


# Start and get a client connection to the proxy
def connect():
    "Returns client (for making calls, and transport, for use in select()"
    import socket, errno, time, subprocess, re, select, fcntl
    from thrift import Thrift
    from thrift.transport import TSocket
    from thrift.transport import TTransport
    from thrift.protocol import TBinaryProtocol
    # fire up the ClientProxy class with cloudbase.sh
    import glob
    command = (os.path.join(CLOUDBASE_HOME, 'bin', 'cloudbase.sh'),
               'cloudbase.core.client.proxy.ClientProxy')

    # read stdout until we see the text containing the local port number
    proc = subprocess.Popen(command, stdout=subprocess.PIPE)
    fcntl.fcntl(proc.stdout, fcntl.F_SETFL, os.O_NDELAY)
    child_processes.append(proc)
    stopWaiting = time.time() + 10
    while time.time() < stopWaiting:
        rd, wr, ex = select.select([proc.stdout], [], [], stopWaiting - time.time())
        if rd:
           data = proc.stdout.read()
           match = re.search("Serving on port \\[([0-9]+)\\]", data)
           if match:
               # got it, go ahead and connect to it
               port = int(match.group(1))
               transport = TSocket.TSocket('localhost', port)
               transport = TTransport.TBufferedTransport(transport)
               protocol = TBinaryProtocol.TBinaryProtocol(transport)
               client = ClientProxy(protocol)
               transport.open()
               print "connected to proxy"
               return client, transport
    raise Exception("Unable to connect to proxy")

# return the client object
def client():
    c, t = connect()
    # add the simplified iterator-based scan interface to the clien
    from scan import scan
    c.scan = lambda *args: scan(c, *args)
    return c

# give the user a command-line prompt if they are running this module
import code
import readline
class HistoryConsole(code.InteractiveConsole):
    def __init__(self, 
                 locals=None, 
                 histfile=os.path.expanduser("~/.cbhistory")):
        code.InteractiveConsole.__init__(self, locals)
        self.init_history(histfile)

    def init_history(self, histfile):
        readline.parse_and_bind("tab: complete")
        if hasattr(readline, "read_history_file"):
            try:
                readline.read_history_file(histfile)
            except IOError:
                pass
            atexit.register(self.save_history, histfile)

    def save_history(self, histfile):
        readline.write_history_file(histfile)

if __name__ == '__main__':
    console = HistoryConsole(globals())
    banner = '''Welcome to the Cloudbase interactive python shell!'''
    console.interact(banner)
