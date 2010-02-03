import unittest
import time

from lib import cloudshell
from lib.Benchmark import Benchmark
from lib.slaves import runAll
from lib.path import cloudbase

class CloudStone1(Benchmark):

    def shortDescription(self):
        return 'Test the speed at which we can check that cloudbase is up '\
               'and we can reach all the slaves. Lower is better.'

    def runTest(self):
        code, out, err = cloudshell.run(self.username, self.password, 'table !METADATA\nscan\n')
        results = runAll('echo help | %s shell' %
                         cloudbase('bin', 'cloudbase.sh'))
                         
    def setSpeed(self, speed):
        "We want to override this method but no speed can be set"

def suite():
    result = unittest.TestSuite([
        CloudStone1(),
        ])
    return result
