
hadoop fs -rmr /testmf

echo "creating first set of map files"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf01 -timestamp 1 -size 50 -random 56 1000000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf02 -timestamp 1 -size 50 -random 56 1000000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf03 -timestamp 1 -size 50 -random 56 1000000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf04 -timestamp 1 -size 50 -random 56 1000000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf05 -timestamp 1 -size 50 -random 56 1000000 4000000 1 &

wait

echo "bulk importing"

hadoop fs -rmr /testmfFail
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.client.mapreduce.bulk.BulkOperations import test_ingest /testmf /testmfFail

echo "verifying"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 4000000 1 &

wait

hadoop fs -rmr /testmf

echo "creating second set of map files"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf01 -timestamp 2 -size 50 -random 57 1000000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf02 -timestamp 2 -size 50 -random 57 1000000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf03 -timestamp 2 -size 50 -random 57 1000000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf04 -timestamp 2 -size 50 -random 57 1000000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -mapFile /testmf/mf05 -timestamp 2 -size 50 -random 57 1000000 4000000 1 &

wait

echo "bulk importing"

hadoop fs -rmr /testmfFail
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.client.mapreduce.bulk.BulkOperations import -batchAssignments test_ingest /testmf /testmfFail

echo "creating second set of map files"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 4000000 1 &

