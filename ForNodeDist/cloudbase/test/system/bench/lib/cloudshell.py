import subprocess

from lib import path
from lib import runner
from lib.options import log

    
def run(username, password, input):
    "Run a command in cloudbase.sh"
    handle = runner.start([path.cloudbase('bin', 'cloudbase.sh'), 'shell %s' % username],
                          stdin=subprocess.PIPE)
    log.debug("Running: %r", input)
    out, err = handle.communicate(password + '\n' + input)
    log.debug("Process finished: %d (%s)",
              handle.returncode,
              ' '.join(handle.command))
    return handle.returncode, out, err
