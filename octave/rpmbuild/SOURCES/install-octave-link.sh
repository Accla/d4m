#!/bin/sh

## Make a symbolic links for octave and mkoctfile.
PRG=$0
PRGDIR=`dirname "$PRG"`

BASEDIR=`cd "$PRGDIR" ; pwd`
echo "BASEDIR = $BASEDIR"
echo "OCTAVE_PROG = $OCTAVE_PROG"
cd /usr/local/bin
ln -s $BASEDIR/octave octave 
ln -s $BASEDIR/mkoctfile mkoctfile

