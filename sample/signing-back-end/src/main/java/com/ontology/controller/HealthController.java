package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.utils.ErrorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags = "Health Indicator")
@RestController
@RequestMapping("/api/v1/health")
@CrossOrigin
public class HealthController {

    @ApiOperation(value = "Health Indicator", notes = "Health Indicator", httpMethod = "GET")
    @GetMapping
    public Result healthCheck() {
        String action = "healthIndicator";
        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), ErrorInfo.SUCCESS.descEN());
    }

}
