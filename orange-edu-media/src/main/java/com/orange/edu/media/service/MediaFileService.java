package com.orange.edu.media.service;

import com.orange.base.model.RestResponse;
import com.orange.edu.media.model.dto.UploadFileParamsDto;
import com.orange.edu.media.model.dto.UploadFileResultDto;
import com.orange.edu.media.model.po.MediaFiles;

import java.io.File;


public interface MediaFileService {

    /**
     * @description 检查文件
     * @param fileMd5
     * @return
     */
    public RestResponse<Boolean> checkFile(String fileMd5);


    public RestResponse<Boolean> checkChunk(String fileMd5, int chunkIndex);

    public RestResponse uploadChunk(String fileMd5,int chunk,String localChunkFilePath);

    public RestResponse mergechunks(Long companyId, String fileMd5, int chunkTotal, UploadFileParamsDto uploadFileParamsDto);

    /**
     * 上传文件
     * @param companyId 机构id
     * @param uploadFileParamsDto 上传文件信息
     * @param localFilePath 文件磁盘路径
     * @return 文件信息
     */
    public UploadFileResultDto uploadFile(Long companyId, UploadFileParamsDto uploadFileParamsDto, String localFilePath);

    /**
     * @description 将文件信息添加到文件表
     * @param companyId  机构id
     * @param fileMd5  文件md5值
     * @param uploadFileParamsDto  上传文件的信息
     * @param bucket  桶
     * @param objectName 对象名称
     */

    public MediaFiles addMediaFilesToDb(Long companyId, String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucket, String objectName);

    public File downloadFileFromMinIO(String bucket, String objectName);

    public boolean addMediaFilesToMinIO(String localFilePath,String mimeType,String bucket, String objectName);
}
