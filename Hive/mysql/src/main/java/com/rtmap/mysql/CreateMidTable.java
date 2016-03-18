package com.rtmap.mysql;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

/**
 * Hello world!
 *
 */
public class CreateMidTable 
{
    public static void main( String[] args ) throws Exception
    {
        if(args == null || args.length < 4 || args.length > 5){
        	System.out.println("Paramter Error: url username password startDate endDate");
        	System.out.println("                url username password 'create'");
        	return;
        }
        
        String url = args[0];
        String username = args[1];
        String password = args[2];
        Connection connection = MySqlConnector.getMySqlConnector(url, username, password);
        if(connection == null){
        	System.out.println("Cannot get connection!");
        	return;
        }
        MySqlOperator operator = new MySqlOperator();
        operator.setConnection(connection);
        
        if(args.length == 4 || "create".equalsIgnoreCase(args[3])){
    		String del = "DROP TABLE IF EXISTS `tb_match_time`";
    		String create = "CREATE TABLE `tb_match_time` (`id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',`time` varchar(30) DEFAULT NULL COMMENT 'Time',`duration` varchar(60) DEFAULT NULL COMMENT 'Duration',PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Time Duration Pair'";
    		operator.executeUpdate(del);
    		operator.executeUpdate(create);
        }else if(args.length == 5){
        	Date startDate = DateUtils.parseToDate(args[3]);
            Date endDate = DateUtils.parseToDate(args[4]);
            if(DateUtils.compareDate(startDate, endDate) ){
            	System.out.println("Paramter Error:startDate equal or large than endDate");
            	return;
            }
            //2015-10-29 15:10  2015-10-29 15:10~15:20
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            
            Date time1 = startDate;
            calendar.add(Calendar.MINUTE, 10);
            Date time2 = calendar.getTime();
            String time = null;
            String duration = null;
            String sql = null;
            //INSERT INTO tb_match_time(time,duration) VALUES('2015-10-29 17:10','2015-10-29 17:10~17:20')
            //DELETE FROM tb_match_time WHERE time='2015-10-29 17:10'
            while(DateUtils.compareDate(endDate, time2)){
            	time = DateUtils.formatDate(time1, "yyyy-MM-dd HH:mm");
            	duration = DateUtils.formatDate(time2, "HH:mm");
            	sql = "DELETE FROM tb_match_time WHERE time='"+time+"'";
            	operator.executeUpdate(sql);
            	sql = "INSERT INTO tb_match_time(time,duration) VALUES('"+time+"','"+time+"~"+duration+"');";
            	operator.executeUpdate(sql);
            	//System.out.println(sql);
            	time1 = time2;
                calendar.add(Calendar.MINUTE, 10);
                time2 = calendar.getTime();
            }
        }
        
        operator.close();
        System.out.println("Finish Job!");
    }
}
