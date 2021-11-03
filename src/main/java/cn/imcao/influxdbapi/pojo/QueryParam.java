package cn.imcao.influxdbapi.pojo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel()
public class QueryParam {

    String bucket;
    String measurement;
    String field;
    String start;
    String stop;
    Map<String, String> tags;
}
