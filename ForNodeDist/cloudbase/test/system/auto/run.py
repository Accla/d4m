#! /usr/bin/env python

import os
import logging
import unittest
import glob
import re
import sys
from subprocess import Popen, PIPE

from TestUtils import CLOUDBASE_HOME, CLOUDBASE_DIR
COBERTURA_HOME = os.path.join(CLOUDBASE_HOME, 'lib', 'test', 'cobertura')
import sleep

log = logging.getLogger('test.auto')

def getTests():
    allTests = []
    base = os.path.dirname(os.path.realpath(__file__))
    sys.path.insert(0, base)
    for path in glob.glob('%s/*/*.py' % base):
        path = path[len(base):]
        if path.find('__init__') >= 0: continue
        moduleName = path.replace(os.path.sep, '.')
        moduleName = moduleName.lstrip('.')[:-3]
        module = __import__(moduleName, globals(), locals(), [moduleName])
        allTests.extend(list(module.suite()))
    return allTests

def parseArguments(parser, allTests):
    for test in allTests:
        if hasattr(test, 'add_options'):
            test.add_options(parser)
    options, hosts = parser.parse_args()
    options.hosts = hosts or ['localhost']
    return options

def testName(test):
    klass = test.__class__
    return '%s.%s' % (klass.__module__, klass.__name__)

def filterTests(allTests, patterns):
    if not patterns:
        return allTests
    filtered = []
    for test in allTests:
	name = testName(test)
        for pattern in patterns:
            if re.search(pattern, name, re.IGNORECASE):
                filtered.append(test)
                break
        else:
            log.debug("Test %s filtered out", name)
    return filtered

def sortTests(tests):
    def compare(t1, t2):
        return cmp(getattr(t1, 'order', 50), getattr(t2, 'order', 50))
    copy = tests[:]
    copy.sort(compare)
    return copy

def assignOptions(tests, options):
    for test in tests:
        test.options = options

def run(cmd, **kwargs):
    log.debug("Running %s", ' '.join(cmd))
    handle = Popen(cmd, stdout=PIPE, **kwargs)
    out, err = handle.communicate()
    log.debug("Result %d (%r, %r)", handle.returncode, out, err)
    return handle.returncode

def fixCoberturaShellScripts():
    "software depot DOS-ified the scripts"
    shellScripts = glob.glob(COBERTURA_HOME + "/*.sh")
    run(['sed', '-i', r's/\r//'] + shellScripts)
    run(['chmod', '+x'] + shellScripts)

def removeCoverageFromPreviousRun():
    """If the class files change between runs, we get confusing results.
    We might be able to remove the files only if they are older than the
    jar file"""
    for f in (os.path.join(os.environ['HOME'], 'cobertura.ser'),
              'cobertura.ser'):
        try:
            os.unlink(f)
        except OSError:
            pass

def instrumentCloudbaseJar(jar):
    instrumented = jar[:-4] + "-instrumented" + ".jar"
    try:
        os.unlink(instrumented)
    except OSError:
        pass
    os.link(jar, instrumented)
    cmd = os.path.join(COBERTURA_HOME, "cobertura-instrument.sh")
    run(['sh', '-c', '%s --includeClasses "cloudbase.*" %s' % (
        cmd, instrumented)])
    assert os.path.exists('cobertura.ser')
    return instrumented

def mergeCoverage():
    "Most of the coverage ends up in $HOME due to ssh'ing around"
    fname = 'cobertura.ser'
    run(['sh', '-c', ' '.join([
        os.path.join(COBERTURA_HOME, "cobertura-merge.sh"),
        os.path.join(os.environ['HOME'], fname),
        fname])])

def produceCoverageReport(sourceDirectories):
    reporter = os.path.join(COBERTURA_HOME, 'cobertura-report.sh')
    run(['sh', '-c', ' '.join([reporter,
                              '--destination', 'test/reports/cobertura-xml',
                              '--format', 'xml',
                              '--datafile', 'cobertura.ser'] +
                              sourceDirectories)])
    run(['sh', '-c', ' '.join([reporter,
                              '--destination', 'test/reports/cobertura-html',
                              '--format', 'html',
                              '--datafile', 'cobertura.ser'] +
                              sourceDirectories)])
    
def main():
    from optparse import OptionParser
    usage = "usage: %prog [options] [host1 [host2 [hostn...]]]"
    parser = OptionParser(usage)
    parser.add_option('-l', '--list', dest='list', action='store_true',
		      default=False)
    parser.add_option('-v', '--level', dest='logLevel',
                      default=logging.WARN, type=int,
                      help="The logging level (%default)")
    parser.add_option('-t', '--test', dest='tests',
                      default=[], action='append',
                      help="A regular expression for the test to run.")
    parser.add_option('-C', '--coverage', dest='coverage',
                      default=False, action='store_true',
                      help="Produce a coverage report")
    parser.add_option('-r', '--repeat', dest='repeat',
                      default=1, type=int,
                      help='Number of times to repeat the tests')

    allTests = getTests()
    options = parseArguments(parser, allTests)
    
    logging.basicConfig(level=options.logLevel)
    filtered = filterTests(allTests, options.tests)

    filtered = sortTests(filtered)
    if options.list:
	for test in filtered:
	    print testName(test)
        sys.exit(0)

    if os.system("hadoop fs -ls %s >/dev/null 2>&1 < /dev/null" % CLOUDBASE_DIR) == 0:
        sys.stderr.write('%s already exists in HDFS. Run:\n\n'% CLOUDBASE_DIR)
        sys.stderr.write('   $ hadoop fs -rmr /cloudbase-test\n\n')
        sys.stderr.write('before running the tests.\n')
        sys.exit(1)


    assignOptions(filtered, options)
    
    runner = unittest.TextTestRunner(verbosity=2)
    
    suite = unittest.TestSuite()
    map(suite.addTest, filtered)

    options.server_jar = os.path.join(
        CLOUDBASE_HOME,
        "cloudbase-server/target/cloudbase-server-1.0.0-RC2.jar")
    options.client_jar = os.path.join(
        CLOUDBASE_HOME,
        "cloudbase-core/target/cloudbase-core-1.0.0-RC2.jar")
    if options.coverage:
        fixCoberturaShellScripts()
        removeCoverageFromPreviousRun()
        options.server_jar = instrumentCloudbaseJar(options.server_jar)
        options.client_jar = instrumentCloudbaseJar(options.client_jar)
        os.environ['HADOOP_CLASSPATH'] = os.path.join(COBERTURA_HOME,
                                                      'cobertura.jar')
        sleep.scale = 2.0

    for i in range(options.repeat):
        runner.run(suite)

    if options.coverage:
        mergeCoverage()
        produceCoverageReport(
            ['cloudbase-core/src/main/java',
             'cloudbase-server/src/main/java']
            )


if __name__ == '__main__':
    main()
