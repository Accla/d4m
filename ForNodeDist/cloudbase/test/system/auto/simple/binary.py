import os
import unittest
import time
from subprocess import PIPE

from TestUtils import TestUtilsMixin

testClass = "cloudbase.core.test.TestBinaryRows"

class BinaryTest(unittest.TestCase, TestUtilsMixin):
    "Test inserting binary data into cloudbase"

    order = 21

    def setUp(self):
        TestUtilsMixin.setUp(self);
        
        # initialize the database
        self.createTable("bt")
    def test(self, *args):
        handle = self.runJarOn(self.masterHost(), testClass, list(args),
                               stdout=PIPE)
        self.waitForStop(handle, 120)
        
    def tearDown(self):
        TestUtilsMixin.tearDown(self)

    def runTest(self):
        self.test("ingest","bt","0","100000")
        self.test("verify","bt","0","100000")
        self.test("randomLookups","bt","0","100000")
        self.test("delete","bt","25000","50000")
        self.test("verify","bt","0","25000")
        self.test("randomLookups","bt","0","25000")
        self.test("verify","bt","75000","25000")
        self.test("randomLookups","bt","75000","25000")
        self.test("verifyDeleted","bt","25000","50000")
        self.shutdown_cloudbase()

class BinaryPreSplitTest(BinaryTest):
    "Test inserting binary data into cloudbase with a presplit table (this will place binary data in !METADATA)"

    def setUp(self):
        TestUtilsMixin.setUp(self);
        self.test("split","bt","8","256")


def suite():
    result = unittest.TestSuite()
    result.addTest(BinaryTest())
    result.addTest(BinaryPreSplitTest())
    return result
