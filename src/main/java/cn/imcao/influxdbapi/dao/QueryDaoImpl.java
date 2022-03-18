package cn.imcao.influxdbapi.dao;

import cn.imcao.influxdbapi.pojo.QueryParam;
import cn.imcao.influxdbapi.utils.TimeBack;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.FluxTable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class QueryDaoImpl implements QueryDao {

    private final InfluxDBClient influxDBClient;

    public QueryDaoImpl(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @Override
    public List<FluxTable> queryRange(QueryParam query) {
        List<FluxTable> res = attemptQuery(query);
        if (res.size() == 0) {
            String lastTime = getLastTime(query);
            String newStart = TimeBack.timeToBack(lastTime, query.getStart());
            newStart = newStart.replace(" ", "T");
            newStart = newStart + "Z";
            query.setStart(newStart);
            query.setStop(lastTime);
            res = attemptQuery(query);
        }
        return res;
    }

    public List<FluxTable> attemptQuery(QueryParam query) {
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
        appendQueryParam(query, fluxBuilder);
        return influxDBClient.getQueryApi().query(fluxBuilder.toString());
    }

    public String getLastTime(QueryParam query) {
        StringBuilder fb = new StringBuilder();
        fb.append(String.format("from(bucket: \"%s\")", query.getBucket()));
        fb.append("|> range(start: -30d, stop: 0m)");
        appendQueryParam(query, fb);
        fb.append("|> last()");
        List<FluxTable> res = influxDBClient.getQueryApi().query(fb.toString());
        return Objects.requireNonNull(res.get(0).getRecords().get(0).getTime()).toString();
    }

    private void appendQueryParam(QueryParam query, StringBuilder fb) {
        if (query.getMeasurement() != null) {
            fb.append(String.format("|> filter(fn: (r) => r[\"_measurement\"] == \"%s\")", query.getMeasurement()));
        }
        if (query.getField() != null) {
            fb.append(String.format("|> filter(fn: (r) => r[\"_field\"] == \"%s\")", query.getField()));
        }
        if (query.getTags() != null) {
            for (Map.Entry<String, String> entry : query.getTags().entrySet()) {
                fb.append(String.format("|> filter(fn: (r) => r[\"%s\"] == \"%s\")", entry.getKey(), entry.getValue()));
            }
        }
    }
}
