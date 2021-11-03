package cn.imcao.influxdbapi.controller;

import cn.imcao.influxdbapi.dao.QueryDao;
import cn.imcao.influxdbapi.pojo.QueryParam;
import com.influxdb.query.FluxTable;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Api(value = "/", tags = {"查询"})
public class QueryController {

    private final QueryDao queryDao;

    public QueryController(QueryDao queryDao) {
        this.queryDao = queryDao;
    }

    @ApiOperation(value = "查询一条 field 记录", notes = "example: http://localhost:8080/query/MQTT/laptop/used_mem?start=-5m&stop=0m&host=ImCaO%27s%20laptop&topic=mqtt/js")
    @GetMapping("/query/{bucket}/{measurement}/{field}")
    public List<FluxTable> query(@ApiParam(value = "bucket 名", required = true) @PathVariable("bucket") String bucket,
                                 @ApiParam(value = "measurement 名", required = true) @PathVariable("measurement") String measurement,
                                 @ApiParam(value = "field 名", required = true) @PathVariable("field") String field,
                                 @ApiParam(value = "查询起始时间", required = true) @RequestParam(value = "start") String start,
                                 @ApiParam(value = "查询结束时间") @RequestParam(value = "stop", required = false) String stop,
                                 @ApiParam(value = "自定义 tags") @RequestParam Map<String, String> params) {
        QueryParam queryParam = new QueryParam();
        queryParam.setBucket(bucket);
        queryParam.setMeasurement(measurement);
        queryParam.setField(field);
        queryParam.setStart(start);
        queryParam.setStop(stop);
        params.remove("start");
        params.remove("stop");
        queryParam.setTags(params);

        return queryDao.queryRange(queryParam);
    }
}
