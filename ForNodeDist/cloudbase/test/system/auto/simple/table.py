import os
import logging
import unittest
import time
import re
from subprocess import PIPE

from TestUtils import TestUtilsMixin, ROOT, ROOT_PASSWORD, CLOUDBASE_DIR

log = logging.getLogger('test.auto')

import readwrite

class TableTest(readwrite.SunnyDayTest):
    "Make a table, use it, delete it, make it again"

    order = 25

    def createTable(self, table):
        
        import tempfile
        fileno, filename = tempfile.mkstemp()
        fp = os.fdopen(fileno, "wb")
        try:
            for i in range(0, 999999):
                fp.write("%08x\n" % i)
            fp.flush()
            code, out, err = self.rootShell(self.masterHost(),
                                            "createtable %s SPLIT %s\n" % (table, filename))
            self.processResult(out, err, code)
        finally:
            fp.close()
            os.unlink(filename)

    def sshell(self, msg):
        code, out, err = self.rootShell(self.masterHost(), msg)
        self.assert_(code == 0)
	return out

    def runTest(self):
        waitTime = 120 * self.options.rows / 1e6 + 60

        self.waitForStop(self.ingester, 30)
        self.waitForStop(self.verify(self.masterHost(), self.options.rows),
                         waitTime)

        # See ticket #595:
        self.sleep(5)
        
        self.sshell("deletetable test_ingest\n")
	self.sleep(10)
        self.shutdown_cloudbase()
	self.sleep(30)
        self.start_cloudbase()
    
        out = self.sshell("table !METADATA\nscan\n")
        self.assert_(re.search('^test_ingest', out, re.MULTILINE) == None)

        handle = self.runOn(self.masterHost(),
                            ['hadoop', 'fs', '-ls', '%s/tables' % CLOUDBASE_DIR],
                            stdout=PIPE)
        out, err = handle.communicate()
        self.assert_(out.find('test_ingest') < 0)

        out = self.sshell("table test_ingest\nscan\n")
	self.assert_(out.find("not connected to a table") >= 0)

        self.createTable('test_ingest')
        self.waitForStop(self.ingest(self.masterHost(), self.options.rows), 30)
        self.waitForStop(self.verify(self.masterHost(), self.options.rows),
                         waitTime)
        self.shutdown_cloudbase()
        
class CreateTableSplitFile(TableTest):
    def createTable(self, table):
        import tempfile
        fileno, filename = tempfile.mkstemp()
        fp = os.fdopen(fileno, "wb")
        try:
            fp.write("a\nb\nc\nd\na\nb\nc\n")
            code, out, err = self.rootShell(self.masterHost(),
                                            "createtable %s SPLITS %s\n" %
                                            (table, filename))
            self.processResult(out, err, code)
        finally:
            fp.close()
            os.unlink(filename)


def suite():
    result = unittest.TestSuite()
    result.addTest(TableTest())
    result.addTest(CreateTableSplitFile())
    return result
