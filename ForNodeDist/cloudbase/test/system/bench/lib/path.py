import os

HERE = os.path.dirname(__file__)
CLOUDBASE_HOME = os.path.normpath(
    os.path.join(HERE, *(os.pardir,)*4)
    )

def cloudbase(*args):
    return os.path.join(CLOUDBASE_HOME, *args)

def cloudbaseJar():
    import glob
    options = (glob.glob(cloudbase('lib', 'cloudbase*.jar')) +
               glob.glob(cloudbase('cloudbase', 'target', 'cloudbase*.jar')))
    options = [jar for jar in options if jar.find('instrumented') < 0]
    return options[0]

