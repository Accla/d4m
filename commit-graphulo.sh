#!/usr/bin/env bash
# This script builds Graphulo from the git repo in $GRAPHULO_HOME,
# copies the Graphulo JAR to D4M_HOME, and commits the new JAR in D4M_HOME.

set -e #safety flag: command fail -> script fail
set -u #safety flag: unset variable reference causes script fail

if [ "${D4M_HOME:-}" = "" ]; then
	echo "Please set D4M_HOME"
	exit 1
fi
if [ "${GRAPHULO_HOME:-}" = "" ]; then
	echo "Please set GRAPHULO_HOME"
	exit 1
fi

cd $D4M_HOME
if output=$(git status --untracked-files=no --porcelain) && [ -z "$output" ]; then
  # Working directory clean

	cd $GRAPHULO_HOME
	if output=$(git status --untracked-files=no --porcelain) && [ -z "$output" ]; then
		# clean
		ghash=$(git rev-parse HEAD)
		gline=$(git log -1 --oneline)
		mvn clean package -DskipTests
		./deploy.sh
		echo "$ghash"
		echo "$gline"
		cd ~/gits/d4m
		git add ./lib/graphulo-*.jar
		git add "./matlab_src/DBinit.m"
		git commit -m "Update Graphulo

Accla/graphulo@$ghash
$gline"
	else
		# Uncommitted changes
		echo "You appear to have uncommitted changes in Graphulo:"
		git status --untracked-files=no  --porcelain
	fi
else 
	# Uncommitted changes
	echo "You appear to have uncommitted changes in D4M:"
	git status --untracked-files=no  --porcelain
fi
