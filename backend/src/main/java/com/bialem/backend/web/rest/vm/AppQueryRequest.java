package com.bialem.backend.web.rest.vm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AppQueryRequest {

    private String table;
    private String action;
    private String select;
    private List<AppFilter> filters = new ArrayList<>();
    private String orderColumn;
    private Boolean orderAsc;
    private Integer limit;
    private Integer offset;
    private Boolean single;
    private Boolean head;
    private Boolean count;
    private Object payload;
    private String onConflict;

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public List<AppFilter> getFilters() {
        return filters;
    }

    public void setFilters(List<AppFilter> filters) {
        this.filters = filters;
    }

    public String getOrderColumn() {
        return orderColumn;
    }

    public void setOrderColumn(String orderColumn) {
        this.orderColumn = orderColumn;
    }

    public Boolean getOrderAsc() {
        return orderAsc;
    }

    public void setOrderAsc(Boolean orderAsc) {
        this.orderAsc = orderAsc;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Boolean getSingle() {
        return single;
    }

    public void setSingle(Boolean single) {
        this.single = single;
    }

    public Boolean getHead() {
        return head;
    }

    public void setHead(Boolean head) {
        this.head = head;
    }

    public Boolean getCount() {
        return count;
    }

    public void setCount(Boolean count) {
        this.count = count;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public String getOnConflict() {
        return onConflict;
    }

    public void setOnConflict(String onConflict) {
        this.onConflict = onConflict;
    }

    public static class AppFilter {

        private String op;
        private String column;
        private Object value;
        private boolean negate;

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public boolean isNegate() {
            return negate;
        }

        public void setNegate(boolean negate) {
            this.negate = negate;
        }
    }

    public static class AppQueryResponse {

        private Object data;
        private String error;
        private Long count;

        public AppQueryResponse() {}

        public AppQueryResponse(Object data, String error, Long count) {
            this.data = data;
            this.error = error;
            this.count = count;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }
    }
}
