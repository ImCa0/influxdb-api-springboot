package cn.imcao.influxdbapi.dao;

import cn.imcao.influxdbapi.pojo.QueryParam;
import com.influxdb.query.FluxTable;

import java.util.List;

public interface QueryDao {

    List<FluxTable> queryRange(QueryParam query);
}
