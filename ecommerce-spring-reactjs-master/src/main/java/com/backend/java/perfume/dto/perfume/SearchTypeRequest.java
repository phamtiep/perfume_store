package com.backend.java.perfume.dto.perfume;

import com.backend.java.perfume.enums.SearchPerfume;
import lombok.Data;

@Data
public class SearchTypeRequest {
    private SearchPerfume searchType;
    private String text;
}
