from subprocess import Popen as BasePopen, PIPE
import os
import time
import logging
import unittest
import sys
import socket
import signal

import sleep

CLOUDBASE_HOME = os.path.dirname(__file__)
CLOUDBASE_HOME = os.path.join(CLOUDBASE_HOME, *(os.path.pardir,)*3)
CLOUDBASE_HOME = os.path.realpath(CLOUDBASE_HOME)
CLOUDBASE_DIR = "/cloudbase-test"

log = logging.getLogger('test.auto')

ROOT = 'root'
ROOT_PASSWORD = 'secret'

class Popen(BasePopen):
   def __init__(self, cmd, **args):
      self.cmd = cmd
      BasePopen.__init__(self, cmd, **args)

class TestUtilsMixin:
    "Define lots of utilities to run cloudbase utilities"
    hosts = ()                          # machines to run cloudbase

    settings = {'cloudbase.tabletserver.portSearch': 'true'
                }
    tableSettings = {}

    def masterHost(self):
        return self.hosts[0]

    def runOn(self, host, cmd, stderr=PIPE, **opts):
        log.debug('%s: %s', host, ' '.join(cmd))
        opts.update(dict(stderr=stderr))
        if host == 'localhost':
            os.environ['SERVER_JAR'] = self.options.server_jar
            os.environ['CLIENT_JAR'] = self.options.client_jar
            return Popen(cmd, **opts)
        else:
            cp = 'HADOOP_CLASSPATH=%s' % os.environ.get('HADOOP_CLASSPATH','')
            sj = 'SERVER_JAR=%s' % self.options.server_jar
            cj = 'CLIENT_JAR=%s' % self.options.client_jar
            return Popen(['ssh', host, cp, sj, cj] + cmd, **opts)
            
    def shell(self, host, input, **opts):
        """Run cloudbase.sh shell with the given input,
        return the exit code, stdout and stderr"""
        input = ROOT_PASSWORD + "\n" + input
	log.debug("Running shell with %r", input)
        handle = self.runOn(host, [self.cloudbase_sh(), 'shell', 'root'],
                            stdin=PIPE, stdout=PIPE, **opts)
        out, err = handle.communicate(input)
        return handle.returncode, out, err

    def cloudbase_sh(self):
        "Determine the location of cloudbase.sh"
        result = os.path.join(CLOUDBASE_HOME, 'scripts', 'cloudbase.sh')
        if not os.path.exists(result):
            result = os.path.join(CLOUDBASE_HOME, 'bin', 'cloudbase.sh')
        return result

    def start_master(self, host, safeMode=None):
        cmd = [self.cloudbase_sh(), 'master']
        if safeMode:
            cmd.append('safeMode')
        return self.runOn(host, cmd)

    def processResult(self, out, err, code):
        if out:
            log.debug("Output from command: %s", out.rstrip())
        if err:
            if err.find('org.apache.hadoop.util.ToolRunner') > 0:
                log.error("This looks like a stack trace: %s", err)
                return False
            else:
                log.info("Error output from command: %s", err.rstrip())
        log.debug("Exit code: %s", code)
        return code == 0
        

    def wait(self, handle):
        out, err = handle.communicate()
        return self.processResult(out, err, handle.returncode)


    def pkill(self, host, pattern, signal=signal.SIGKILL):
        cmd= ['pkill', '-%d' % signal, '-U', str(os.getuid()), '-f', pattern]
        if sys.platform == 'darwin':
	    cmd = ['sh', '-c',
               "ps -eo pid,command | "
               "grep '%s' | grep -v grep | "
               "awk '{ print $1}' | "
               "xargs kill -%d " % (pattern, signal)]
        handle = self.runOn(host, cmd)
        handle.communicate()

    def cleanupCloudbaseHandles(self, secs=2):
        handles = []
        for h in self.cloudbaseHandles:
            if not self.isStopped(h, secs):
                handles.append(h)
        self.cloudbaseHandles = handles

    def stop_master(self, host):
        self.pkill(host, 'java.*master$', signal=signal.SIGHUP)
        self.cleanupCloudbaseHandles()

    def stop_monitor(self, host):
        self.pkill(host, 'java.*monitor$', signal=signal.SIGHUP)
        self.cleanupCloudbaseHandles()

    def start_tserver(self, host):
        return self.runOn(host,
                          [self.cloudbase_sh(), 'tserver'],
                          stdout=None,
                          stderr=PIPE)

    def start_monitor(self, host):
        return self.runOn(host,
                          [self.cloudbase_sh(), 'monitor'],
                          stdout=None,
                          stderr=PIPE)

    def stop_tserver(self, host):
        self.pkill(host, 'java.*tserver$', signal=signal.SIGHUP)
        # wait for it to stop
        self.sleep(5)
        self.cleanupCloudbaseHandles(0.5)

    def runJarOn(self, host, klass, args, **kwargs):
        "Invoke a the given class in the cloudbase.jar file"
        jar = self.options.server_jar
        return self.runOn(host,
                          [self.cloudbase_sh(), klass] + args,
                          **kwargs)

    def ingest(self, host, count, start=0, timestamp=None, size=50, **kwargs):
        klass = 'cloudbase.core.test.TestIngest'
        args = ''
        if timestamp:
            args += "-timestamp %ld " % int(timestamp)
        args += '-tsbw -size %d -random 56 %d %d 1 ' % (size, count, start)
        return self.runJarOn(host, klass, args.split(), stdout=PIPE, **kwargs)

    def verify(self, host, count, start=0, size=50, timestamp=None):
        klass = 'cloudbase.core.test.VerifyIngest'
        args = ''
        if timestamp:
            args += "-timestamp %ld " % int(timestamp)
        args += '-size %d -random 56 %d %d 1' % (size, count, start)
        return self.runJarOn(host, klass, args.split(),
                             stdout=PIPE,
                             stderr=None)

    def stop_cloudbase(self):
        for host in self.hosts:
            self.pkill(host, 'cloudbase.start')

    def clean_cloudbase(self, host):
        self.stop_cloudbase()
        fp = open(os.path.join(CLOUDBASE_HOME, 'conf', 'masters'),'wb')
        fp.write(self.masterHost() + '\n')
        fp.close()

        fp = open(os.path.join(CLOUDBASE_HOME, 'conf', 'cloudbase-site.xml'),
                  'w')
        fp.write('<configuration>\n')
        settings = self.settings.copy()
        settings.update({'cloudbase.zookeeper.host':socket.getfqdn(),
                         'cloudbase.directory':CLOUDBASE_DIR,
                         'cloudbase.tabletserver.clientPort':49997,
                         'cloudbase.tabletserver.masterPort':49998,
                         'cloudbase.master.clientPort':49999,
                         'cloudbase.master.tabletPort':41223,
                         'cloudbase.monitor.port':50099,
                         'cloudbase.tablet.walog.directory':
                         os.path.join(CLOUDBASE_HOME, 'logs'),
			 'cloudbase.master.tabletserver.maxFutureDelta': 15,
                         })
        for a, v in settings.items():
            fp.write('  <property>\n')
            fp.write('    <name>%s</name>\n' % a)
            fp.write('    <value>%s</value>\n' % v)
            fp.write('  </property>\n')
        fp.write('</configuration>\n')
        fp.close()
        os.system('rm -rf %s/logs/*.log' % CLOUDBASE_HOME)

        self.wait(self.runOn(host,
                             ['hadoop', 'fs', '-rmr', CLOUDBASE_DIR],
                             stdout=PIPE))
        handle = self.runOn(host, [self.cloudbase_sh(), 'init'],
                            stdin=PIPE,
                            stdout=PIPE)
        out, err = handle.communicate(ROOT_PASSWORD + "\n")
        self.processResult(out, err, handle.returncode)

    def start_cloudbase(self, safeMode=None):
        self.cloudbaseHandles = [
           self.start_master(self.masterHost(), safeMode)
           ] + [
           self.start_tserver(host) for host in self.hosts
           ] + [
           self.start_monitor(self.masterHost())
           ]
        self.sleep(5)    
        settings = []
        for table, values in self.tableSettings.items():
            for k, v in values.items():
                settings.append('config -t %s %s=%s\n' % (table, k, v))
	if settings:
            self.processResult(*self.shell(self.masterHost(), ''.join(settings)))


    def rootShell(self, host, cmd, **opts):
        return self.shell(host, cmd, **opts)

    def flush(self, tablename):
        code, out, err = self.rootShell(self.masterHost(),
                                        "flush %s\n" % tablename)
	assert code == 0

    def isStopped(self, handle, secs):
        stop = time.time() + secs * sleep.scale
        
        while time.time() < stop:
            time.sleep(0.1)
            try:
                code = handle.poll()
                if code is not None:
                    out, err = '', ''
                    try:
                        out, err = handle.communicate()
                    except Exception:
                        pass
                    return True
            except OSError, ex:
                if ex.args[0] != errno.ECHILD:
                    raise
        return False

    def waitForStop(self, handle, secs):
	log.debug('Waiting for %s to stop in %s secs',
                  ' '.join(handle.cmd),
                  secs)
        stop = time.time() + secs * sleep.scale
        
        while time.time() < stop:
            time.sleep(0.1)
            try:
                code = handle.poll()
                if code is not None:
                    out, err = '', ''
                    try:
                        out, err = handle.communicate()
                    except Exception:
                        pass
                    self.assert_(
                       self.processResult(out, err, handle.returncode)
                       )
                    return
            except OSError, ex:
                if ex.args[0] != errno.ECHILD:
                    raise
        self.fail("Process failed to finish in %s seconds" % secs)

    def shutdown_cloudbase(self):
        handle = self.runOn(self.masterHost(),
                            [self.cloudbase_sh(), 'admin', 'stopAll'],
                            stdout=PIPE)
        self.waitForStop(handle, 100)
        self.stop_monitor(self.masterHost())
        self.cleanupCloudbaseHandles()
        # give everyone a couple seconds to completely stop
        for h in self.cloudbaseHandles:
            self.waitForStop(h, 60)

    def sleep(self, secs):
        log.debug("Sleeping %f seconds" % secs)
        sleep.sleep(secs)

    def setUp(self):
        self.hosts = self.options.hosts
        self.clean_cloudbase(self.masterHost())
        self.start_cloudbase()

        # ensure we can read the METADATA tablet before continuing
        while True:
            code, out, err = self.rootShell(self.masterHost(),
                                            'table !METADATA\nscan\n')
            if code == 0:
                break
            self.sleep(1)

    def tearDown(self):
       self.stop_cloudbase()
       os.unlink(os.path.join(CLOUDBASE_HOME, 'conf', 'cloudbase-site.xml'))
       self.wait(self.runOn(self.masterHost(),
                            ['hadoop', 'fs', '-rmr', CLOUDBASE_DIR],
                            stdout=PIPE))

    def createTable(self, table):
        code, out, err = self.rootShell(self.masterHost(),
                                        "createtable %s\n" % table)
        self.processResult(out, err, code)
        self.sleep(1)
