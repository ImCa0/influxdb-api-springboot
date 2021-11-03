package cn.imcao.influxdbapi.dao;

import cn.imcao.influxdbapi.pojo.QueryParam;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxTable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class QueryDaoImpl implements QueryDao {

    private final InfluxDBClient influxDBClient;

    public QueryDaoImpl(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @Override
    public List<FluxTable> queryRange(QueryParam query) {
        StringBuilder fluxBuilder = new StringBuilder();
        if (query.getBucket() == null || query.getStart() == null) {
            return null;
        }
        fluxBuilder.append(String.format("from(bucket: \"%s\")", query.getBucket()));
        if (query.getStop() != null) {
            fluxBuilder.append(String.format("|> range(start: %s, stop: %s)", query.getStart(), query.getStop()));
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
        System.out.println(fluxBuilder);

        return influxDBClient.getQueryApi().query(fluxBuilder.toString());
    }
}
