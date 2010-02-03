import unittest
import subprocess
import os
import glob
import random
import time

from lib import cloudshell, runner, path
from lib.Benchmark import Benchmark
from lib.slaves import runEach, slaveNames
from lib.path import cloudbase, cloudbaseJar
from lib.util import sleep
from lib.options import log

class TeraSortBenchmark(Benchmark):
    "TeraSort in the cloud"

    keymin = 10
    keymax = 10
    valmin = 78
    valmax = 78
    rows = 10000000000
    numsplits = 400
    # Change this number to modify how the jobs are run on hadoop
    rows_per_split = 250000
    hadoop_version = ''
    tablename = 'CloudIngestTest'


    def setUp(self): 
        random.jumpahead(int(time.time()))
        num = random.randint(1, 100000)   
        self.tablename = self.tablename + "-" + str(num)  
        # Find which hadoop version
        # code, out, err = cloudshell.run(self.username, self.password, 'table %s\n' % self.tablename)
        #if out.find('no such table') == -1:
        #    log.debug('Deleting table %s' % self.tablename)
        #    code, out, err = cloudshell.run(self.username, self.password, 'deletetable %s\n' % self.tablename)
        #    self.sleep(10)
        Benchmark.setUp(self)
        
    def keysizemin(self):
        return self.keymin

    def keysizemax(self):
        return self.keymax

    def numrows(self):
        return self.rows

    def minvaluesize(self):
        return self.valmin

    def maxvaluesize(self):
        return self.valmax
        
    def runTest(self):        
        dir = os.path.dirname(os.path.realpath(__file__))
        file = os.path.join( dir, 'splits' )
        code, out, err = cloudshell.run(self.username, self.password, "createtable %s SPLITS %s\n" % (self.tablename, file))
        self.sleep(60)
        args = ' %d %d %d %d %d %s %s %s %s %d' % (self.numrows(),
                                                   self.keysizemin(),
                                                   self.keysizemax(),
                                                   self.minvaluesize(),
                                                   self.maxvaluesize(),
                                                   self.tablename,
                                                   self.instance,
                                                   self.username,
                                                   self.password, 
                                                   self.numsplits)
        hadoop, command = self.buildcommand('cloudbase.examples.mapreduce.TeraSortIngest')
        command = command + args
        command = command.split()
        handle = runner.start([hadoop] + command, stdin=subprocess.PIPE)
        log.debug("Running: %r", command)
        out, err = handle.communicate("")
        log.debug("Process finished: %d (%s)", handle.returncode, ' '.join(handle.command))
        return handle.returncode, out, err
        
    def needsAuthentication(self):
        return 1
    
    def shortDescription(self):
        return 'Ingests %d rows (to be sorted). '\
               'Lower score is better.' % (self.numrows())
               
    def setSpeed(self, speed):
        if speed == "slow":
            self.rows = 10000000000            
        elif speed == "medium":
            self.rows = 10000000
        elif speed == "fast":
            self.rows = 10000 
        self.numsplits = 400