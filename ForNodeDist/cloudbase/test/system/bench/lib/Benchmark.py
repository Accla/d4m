import time
import unittest
import os
import glob

from options import log

from path import cloudbase

class Benchmark(unittest.TestCase):
    
    instance = ''
    username = ''
    password = ''

    def __init__(self):
        unittest.TestCase.__init__(self)
        self.finished = None

    def name(self):
        return self.__class__.__name__

    def setUp(self):
        # verify cloudbase is running
        self.start = time.time()

    def tearDown(self):
        self.stop = time.time()
        log.debug("Runtime: %.2f", self.stop - self.start)
        self.finished = True

    def runTime(self):
        return self.stop - self.start

    def score(self):
        if self.finished:
            return self.runTime()
        return 0.

    # Each class that extends Benchmark should overwrite this
    def setSpeed(self, speed):
        print "Classes that extend Benchmark need to override setSpeed."
        

    def setUsername(self, user):
        self.username = user

    def setPassword(self, password):
        self.password = password
        
    def setInstance(self, instance):
        self.instance = instance
        
    def getInstance(self):
        return self.instance
        
    def getUsername(self):
        return self.username
        
    def getPassword(self):
        return self.password
        
    def sleep(self, tts):
        time.sleep(tts)
        
    def needsAuthentication(self):
        return 0
    
    # Returns the location of the local examples jar
    def getexamplejar(self):
        cloudbase_example_glob = glob.glob(cloudbase() + '/lib/*examples*.jar')
        cloudbase_example_jar = cloudbase_example_glob[0]
        return cloudbase_example_jar
    
    # Returns a string of core, thrift and zookeeper jars with a specified delim
    def getjars(self, delim):
        cloudbase_core_glob = glob.glob(cloudbase() + '/lib/*core*.jar')
        for j in cloudbase_core_glob:
            if j.find('javadoc') >= 0:
                cloudbase_core_glob.remove(j)
        cloudbase_core_jar = cloudbase_core_glob[0]
        cloudbase_thrift_glob = glob.glob(cloudbase() + '/lib/*thrift*.jar')
        for j in cloudbase_thrift_glob:
            if j.find('javadoc') >= 0:
                cloudbase_thrift_glob.remove(j)
        cloudbase_thrift_jar = cloudbase_thrift_glob[0]
        cloudbase_zookeeper_glob = glob.glob(os.path.join(os.getenv('ZOOKEEPER_HOME'), 'zookeeper*.jar'))
        for j in cloudbase_zookeeper_glob:
            if j.find('javadoc') >= 0:
                cloudbase_zookeeper_glob.remove(j)
        cloudbase_zookeeper_jar = cloudbase_zookeeper_glob[0]
        toret = '%s%s%s%s%s' % (cloudbase_core_jar, delim, cloudbase_thrift_jar, delim, cloudbase_zookeeper_jar)
        return toret
       
    # Builds the running command for the map/reduce class specified sans the arguments
    def buildcommand(self, classname):
        command = ''        
        hadoop_path = os.getenv('HADOOP_HOME')
        loc = hadoop_path.split('/')
        hadoop_version = loc[-1:][0]        
        libjars = '-libjars ' + self.getjars(",")
        myJar = self.getexamplejar()               
        myClass = classname
        if hadoop_version == 'hadoop-0.19.1':
            command = 'jar %s %s %s' % (libjars, myJar, myClass) 
        elif hadoop_version == 'hadoop-0.20.0':
            command = 'jar %s %s %s' % (myJar, myClass, libjars)
        # command = command + '%d %d %d %d %d %s %s %s %s\n' % args
        hadoop = hadoop_path + '/bin/hadoop'
        return hadoop, command
    
