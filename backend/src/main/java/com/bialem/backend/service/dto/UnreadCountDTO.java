package com.bialem.backend.service.dto;

import java.io.Serializable;

public class UnreadCountDTO implements Serializable {

    private long count;

    public UnreadCountDTO() {}

    public UnreadCountDTO(long count) {
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
