package com.sparta.showmethecode.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class PageResponseDto<T> {

    private List<T> data = new ArrayList<>();

    private int totalPage;
    private long totalElements;
    private int page;
    private int size;
}
