package com.orange.edu.content.service;

import com.orange.edu.content.model.dto.CourseCategoryTreeDto;

import java.util.List;

public interface CourseCategoryService {
    public List<CourseCategoryTreeDto> queryTreeNodes(String id);
}
