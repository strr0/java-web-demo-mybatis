package com.example.demo.config.mybatis.model;

import java.util.List;

public class Page<T> extends Pageable {
    private Integer total;
    private List<T> content;

    public Page(Integer page, Integer size) {
        super.setPage(page);
        super.setSize(size);
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
