package com.example.demo.config.mybatis.model;

public class Pageable {
    private Integer page;
    private Integer size;

    public Pageable() {
        page = 0;
        size = 10;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public <T> Page<T> toPage() {
        return new Page<>(page, size);
    }
}
