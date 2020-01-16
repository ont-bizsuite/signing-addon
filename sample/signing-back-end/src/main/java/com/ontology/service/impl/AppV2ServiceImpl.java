package com.ontology.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.entity.CentReg;
import com.ontology.entity.Invoke;
import com.ontology.entity.Login;
import com.ontology.exception.OntIdException;
import com.ontology.mapper.*;
import com.ontology.service.AppV2Service;
import com.ontology.utils.*;
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
public class AppV2ServiceImpl implements AppV2Service {
    @Autowired
    private CentRegMapper centRegMapper;
    @Autowired
    private InvokeMapper invokeMapper;
    @Autowired
    private ConfigParam configParam;

    private SigningSdk signingSdk;

    private CentralizationOntId ontIdInstance;

    @PostConstruct
    public void init() throws Exception {
        signingSdk = SigningSdk.getInstance(configParam.SIGNING_SERVER_URL);
        signingSdk.init("helloworld.app.ont", "your app wif");
        signingSdk.setPayer("payer address", "payer wif");
        signingSdk.setBlockChainUrl(configParam.RESTFUL_URL);

        //ontId
        ontIdInstance = CentralizationOntId.getInstance();
        ontIdInstance.init("the first wif", "the second wif");
    }

    @Override
    public Map<String, Object> register(String action, CentRegisterDto req) throws Exception {
        String userName = req.getUserName();
        String password = req.getPassword();

        CentReg record = new CentReg();
        record.setUserName(userName);
        CentReg centReg = centRegMapper.selectOne(record);
        if (centReg != null) {
            throw new OntIdException(action, ErrorInfo.USER_ALREADY_EXIST.descCN(), ErrorInfo.USER_ALREADY_EXIST.descEN(), ErrorInfo.USER_ALREADY_EXIST.code());
        }

        record.setPassword(HelperUtil.sha256(password));
        record.setCreateTime(new Date());
        centRegMapper.insertSelective(record);

        Integer id = record.getId();
        log.info("id:{}", id);
        String ontid = ontIdInstance.registerOntId(id);
        record.setOntid(ontid);
        centRegMapper.updateByPrimaryKeySelective(record);

        Map<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("ontid", ontid);
        return result;
    }


    @Override
    public Map<String, Object> login(String action, CentRegisterDto req) throws Exception {
        String userName = req.getUserName();
        String password = req.getPassword();

        CentReg record = new CentReg();
        record.setUserName(userName);

        CentReg centReg = centRegMapper.selectOne(record);
        if (centReg == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        if (!centReg.getPassword().equals(HelperUtil.sha256(password))) {
            throw new OntIdException(action, ErrorInfo.INVALID_PASSWORD.descCN(), ErrorInfo.INVALID_PASSWORD.descEN(), ErrorInfo.INVALID_PASSWORD.code());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("ontid", centReg.getOntid());
        return result;
    }

    @Override
    public Map<String, Object> invokeContract(String action, String userName) throws Exception {
        CentReg record = new CentReg();
        record.setUserName(userName);

        CentReg centReg = centRegMapper.selectOne(record);
        if (centReg == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

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
        qrCodeParams.put("ontid", centReg.getOntid());

        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("qrCode", qrCodeParams);

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
    public Map<String, Object> addOwner(String action, String ontid) throws Exception {
        CentReg record = new CentReg();
        record.setOntid(ontid);
        record = centRegMapper.selectOne(record);
        if (record == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        if (!StringUtils.isEmpty(record.getOwner())) {
            throw new OntIdException(action, ErrorInfo.OWNER_ALREADY_EXIST.descCN(), ErrorInfo.OWNER_ALREADY_EXIST.descEN(), ErrorInfo.OWNER_ALREADY_EXIST.code());
        }

        Integer index = record.getId();
        String id = UUID.randomUUID().toString();
        String data = ontIdInstance.addOwnerParams(index);

        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(data);
        invoke.setOntidIndex(index);
        invokeMapper.insertSelective(invoke);

        String dataUrl = configParam.LOCAL_SERVER_URL + "/api/v2/app/invoke/params/" + id;
        String callbackUrl = configParam.LOCAL_SERVER_URL + "/api/v2/app/add-owner/callback";
        Long expire = (System.currentTimeMillis() + 5 * 60 * 1000) / 1000L;
        String signature = signingSdk.sign(data);
        return ontIdInstance.generateQrCode(id, expire, callbackUrl, dataUrl, signature, false);
    }

    @Override
    public String addOwnerCallback(String action, InvokeDto req) throws Exception {
        String id = (String) req.getExtraData().get("id");
        Invoke invoke = invokeMapper.selectByPrimaryKey(id);
        if (invoke == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        try {
            String ontid = req.getOntid();
            String signedTx = req.getSignedTx();
            boolean b = signingSdk.verifySignature(ontid, signedTx);
            if (!b) {
                throw new OntIdException(action, ErrorInfo.IDENTITY_VERIFY_FAILED.descCN(), ErrorInfo.IDENTITY_VERIFY_FAILED.descEN(), ErrorInfo.IDENTITY_VERIFY_FAILED.code());
            }

            Integer index = invoke.getOntidIndex();
            CentReg centReg = centRegMapper.selectByPrimaryKey(index);
            if (!StringUtils.isEmpty(centReg.getOwner())) {
                throw new OntIdException(action, ErrorInfo.OWNER_ALREADY_EXIST.descCN(), ErrorInfo.OWNER_ALREADY_EXIST.descEN(), ErrorInfo.OWNER_ALREADY_EXIST.code());
            }

            String txHash = ontIdInstance.addOwner(index, ontid);
            log.info(txHash);
            invoke.setState(1);
            invoke.setTxHash(txHash);
            invokeMapper.updateByPrimaryKeySelective(invoke);

            centReg.setOwner(ontid);
            centRegMapper.updateByPrimaryKeySelective(centReg);
            return txHash;
        } catch (Exception e) {
            invoke.setState(0);
            invokeMapper.updateByPrimaryKeySelective(invoke);
            throw e;
        }
    }

    @Override
    public Map<String, Object> removeOwner(String action, String ontid) throws Exception {
        CentReg record = new CentReg();
        record.setOntid(ontid);
        record = centRegMapper.selectOne(record);
        if (record == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        Integer index = record.getId();

        String id = UUID.randomUUID().toString();
        String data = ontIdInstance.removeOwnerParams(index);

        Invoke invoke = new Invoke();
        invoke.setId(id);
        invoke.setParams(data);
        invoke.setOntidIndex(index);
        invokeMapper.insertSelective(invoke);

        String dataUrl = configParam.LOCAL_SERVER_URL + "/api/v2/app/invoke/params/" + id;
        String callbackUrl = configParam.LOCAL_SERVER_URL + "/api/v2/app/remove-owner/callback";
        Long expire = (System.currentTimeMillis() + 5 * 60 * 1000) / 1000L;
        String signature = signingSdk.sign(data);
        return ontIdInstance.generateQrCode(id, expire, callbackUrl, dataUrl, signature, false);
    }

    @Override
    public String removeOwnerCallback(String action, InvokeDto req) throws Exception {
        String id = (String) req.getExtraData().get("id");
        Invoke invoke = invokeMapper.selectByPrimaryKey(id);
        try {
            String ontid = req.getOntid();
            String signedTx = req.getSignedTx();
            boolean b = signingSdk.verifySignature(ontid, signedTx);
            if (!b) {
                throw new OntIdException(action, ErrorInfo.IDENTITY_VERIFY_FAILED.descCN(), ErrorInfo.IDENTITY_VERIFY_FAILED.descEN(), ErrorInfo.IDENTITY_VERIFY_FAILED.code());
            }
            log.info("verify pass");
            Integer index = invoke.getOntidIndex();
            String txHash = ontIdInstance.removeOwner(index, ontid);
            log.info(txHash);
            invoke.setState(1);
            invoke.setTxHash(txHash);
            invokeMapper.updateByPrimaryKeySelective(invoke);
            return txHash;
        } catch (Exception e) {
            invoke.setState(0);
            invokeMapper.updateByPrimaryKeySelective(invoke);
        }
        return null;
    }

    @Override
    public Map<String, Object> ownerVerify(String action) {
        return null;
    }

    @Override
    public void ownerVerifyCallback(String action, OntAuthDto req) {

    }

    @Override
    public Map<String, Object> loginWithOwner(String action) throws Exception {
        String id = UUID.randomUUID().toString();

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
        Map<String, Object> qrCodeParams = signingSdk.invoke("loginWithOwner", id);
        qrCodeParams.put("signature", signature);
        Map<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("qrCode", qrCodeParams);
        return result;
    }

    @Override
    public void loginWithOwnerCallback(String action, MessageCallbackDto req) {
        String id = req.getId();
        String ontid = req.getOntid();
        Boolean verified = req.getVerified();
        Invoke invoke = invokeMapper.selectByPrimaryKey(id);
        if (invoke == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        if (verified) {
            invoke.setState(1);
            invoke.setOntid(ontid);
        } else {
            invoke.setState(0);
        }
        invokeMapper.updateByPrimaryKeySelective(invoke);
    }

    @Override
    public Result loginResult(String action, String id) {
        Map<String, Object> result = new HashMap<>();

        Invoke invoke = invokeMapper.selectByPrimaryKey(id);
        if (invoke == null) {
            throw new OntIdException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }

        Integer state = invoke.getState();
        if (state!=null && state == 1) {
            String ontid = invoke.getOntid();
            CentReg reg = new CentReg();
            reg.setOwner(ontid);
            List<CentReg> list = centRegMapper.select(reg);
            if (CollectionUtils.isEmpty(list)) {
                result.put("result", "2");
            } else {
                CentReg centReg = list.get(0);
                result.put("result", "1");
                result.put("ontid", centReg.getOntid());
                result.put("userName", centReg.getUserName());
            }
        } else {
            result.put("result", null);
        }

        return new Result(action, ErrorInfo.SUCCESS.code(), ErrorInfo.SUCCESS.descEN(), result);
    }

}
