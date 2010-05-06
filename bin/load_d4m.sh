
#!/bin/sh


PRGDIR=`dirname "$PRG"`
H=`cd "$PRGDIR/.." ; pwd`

scale=10
for fileNumber in 1
do

# ./runjob_load_mlbench_cloud_ingester2_w-args.sh test_table /home/wi20909/hadoopdev/cloud_data/NewDataGen/data2/startVertex.20.2.txt /home/wi20909/hadoopdev/cloud_data/NewDataGen/data2/endVertex.20.txt /home/wi20909/hadoopdev/cloud_data/NewDataGen/data2/weight.20.txt"/>
FILE1="one index,2,3,4,5,6,7,8,9,10,"
FILE2="one column,22,333,4444,55555,666666,7777777,88888888,999999999,10101010101010101010,"
FILE3="val1 value,val2,val3,val4,val5,val6,val7,val8,val9,val10,"

#FILE1="1 2 3 4 5 6 7 8 9 10 "
#FILE2="1 22 333 4444 55555 666666 7777777 88888888 999999999 10101010101010101010 "
#FILE3="val1 val2 val3 val4 val5 val6 val7 val8 val9 val10 "


./load_d4m_w-args.sh f-2-2.llgrid.ll.mit.edu test_table132 $FILE1 $FILE2 $FILE3

done



