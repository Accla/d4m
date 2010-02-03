
# simplify scanning with a python-style iterator wrapper
def scan(client, *args):
    initialScan = client.startScan(*args)
    scanID = initialScan.scanID
    result = initialScan.result
    for row in result.data:
        yield row
    while result.more:
        result = client.continueScan(scanID)
        for row in result.data:
            yield row
