package com.orange.edu.media.controller;

import com.orange.edu.media.model.dto.UploadFileParamsDto;
import com.orange.edu.media.model.dto.UploadFileResultDto;
import com.orange.edu.media.service.MediaFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Api(value = "媒资文件管理接口", tags = "媒资文件管理接口")
@RestController
public class MediaFileController {

    @Autowired
    MediaFileService mediaFileService;

    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseBody
    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata,@RequestParam(value = "folder",required=false) String folder,@RequestParam(value = "objectName",required=false) String objectName) throws IOException {

        Long companyId = 1232141425L;
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        //文件大小
        uploadFileParamsDto.setFileSize(filedata.getSize());
        //图片
        uploadFileParamsDto.setFileType("001001");
        //文件名称
        uploadFileParamsDto.setFilename(filedata.getOriginalFilename());//文件名称
        //文件大小
        long fileSize = filedata.getSize();
        uploadFileParamsDto.setFileSize(fileSize);
        //创建临时文件
        File tempFile = File.createTempFile("minio", "temp");
        //上传的文件拷贝到临时文件
        filedata.transferTo(tempFile);
        //文件路径
        String absolutePath = tempFile.getAbsolutePath();
        //上传文件 todo
        UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, absolutePath, objectName);
//        UploadFileResultDto uploadFileResultDto = mediaFileService.uploadFile(companyId, uploadFileParamsDto, absolutePath, null);

        return uploadFileResultDto;
    }

//    @ApiOperation("上传文件")
//    @RequestMapping(value = "/upload/coursefile",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public UploadFileResultDto upload(@RequestPart("filedata") MultipartFile filedata,
//                                      @RequestParam(value= "objectName",required=false) String objectName) throws IOException{
//        //....
//        return null;
//    }


    @RequestMapping(value = "/say/hello")
    @ResponseBody
    public String hello(@RequestBody String s)  {
        System.out.println("???????????????");
        return s + "返回成功";
    }

}
