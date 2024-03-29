package com.orange.edu.checkcode.controller;


import com.orange.edu.checkcode.model.CheckCodeParamsDto;
import com.orange.edu.checkcode.model.CheckCodeResultDto;
import com.orange.edu.checkcode.service.CheckCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 验证码服务接口
 *
 */
@Api(value = "验证码服务接口")
@RestController
public class CheckCodeController {
    @Resource(name = "PicCheckCodeService")
    private CheckCodeService picCheckCodeService;


    @ApiOperation(value = "生成验证信息", notes = "生成验证信息")
    @PostMapping(value = "/pic")
    public CheckCodeResultDto generatePicCheckCode(CheckCodeParamsDto checkCodeParamsDto) {
        return picCheckCodeService.generate(checkCodeParamsDto);
    }

    @ApiOperation(value = "校验", notes = "校验")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "业务名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "key", value = "验证key", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "code", value = "验证码", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping(value = "/verify")
    public Boolean verify(String key, String code) {
        return picCheckCodeService.verify(key, code);
    }

    @ApiOperation(value = "生成手机验证码信息", notes = "生成手机验证码信息")
    @GetMapping("/phone")
    public String generatePhoneCheckCode(@RequestParam("number") String number) {
        return "123456";
    }
}
