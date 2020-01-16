package com.ontology.controller;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.service.AppService;
import com.ontology.service.AppV2Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;


@Api(tags = "register and login Version 2.0")
@RestController
@RequestMapping("/api/v2/app")
@CrossOrigin
public class AppV2Controller {
    @Autowired
    private AppV2Service appService;

    @ApiOperation(value = "register", notes = "register", httpMethod = "POST")
    @PostMapping("/register")
    public Result register(@Valid @RequestBody CentRegisterDto req) throws Exception {
        String action = "register";
        Map<String, Object> result = appService.register(action, req);
        return new Result(action, 0, "SUCCESS", result);
    }


    @ApiOperation(value = "login with password", notes = "login with password", httpMethod = "POST")
    @PostMapping("/login")
    public Result login(@Valid @RequestBody CentRegisterDto req) throws Exception {
        String action = "login";
        Map<String, Object> result = appService.login(action, req);
        return new Result(action, 0, "SUCCESS", result);
    }

    @ApiOperation(value = "scan and login with owner", notes = "scan and login with owner", httpMethod = "POST")
    @PostMapping("/login/owner")
    public Result loginWithOwner() throws Exception {
        String action = "loginWithOwner";
        Map<String, Object> result = appService.loginWithOwner(action);
        return new Result(action, 0, "SUCCESS", result);
    }

    @ApiOperation(value = "scan and login with owner callback", notes = "scan and login with owner callback", httpMethod = "POST")
    @PostMapping("/login/callback")
    public Result loginWithOwnerCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "loginWithOwnerCallback";
        appService.loginWithOwnerCallback(action,req);
        return new Result(action, 0, "SUCCESS", "SUCCESS");
    }

    @ApiOperation(value = "login with owner result", notes = "login with owner result", httpMethod = "GET")
    @GetMapping("/login/result/{id}")
    public Result loginResult(@PathVariable String id) throws Exception {
        String action = "loginResult";
        return appService.loginResult(action,id);
    }

    @ApiOperation(value = "invoke contract", notes = "invoke contract", httpMethod = "POST")
    @PostMapping("/invoke/{userName}")
    public Result invokeContract(@PathVariable String userName) throws Exception {
        String action = "invokeContract";
        Map<String, Object> result = appService.invokeContract(action, userName);
        return new Result(action, 0, "SUCCESS", result);
    }

    @ApiOperation(value = "get param", notes = "get param", httpMethod = "GET")
    @GetMapping("/invoke/params/{id}")
    public String getParams(@PathVariable String id) throws Exception {
        String action = "getParams";
        return appService.getParams(action, id);
    }

    @ApiOperation(value = "invoke callback", notes = "invoke callback", httpMethod = "POST")
    @PostMapping("/invoke/callback")
    public JSONObject invokeCallback(@RequestBody MessageCallbackDto req) throws Exception {
        String action = "invokeCallback";
        return appService.invokeCallback(action, req);
    }

    @ApiOperation(value = "invoke result", notes = "invoke result", httpMethod = "GET")
    @GetMapping("/invoke/result/{id}")
    public Result invokeResult(@PathVariable String id) throws Exception {
        String action = "invokeResult";
        return appService.invokeResult(action, id);
    }

    @ApiOperation(value = "add owner", notes = "add owner", httpMethod = "POST")
    @PostMapping("/add-owner/{ontid}")
    public Result addOwner(@PathVariable String ontid) throws Exception {
        String action = "addOwner";
        Map<String, Object> qrCode = appService.addOwner(action,ontid);
        return new Result(action, 0, "SUCCESS", qrCode);
    }

    @ApiOperation(value = "add owner callback", notes = "add owner callback", httpMethod = "POST")
    @PostMapping("/add-owner/callback")
    public Result addOwnerCallback(@RequestBody InvokeDto req) throws Exception {
        String action = "addOwnerCallback";
        String txHash = appService.addOwnerCallback(action, req);
        return new Result(action, 0, "SUCCESS", txHash);
    }

    @ApiOperation(value = "delete owner", notes = "delete owner", httpMethod = "POST")
    @PostMapping("/remove-owner/{ontid}")
    public Result removeOwner(@PathVariable String ontid) throws Exception {
        String action = "removeOwner";
        Map<String, Object> qrCode = appService.removeOwner(action,ontid);
        return new Result(action, 0, "SUCCESS", qrCode);
    }

    @ApiOperation(value = "delete owner callback", notes = "delete owner callback", httpMethod = "POST")
    @PostMapping("/remove-owner/callback")
    public Result removeOwnerCallback(@RequestBody InvokeDto req) throws Exception {
        String action = "removeOwnerCallback";
        String txHash = appService.removeOwnerCallback(action, req);
        return new Result(action, 0, "SUCCESS", txHash);
    }

    @ApiOperation(value = "owner verify", notes = "owner verify", httpMethod = "POST")
    @PostMapping("/owner/verify")
    public Result ownerVerify() throws Exception {
        String action = "ownerVerify";
        Map<String, Object> qrCode = appService.ownerVerify(action);
        return new Result(action, 0, "SUCCESS", qrCode);
    }

    @ApiOperation(value = "owner verify callback", notes = "owner verify callback", httpMethod = "POST")
    @PostMapping("/owner/verify/callback")
    public Result ownerVerifyCallback(@Valid @RequestBody OntAuthDto req) throws Exception {
        String action = "ownerVerifyCallback";
        appService.ownerVerifyCallback(action, req);
        return new Result(action, 0, "SUCCESS", "SUCCESS");
    }


}
