package com.ontology.service;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;

import java.util.Map;


public interface AppService {

    Map<String, Object> register(String action, RegisterDto req) throws Exception;

    String registerCallback(String action, MessageCallbackDto req) throws Exception;

    Result registerResult(String action, String id);

    Map<String, Object> login(String action) throws Exception;

    String loginCallback(String action, MessageCallbackDto req);

    Result loginResult(String action, String id);

    Map<String, Object> invokeContract(String action, String userName) throws Exception;

    String getParams(String action, String id);

    JSONObject invokeCallback(String action, MessageCallbackDto req) throws Exception;

    Result invokeResult(String action, String id);

    JSONObject getClaimAuthorization(String action) throws Exception;

    void claimCallback(String action, InvokeDto req) throws Exception;

    Result claimResult(String action, String id);

    Map<String, Object> registerWithClaim(String action, RegisterDto req) throws Exception;

    String registerWithClaimCallback(String action, MessageCallbackDto req);


}
