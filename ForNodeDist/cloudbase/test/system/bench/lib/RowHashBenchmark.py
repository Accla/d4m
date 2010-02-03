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

class RowHashBenchmark(Benchmark):
    "RowHashing Benchmark"

    keymin = 10
    keymax = 10
    valmin = 80
    valmax = 80
    rows = 1000000
    maxmaps = 60
    hadoop_version = ''
    input_table = 'RowHashTestInput'
    output_table = 'RowHashTestOutput'

    def setUp(self): 
        random.jumpahead(int(time.time()))
        num = random.randint(1, 100000)
        self.input_table = self.input_table + "-" + str(num) 
        self.output_table = self.output_table + "-" + str(num)    
        #if (not os.getenv("HADOOP_CLASSPATH")):
        #    os.putenv("HADOOP_CLASSPATH", self.getjars(":"))
        dir = os.path.dirname(os.path.realpath(__file__))
        file = os.path.join( dir, 'splits' )  
        # code, out, err = cloudshell.run(self.username, self.password, 'table RowHashTestInput\n') 
        # if out.find('no such table') == -1:
        #    code, out, err = cloudshell.run(self.username, self.password, 'deletetable RowHashTestInput\n') 
        #    self.sleep(15)
        code, out, err = cloudshell.run(self.username, self.password, "createtable %s SPLITS %s\n" % (self.input_table, file))
        #code, out, err = cloudshell.run('table RowHashTest\n') 
        #if out.find('no such table') == -1:
        #    code, out, err = cloudshell.run('setuser root\nsecret\ndeletetable RowHashTest\n') 
        #    self.sleep(15)
        code, out, err = cloudshell.run(self.username, self.password, "createtable %s SPLITS %s\n" % (self.output_table, file))
        hadoop, command = self.buildcommand('cloudbase.examples.mapreduce.TeraSortIngest')        
        args = ' %d %d %d %d %d %s %s %s %s %d\n' % (self.numrows(),
                                                self.keysizemin(),
                                                self.keysizemax(),
                                                self.minvaluesize(),
                                                self.maxvaluesize(),
                                                self.input_table, 
                                                self.getInstance(),
                                                self.getUsername(),
                                                self.getPassword(),
                                                400)
        command = command + args
        command = command.split()
        handle = runner.start([hadoop] + command,
                          stdin=subprocess.PIPE)
        log.debug("Running: %r", command)
        out, err = handle.communicate("")  
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
        hadoop, command = self.buildcommand('cloudbase.examples.mapreduce.RowHash')        
        args = ' %s %s %s %s %s %s %d' % (
            self.getInstance(),
            self.getUsername(),
            self.getPassword(),
            self.input_table,
            'column',
            self.output_table,
            self.maxmaps)
        command = command + args
        command = command.split()
        handle = runner.start([hadoop] + command, stdin=subprocess.PIPE)        
        log.debug("Running: %r", command)
        out, err = handle.communicate("")
        log.debug("Process finished: %d (%s)", handle.returncode, ' '.join(handle.command))
        return handle.returncode, out, err
    
    def shortDescription(self):
        return 'Hashes %d rows from one CB and outputs them into another Table. '\
               'Lower score is better.' % (self.numrows())
               
    def setSpeed(self, speed):
        if speed == "slow":
            self.rows = 1000000
            self.maxmaps = 400
        elif speed == "medium":
            self.rows = 100000
            self.maxmaps = 300
        elif speed == "fast":
            self.rows = 10000
            self.maxmaps = 200
            
    def needsAuthentication(self):
        return 1
