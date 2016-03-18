#!/bin/bash
if [ $1 ]
then
  stat_date=$1
else
  stat_date=`date -d yesterday +%Y-%m-%d`
fi
echo ">>> anjian_barcod generate data,stat_date is $stat_date <<<"

sql=`cat insert.sql`
hive -e ${sql//param_date/$1}


