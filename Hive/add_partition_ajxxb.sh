#!/bin/bash
if [ $1 ]
then
  stat_date=$1
else
  stat_date=`date -d yesterday +%Y-%m-%d`
fi
echo ">>> log_sec_ajxxb add partition,stat_date is $stat_date <<<"

files=`hadoop fs -ls /bcia-queue/calc/anjian/ajxxb/`
for file in $files
do
  path=`basename $file`
  if [ ${path:0:1} = "-" ]
  then  
    ts=${path:1:10}
  else
    ts=${path:0:10}
  fi
  dt=`date -d "@$ts" +"%Y-%m-%d"`
  if [ $stat_date = $dt ]
  then
    sql="alter table log_sec_ajxxb add IF NOT EXISTS partition (stat_date='$dt',stat_time='$ts') location '/bcia-queue/calc/anjian/ajxxb/$path/'"
    echo $sql
    hive -e $sql
  fi
done
