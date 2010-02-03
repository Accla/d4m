#! /usr/bin/env python

import sys

from cbshell import client
import time

from clientproxy.ttypes import *
from data.ttypes import Range, Column
from scan import scan

def main(argv):
    table = 'atable'
    column = 'cats:CeilingCat'

    if len(argv) > 0: table = argv[0]
    if len(argv) > 1: column = argv[1]

    columnFamily, columnQualifier, columnVisibility = (column + ':::').split(':')[:3]

    c = client()
    
    c.createTable(table, map(str, range(10)))
    cr = Column(locals())

    # make a bunch of mutations 
    mutations = []
    for i in range(100):
        cu = ColumnUpdate(dict(column=cr, value='value:%d' % i))
        mutation = Mutation(dict(row = "%d" % i, updates = [cu]))
        mutations.append(mutation)

    # ship them in
    errs = c.update(table, mutations)

    # list them out
    for row in scan(c, table, Range(), [], 10):
        print repr(row.key.row), repr(row.value)

    # delete them
    for m in mutations:
        for cu in m.updates:
            cu.deleted = True
    errs = c.update(table, mutations)

    results = list(scan(c, table, Range(), [], 10))
    assert len(results) == 0


main(sys.argv[1:])
