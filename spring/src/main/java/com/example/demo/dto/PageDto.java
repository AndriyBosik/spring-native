package com.example.demo.dto;

import com.example.demo.model.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PageDto<T> {
    private final int page;
    private final int size;
    private final long totalElements;
    private final List<T> content;

    public PageDto(PageRequest pageRequest, long totalElements, List<T> content) {
        this.page = pageRequest.getPage();
        this.size = pageRequest.getSize();
        this.totalElements = totalElements;
        this.content = content;
    }

    public int getTotalPages() {
        return (int) Math.ceil(totalElements * 1.0 / size);
    }
}
