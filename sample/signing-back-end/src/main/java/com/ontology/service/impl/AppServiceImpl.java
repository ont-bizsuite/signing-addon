package com.ontology.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.entity.Claim;
import com.ontology.entity.Invoke;
import com.ontology.entity.Login;
import com.ontology.entity.Register;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.ClaimMapper;
import com.ontology.mapper.InvokeMapper;
import com.ontology.mapper.LoginMapper;
import com.ontology.mapper.RegisterMapper;
import com.ontology.service.AppService;
import com.ontology.utils.*;
import io.ont.addon.claim.sdk.common.ClaimUtility;
import io.ont.addon.claim.sdk.consumer.Application;
import io.ont.addon.claim.sdk.consumer.ClaimModel;
import io.ont.addon.claim.sdk.consumer.Consumer;
import io.ont.addon.signing.sdk.CentralizationOntId;
import io.ont.addon.signing.sdk.SigningSdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AppServiceImpl implements AppService {
    @Autowired
    private RegisterMapper registerMapper;
    @Autowired
    private LoginMapper loginMapper;
    @Autowired
    private InvokeMapper invokeMapper;
    @Autowired
    private ClaimMapper claimMapper;
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private SDKUtil sdkUtil;

    private SigningSdk signingSdk;

    private CentralizationOntId ontIdInstance;

    private Consumer consumer;

    @PostConstruct
    public void init() throws Exception {
        signingSdk = SigningSdk.getInstance(configParam.SIGNING_SERVER_URL);
        signingSdk.init("helloworld.app.ont", "your app wif");
        signingSdk.setPayer("payer address", "payer wif");
        signingSdk.setBlockChainUrl(configParam.RESTFUL_URL);

        //ontId
        ontIdInstance = CentralizationOntId.getInstance();
        ontIdInstance.init("the first wif", "the second wif");

        // init consumer claim sdk
        Application.init("helloworld.app.ont", "your app private key",
                "Hello World", "icon");
        Application.setRestfulUrl(configParam.RESTFUL_URL);
        consumer = new Consumer();
    }

    @Override
    public Map<String, Object> register(String action, RegisterDto req) throws Exception {
        return commonRegister(action, req, Constant.ACTION_REGISTER);
    }

    private Map<String, Object> commonRegister(String action, RegisterDto req, String actionRegister) throws Exception {
        String userName = req.getUserName();
        String id = UUID.randomUUID().toString();
        // check duplicate
        Register register = new Register();
        register.setUserName(userName);
        register.setState(Constant.STATE_SUCCESS);
        List<Register> list = registerMapper.select(register);
        if (!CollectionUtils.isEmpty(list)) {
            throw new OntIdException(action, ErrorInfo.USER_ALREADY_EXIST.descCN(), ErrorInfo.USER_ALREADY_EXIST.descEN(), ErrorInfo.USER_ALREADY_EXIST.code());
        }

        register.setId(id);
        register.setState(null);
        register.setCreateTime(new Date());
        registerMapper.insertSelective(register);

        List<Map<String, Object>> argsList = new ArrayList<>();
        Map<String, Object> arg0 = new HashMap<>();
        arg0.put("name", "register");
        arg0.put("value", "ontid");
        argsList.add(arg0);
        String params = signingSdk.constructMessage(argsList);
        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(params);
        invokeMapper.insertSelective(invoke);

        String signature = signingSdk.sign(params);
        Map<String, Object> qrCodeParams = signingSdk.invoke(actionRegister, id);
        qrCodeParams.put("signature", signature);

        Map<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("qrCode",qrCodeParams);

        return result;
    }

    @Override
    public String registerCallback(String action, MessageCallbackDto req) {
        return commonRegisterCallback(action, req);
    }

    private String commonRegisterCallback(String action, MessageCallbackDto req) {
        String id = req.getId();
        Boolean verified = req.getVerified();
        String ontid = req.getOntid();
        Register register = registerMapper.selectByPrimaryKey(id);
        if (register == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        if (verified) {
            // ontid和用户名查重
            String userName = register.getUserName();
            List<Register> list = registerMapper.selectByOntidAndUserName(ontid, userName);
            if (!CollectionUtils.isEmpty(list)) {
                register.setState(Constant.REGISTER_STATE_ALREADY_EXIST);
            } else {
                register.setOntid(ontid);
                register.setState(Constant.STATE_SUCCESS);
            }
        } else {
            register.setState(Constant.STATE_FAILURE);
        }
        registerMapper.updateByPrimaryKeySelective(register);
        return null;
    }

    @Override
    public Result registerResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Register register = registerMapper.selectByPrimaryKey(id);
        if (register == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Integer state = register.getState();
        if (state != null) {
            result.put("result", state.toString());
            result.put("ontid", register.getOntid());
            result.put("userName", register.getUserName());
        } else {
            result.put("result", null);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @Override
    public Map<String, Object> login(String action) throws Exception {
        String id = UUID.randomUUID().toString();
        Login login = new Login();
        login.setId(id);
        login.setCreateTime(new Date());
        loginMapper.insertSelective(login);

        List<Map<String, Object>> argsList = new ArrayList<>();
        Map<String, Object> arg0 = new HashMap<>();
        arg0.put("name", "login");
        arg0.put("value", "String:ontid");
        argsList.add(arg0);
        String params = signingSdk.constructMessage(argsList);
        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(params);
        invokeMapper.insertSelective(invoke);

        String signature = signingSdk.sign(params);
        Map<String, Object> qrCodeParams = signingSdk.invoke(Constant.ACTION_LOGIN, id);
        qrCodeParams.put("signature", signature);
        Map<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("qrCode",qrCodeParams);
        return result;
    }

    @Override
    public String loginCallback(String action, MessageCallbackDto req) {
        String id = req.getId();
        Boolean verified = req.getVerified();
        String ontid = req.getOntid();
        Login login = loginMapper.selectByPrimaryKey(id);
        if (login == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        if (verified) {
            // find register info
            Register register = new Register();
            register.setOntid(ontid);
            register = registerMapper.selectOne(register);
            if (register == null) {
                login.setState(Constant.LOGIN_STATE_NOT_REGISTER);
            } else {
                login.setOntid(ontid);
                login.setState(Constant.STATE_SUCCESS);
                login.setUserName(register.getUserName());
            }
        } else {
            login.setState(Constant.STATE_FAILURE);
        }
        loginMapper.updateByPrimaryKey(login);
        return null;
    }

    @Override
    public Result loginResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Login login = loginMapper.selectByPrimaryKey(id);
        if (login == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Integer state = login.getState();
        if (state != null) {
            result.put("result", state.toString());
            result.put("ontid", login.getOntid());
            result.put("userName", login.getUserName());
        } else {
            result.put("result", null);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @Override
    public Map<String, Object> invokeContract(String action, String userName) throws Exception {
        String id = UUID.randomUUID().toString();

        List<Map<String, Object>> args = new ArrayList<>();
        Map<String, Object> arg0 = new HashMap<>();
        arg0.put("name", "key");
        arg0.put("value", "String:" + id);
        Map<String, Object> arg1 = new HashMap<>();
        arg1.put("name", "value");
        arg1.put("value", "String: hello world," + userName);

        args.add(arg0);
        args.add(arg1);
        String params = signingSdk.constructTransaction("2e797a71258d1bdafaf7f94f4d90e55f4a28b4b8", "putRecord", args, "AcdBfqe7SG8xn4wfGrtUbbBDxw2x1e8UKm", null, null);

        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(params);
        invokeMapper.insertSelective(invoke);

        String signature = signingSdk.sign(params);
        Map<String, Object> qrCodeParams = signingSdk.invoke(Constant.ACTION_HELLO, id);
        qrCodeParams.put("signature", signature);
        Map<String, Object> result = new HashMap<>();
        result.put("id",id);
        result.put("qrCode",qrCodeParams);
        return result;
    }

    @Override
    public String getParams(String action, String id) {
        Invoke exist = invokeMapper.selectByPrimaryKey(id);
        if (exist == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        return exist.getParams();
    }

    @Override
    public JSONObject invokeCallback(String action, MessageCallbackDto req) throws Exception {
        String id = req.getId();
        String hash = req.getHash();
        Invoke invoke = invokeMapper.selectByPrimaryKey(id);
        if (invoke == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        invoke.setTxHash(hash);
        Object event = signingSdk.checkEvent(hash);
        if (StringUtils.isEmpty(event)) {
            invoke.setState(Constant.STATE_FAILURE);
        } else {
            invoke.setState(Constant.STATE_SUCCESS);
        }
        invokeMapper.updateByPrimaryKeySelective(invoke);
        return null;
    }

    @Override
    public Result invokeResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Invoke invoke = invokeMapper.selectByPrimaryKey(id);
        if (invoke == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Integer state = invoke.getState();
        if (state != null) {
            result.put("result", state.toString());
            result.put("txHash", invoke.getTxHash());
        } else {
            result.put("result", null);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @Override
    public JSONObject getClaimAuthorization(String action) throws Exception {
        String id = UUID.randomUUID().toString();

        // if need claim by model
        ClaimModel model = new ClaimModel();
        // acquire claim qrcode params
        String callbackUrl = configParam.LOCAL_SERVER_URL + "/api/v1/app/claim/callback";
        String dataUrl = configParam.LOCAL_SERVER_URL + "/api/v1/app/invoke/params/" + id;
        String data = consumer.constructData("helloConsumer");
        String signature = consumer.sign(data);
        String qrCodeParams = consumer.qrCodeParams(id, signature, "", dataUrl, callbackUrl, false);
        JSONObject jsonObject = JSONObject.parseObject(qrCodeParams);

        // save data
        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(data);
        invokeMapper.insertSelective(invoke);

        // save record
        Claim record = new Claim();
        record.setId(id);
        record.setCreateTime(new Date());
        claimMapper.insertSelective(record);

        return jsonObject;
    }

    @Override
    public void claimCallback(String action, InvokeDto req) throws Exception {
        String id = (String) req.getExtraData().get("id");
        String claim = (String) req.getExtraData().get("claim");
        Claim record = claimMapper.selectByPrimaryKey(id);
        if (record == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        String ontid = req.getOntid();
        String signedTx = req.getSignedTx();

        // verify signature
        boolean b = ClaimUtility.verifySignature(ontid, signedTx);
        if (!b) {
            throw new OntIdException(action, ErrorInfo.VERIFY_FAILED.descCN(), ErrorInfo.VERIFY_FAILED.descEN(), ErrorInfo.VERIFY_FAILED.code());
        }

        // verify claim
        boolean claimVerify = ClaimUtility.verifyClaim(claim, configParam.CLAIM_ISSUER);
        if (claimVerify) {
            String owner = ClaimUtility.getOwnerByClaim(claim);
            record.setClaim(claim);
            record.setOntid(owner);
            record.setState(Constant.STATE_SUCCESS);
        } else {
            record.setState(Constant.STATE_FAILURE);
        }
        claimMapper.updateByPrimaryKey(record);
    }

    @Override
    public Result claimResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Claim claim = claimMapper.selectByPrimaryKey(id);
        if (claim == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Integer state = claim.getState();
        if (state != null) {
            result.put("result", state.toString());
        } else {
            result.put("result", null);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

    @Override
    public Map<String, Object> registerWithClaim(String action, RegisterDto req) throws Exception {
        return commonRegister(action, req, Constant.ACTION_REGISTER_WITH_CLAIM);
    }

    @Override
    public String registerWithClaimCallback(String action, MessageCallbackDto req) {
        String ontid = req.getOntid();
        Claim claim = new Claim();
        claim.setState(Constant.STATE_SUCCESS);
        claim.setOntid(ontid);
        List<Claim> list = claimMapper.select(claim);
        if (CollectionUtils.isEmpty(list)) {
            String id = req.getId();
            Register register = registerMapper.selectByPrimaryKey(id);
            if (register != null) {
                register.setOntid(ontid);
                register.setState(Constant.STATE_FAILURE);
                registerMapper.updateByPrimaryKeySelective(register);
            }
            throw new OntIdException(action, ErrorInfo.IDENTITY_VERIFY_FAILED.descCN(), ErrorInfo.IDENTITY_VERIFY_FAILED.descEN(), ErrorInfo.IDENTITY_VERIFY_FAILED.code());
        }

        return commonRegisterCallback(action, req);
    }

}
