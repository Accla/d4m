import os
import logging
import unittest
import time
import sleep
from subprocess import PIPE

from TestUtils import TestUtilsMixin, CLOUDBASE_DIR

log = logging.getLogger('test.auto')

from readwrite import SunnyDayTest, Interleaved
from delete import DeleteTest

class TabletShouldSplit(SunnyDayTest):

    order = 80

    settings = TestUtilsMixin.settings.copy()
    settings.update({
        'cloudbase.tabletserver.maxMapMemory':5000,
        'cloudbase.tabletserver.compactionDelay': 1,
        'cloudbase.tabletserver.maxOpen': 500,
        })
    tableSettings = SunnyDayTest.tableSettings.copy()
    tableSettings['test_ingest'] = { 
    	'cloudbase.tablet.split.threshold': 5000,
        }
    def runTest(self):

        self.waitForStop(self.ingester, 60)
        self.waitForStop(self.verify(self.masterHost(), self.options.rows), 60)

        # let the server split tablets and move them around
        self.sleep(30)
        
        # flush to disk
        self.shutdown_cloudbase()
        self.start_cloudbase()
        self.sleep(10)

        # verify that we can read all the data: give it a minute to load
        # tablets
        self.waitForStop(self.verify(self.masterHost(), self.options.rows),
                         120)
        self.shutdown_cloudbase()
        
        # verify we got a bunch of splits
        handle = self.runOn(self.masterHost(), [
            'hadoop', 'fs', '-ls', CLOUDBASE_DIR + '/tables/test_ingest'
            ], stdout=PIPE)
        out, err = handle.communicate()
        self.assert_(len(out.split('\n')) > 10)

class InterleaveSplit(Interleaved):
    order = 80

    settings = TestUtilsMixin.settings.copy()
    settings.update({
        'cloudbase.tabletserver.maxMapMemory':5000,
        'cloudbase.tabletserver.compactionDelay': 1,
        'cloudbase.tabletserver.maxOpen': 500,
        })
    tableSettings = SunnyDayTest.tableSettings.copy()
    tableSettings['test_ingest'] = { 
    	'cloudbase.tablet.split.threshold': 10000,
        }

    def waitTime(self):
        return Interleaved.waitTime(self) * 10

    def runTest(self):
        Interleaved.runTest(self)
        handle = self.runOn(self.masterHost(), [
            'hadoop', 'fs', '-ls', '%s/tables/test_ingest' % CLOUDBASE_DIR
            ], stdout=PIPE)
        out, err = handle.communicate()
        self.assert_(len(out.split('\n')) > 30)

class DeleteSplit(DeleteTest):
    order = 80
        
    settings = TestUtilsMixin.settings.copy()
    settings.update({
        'cloudbase.tabletserver.maxMapMemory': 10000,
        'cloudbase.tabletserver.compactionDelay': 1,
        'cloudbase.tabletserver.maxOpen': 100,
        'cloudbase.tablet.majorCompaction.compactAllProbability': 0.0,
        })
    tableSettings = SunnyDayTest.tableSettings.copy()
    tableSettings['test_ingest'] = { 
    	'cloudbase.tablet.split.threshold': 80000,
        }

    def runTest(self):
        DeleteTest.runTest(self)
        handle = self.runOn(self.masterHost(), [
            'hadoop', 'fs', '-ls', '%s/tables/test_ingest' % CLOUDBASE_DIR
            ], stdout=PIPE)
        out, err = handle.communicate()
        self.assert_(len(out.split('\n')) > 30)

def suite():
    result = unittest.TestSuite()
    result.addTest(DeleteSplit())
    result.addTest(TabletShouldSplit())
    result.addTest(InterleaveSplit())
    return result
