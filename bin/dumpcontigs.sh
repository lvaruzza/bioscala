bin=`readlink -f $0`
base=`dirname $bin`/../

scala -cp $base/target/bioscala-1.0-SNAPSHOT.jar bio.cli.RunCmdlet DumpContigs $*

