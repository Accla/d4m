
echo "ingesting first halves (0 to (500K - 1), 1M to (1.5M - 1), etc)"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 4000000 1 &

wait

echo "ingesting second halves (500K to (1M - 1), 1.5M to (2M - 1), etc) and verifying first halves"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 4000000 1 &

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 1500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 2500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 3500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 1 -size 50 -random 56 500000 4500000 1 &

wait

echo "verifying complete range"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 1000000 4000000 1 &

wait

echo "ingesting first halves (0 to (500K - 1), 1M to (1.5M - 1), etc) w/ new timestamp AND verifying second half w/ old timestamp"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 4000000 1 &

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 1500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 2500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 3500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 1 -random 56 500000 4500000 1 &


wait

echo "ingesting second halves (500K to (1M - 1), 1.5M to (2M - 1), etc) and verifying first halves"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 500000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 500000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 500000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 500000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 500000 4000000 1 &

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 1500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 2500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 3500000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.TestIngest -timestamp 2 -size 50 -random 57 500000 4500000 1 &

wait

echo "verifying complete range"

../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 0 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 1000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 2000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 3000000 1 &
../../../bin/cloudbase.sh jar ../../../lib/cloudbase.jar cloudbase.core.test.VerifyIngest -size 50 -timestamp 2 -random 57 1000000 4000000 1 &


wait
