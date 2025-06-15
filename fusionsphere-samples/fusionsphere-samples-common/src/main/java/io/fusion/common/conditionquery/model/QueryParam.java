package io.fusion.common.conditionquery.model;

import lombok.Data;

import java.util.List;

@Data
public class QueryParam {
    private List<Condition> conditions;
    private List<Sort> sorts;
    private Integer page;
    private Integer size;
    
    @Data
    public static class Condition {
        private String field;
        private String operator;
        private Object value;
        private String logic; // and or
        private List<Condition> conditions; // 嵌套条件
    }
    
    @Data
    public static class Sort {
        private String field;
        private String order; // asc, desc
    }
}