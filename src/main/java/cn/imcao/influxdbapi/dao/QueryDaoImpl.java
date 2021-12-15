package cn.imcao.influxdbapi.dao;

import cn.imcao.influxdbapi.pojo.QueryParam;
import cn.imcao.influxdbapi.utils.TimeBack;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxTable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class QueryDaoImpl implements QueryDao {

    private final InfluxDBClient influxDBClient;
    private int fluxTableSize;
    public QueryDaoImpl(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @Override
    public List<FluxTable> queryRange(QueryParam query) {
        /**
         * 第一次查询数据库只是为了获取最后一个不为空的数据的时间点
         * 第二次查询就是在第一次的查询的时间点及其前几分钟内的数据
         * 最终返回的就是第二次查询的数据
         */

        StringBuilder fluxBuilder = new StringBuilder();
        if (query.getBucket() == null || query.getStart() == null) {
            return null;
        }
        fluxBuilder.append(String.format("from(bucket: \"%s\")", query.getBucket()));
        if (query.getStop() != null) {
            fluxBuilder.append(String.format("|> range(start: -30d, stop: %s)", query.getStart(), query.getStop()));
        } else {
            fluxBuilder.append(String.format("|> range(start: %s)", query.getStart()));
        }
        if (query.getMeasurement() != null) {
            fluxBuilder.append(String.format("|> filter(fn: (r) => r[\"_measurement\"] == \"%s\")", query.getMeasurement()));
        }
        if (query.getField() != null) {
            fluxBuilder.append(String.format("|> filter(fn: (r) => r[\"_field\"] == \"%s\")", query.getField()));
        }
        if (query.getTags() != null) {
            for (Map.Entry<String, String> entry : query.getTags().entrySet()) {
                fluxBuilder.append(String.format("|> filter(fn: (r) => r[\"%s\"] == \"%s\")", entry.getKey(), entry.getValue()));
            }
        }

        //获取空值之前一个的数据
        fluxBuilder.append("|> last()");
        /**
         * 查询数据核心
         */
        //将查询的数据临时存储下来
        List<FluxTable> fluxTableBuffer =influxDBClient.getQueryApi().query(fluxBuilder.toString());
        //获取recodes查询了多少数据
        fluxTableSize= fluxTableBuffer.get(0).getRecords().size();
        //获取最后一条记录的时间
        String time=fluxTableBuffer.get(0).getRecords().get(fluxTableSize-1).getTime().toString();
        //获取新的查询开始时间  time-query.getStart()  =>  time
        String newTime= TimeBack.timeToBack(time,query.getStart());
        //将时间格式拼接成数据库可识别格式 : yyyy-MM-ddThh-mm-ssZ
        newTime=newTime.replace(" ","T");
        newTime=newTime+"Z";

        // 然后再去查一次数据库并返回数据
        StringBuilder fluxBuilderHistory = new StringBuilder();
        fluxBuilderHistory.append("from(bucket: \"Machines\")\n" +
                "  |> range(start:"+newTime+", stop:"+time+")\n" +
                "  |> filter(fn: (r) => r[\"_measurement\"] == \"CN002\")\n" +
                "  |> filter(fn: (r) => r[\"_field\"] == \"Vibration\")\n");

        return influxDBClient.getQueryApi().query(fluxBuilder.toString());
    }
}
