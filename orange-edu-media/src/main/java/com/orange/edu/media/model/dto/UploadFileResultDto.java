package com.orange.edu.media.model.dto;


import com.orange.edu.media.model.po.MediaFiles;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上传普通文件成功响应结果
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UploadFileResultDto extends MediaFiles {
}
