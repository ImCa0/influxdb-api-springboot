package cn.imcao.influxdbapi.utils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeBack {

 /**
  * 获取startTime-timeBack的时间
  * @param startTime
  * @param timeBack
  * @return
  */
 public static String timeToBack(String startTime, String timeBack){

        //先将timeBack转化为int型 的
        String[] tokens = timeBack.split("m");
        timeBack=tokens[0];
        int time =Integer.parseInt(timeBack);

        startTime=startTime.replace('T',' ');
        startTime=startTime.substring(0,startTime.length()-1);
        System.out.println(startTime);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//创建时间格式类
        Date date = format.parse(startTime,new ParsePosition(0));//设定一个时间
        /* 核心代码 */
        Calendar beforeTime = Calendar.getInstance();
        beforeTime.setTime(date);
        beforeTime.add(Calendar.MINUTE, time);// 5分钟之前的时间
        Date beforeD = beforeTime.getTime();
        String before5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(beforeD);
        return before5;
    }
}