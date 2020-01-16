package com.ontology.service;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;

import java.util.Map;


public interface AppV2Service {

    Map<String, Object> register(String action, CentRegisterDto req) throws Exception;

    Map<String, Object> login(String action, CentRegisterDto req) throws Exception;

    Map<String, Object> invokeContract(String action, String userName) throws Exception;

    String getParams(String action, String id);

    JSONObject invokeCallback(String action, MessageCallbackDto req) throws Exception;

    Result invokeResult(String action, String id);

    Map<String, Object> addOwner(String action, String ontid) throws Exception;

    String addOwnerCallback(String action, InvokeDto req) throws Exception;

    Map<String, Object> removeOwner(String action, String ontid) throws Exception;

    String removeOwnerCallback(String action, InvokeDto req) throws Exception;

    Map<String, Object> ownerVerify(String action);

    void ownerVerifyCallback(String action, OntAuthDto req);

    Map<String, Object> loginWithOwner(String action) throws Exception;

    void loginWithOwnerCallback(String action, MessageCallbackDto req);

    Result loginResult(String action, String id);

}
