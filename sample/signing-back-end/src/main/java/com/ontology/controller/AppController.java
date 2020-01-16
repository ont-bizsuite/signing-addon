package com.ontology.controller;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;


@Api(tags = "Register and Login")
@RestController
@RequestMapping("/api/v1/app")
@CrossOrigin
public class AppController {
    @Autowired
    private AppService appService;

    @ApiOperation(value = "register", notes = "register", httpMethod = "POST")
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDto req) throws Exception {
        String action = "register";
        Map<String, Object> result = appService.register(action,req);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "register callback", notes = "register callback", httpMethod = "POST")
    @PostMapping("/register/callback")
    public Result registerCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "registerCallback";
        String txHash = appService.registerCallback(action,req);
        return new Result(action,0, "SUCCESS", txHash);
    }

    @ApiOperation(value = "register result", notes = "register result", httpMethod = "GET")
    @GetMapping("/register/result/{id}")
    public Result registerResult(@PathVariable String id) throws Exception {
        String action = "registerResult";
        return appService.registerResult(action,id);
    }

    @ApiOperation(value = "login", notes = "login", httpMethod = "POST")
    @PostMapping("/login")
    public Result login() throws Exception {
        String action = "login";
        Map<String, Object> result = appService.login(action);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "login callback", notes = "login callback", httpMethod = "POST")
    @PostMapping("/login/callback")
    public Result loginCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "loginCallback";
        String txHash = appService.loginCallback(action,req);
        return new Result(action,0, "SUCCESS", txHash);
    }

    @ApiOperation(value = "login result", notes = "login result", httpMethod = "GET")
    @GetMapping("/login/result/{id}")
    public Result loginResult(@PathVariable String id) {
        String action = "loginResult";
        return appService.loginResult(action,id);
    }

    @ApiOperation(value = "invoke contract", notes = "invoke contract", httpMethod = "POST")
    @PostMapping("/invoke/{userName}")
    public Result invokeContract(@PathVariable String userName) throws Exception {
        String action = "invokeContract";
        Map<String, Object> result = appService.invokeContract(action,userName);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "get param", notes = "get param", httpMethod = "GET")
    @GetMapping("/invoke/params/{id}")
    public String getParams(@PathVariable String id) throws Exception {
        String action = "getParams";
        return appService.getParams(action,id);
    }

    @ApiOperation(value = "invoke callback", notes = "invoke callback", httpMethod = "POST")
    @PostMapping("/invoke/callback")
    public JSONObject invokeCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "invokeCallback";
        return appService.invokeCallback(action,req);
    }

    @ApiOperation(value = "invoke result", notes = "invoke result", httpMethod = "GET")
    @GetMapping("/invoke/result/{id}")
    public Result invokeResult(@PathVariable String id) throws Exception {
        String action = "invokeResult";
        return appService.invokeResult(action,id);
    }

    @ApiOperation(value = "ask for claim from user", notes = "ask for claim from user", httpMethod = "POST")
    @PostMapping("/claim")
    public Result getClaim() throws Exception {
        String action = "getClaimAuthorization";
        JSONObject result = appService.getClaimAuthorization(action);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "receive claim from OntAuth user", notes = "receive claim from OntAuth user", httpMethod = "POST")
    @PostMapping("/claim/callback")
    public Result claimCallback(@RequestBody InvokeDto req) throws Exception {
        String action = "claimCallback";
         appService.claimCallback(action,req);
        return new Result(action,0, "SUCCESS", "SUCCESS");
    }

    @ApiOperation(value = "query claim result", notes = "query claim result", httpMethod = "GET")
    @GetMapping("/claim/result/{id}")
    public Result claimResult(@PathVariable String id) throws Exception {
        String action = "claimResult";
        return appService.claimResult(action,id);
    }

    @ApiOperation(value = "register with claim", notes = "register with claim", httpMethod = "POST")
    @PostMapping("/register/claim")
    public Result registerWithClaim(@RequestBody RegisterDto req) throws Exception {
        String action = "registerWithClaim";
        Map<String, Object> result = appService.registerWithClaim(action,req);
        return new Result(action,0, "SUCCESS", result);
    }

    @ApiOperation(value = "register with claim callback", notes = "register with claim callback", httpMethod = "POST")
    @PostMapping("/register/claim/callback")
    public Result registerWithClaimCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "registerWithClaimCallback";
        String txHash = appService.registerWithClaimCallback(action,req);
        return new Result(action,0, "SUCCESS", txHash);
    }

}
