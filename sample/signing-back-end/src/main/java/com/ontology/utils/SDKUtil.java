package com.ontology.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.Base64;
import com.github.ontio.OntSdk;
import com.github.ontio.account.Account;
import com.github.ontio.common.*;
import com.github.ontio.common.Helper;
import com.github.ontio.core.DataSignature;
import com.github.ontio.core.asset.Sig;
import com.github.ontio.core.transaction.Transaction;
import com.github.ontio.crypto.SignatureScheme;
import com.github.ontio.sdk.exception.SDKException;
import com.github.ontio.sdk.wallet.Identity;
import com.github.ontio.smartcontract.nativevm.abi.NativeBuildParams;
import com.github.ontio.smartcontract.nativevm.abi.Struct;
import com.ontology.secure.ECIES;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SDK 入口类
 *
 * @author 12146
 */
@Component
@Slf4j
public class SDKUtil {

    @Autowired
    private ConfigParam configParam;

    private OntSdk wm;

    private OntSdk getOntSdk() throws Exception {
        if (wm == null) {
            wm = OntSdk.getInstance();
            wm.setRestful(configParam.RESTFUL_URL);
            wm.openWalletFile("wallet.json");
        }
        if (wm.getWalletMgr() == null) {
            wm.openWalletFile("wallet.json");
        }
        return wm;
    }


    public Object checkEvent(String txHash) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Object event = ontSdk.getConnect().getSmartCodeEvent(txHash);
        return event;
    }


    public boolean verifyMessage(String pubKey, String message, String signature) throws Exception {
        Account account = new Account(false, Helper.hexToBytes(pubKey));
        boolean b = account.verifySignature(message.getBytes(), Helper.hexToBytes(signature));
        return b;
    }

    public boolean verifyHex(String pubKey, String message, String signature) throws Exception {
        Account account = new Account(false, Helper.hexToBytes(pubKey));
        boolean b = account.verifySignature(Helper.hexToBytes(message), Helper.hexToBytes(signature));
        return b;
    }

    public String getPublicKey(String ontid) throws Exception {
        OntSdk ontSdk = getOntSdk();
        String s = ontSdk.nativevm().ontId().sendGetPublicKeys(ontid);
        return JSONArray.parseArray(s).getJSONObject(0).getString("Value");
    }

    public Object checkContract(String contractHash) throws Exception {
        OntSdk ontSdk = getOntSdk();
        return ontSdk.getConnect().getContract(contractHash);
    }

    public boolean verifyClaim(String pubKey, String headerAndPayload, byte[] signature) throws Exception {
        Account account = new Account(false, Helper.hexToBytes(pubKey));
        boolean b = account.verifySignature(headerAndPayload.getBytes(), signature);
        return b;
    }

    public String getPk(String wif) {
        return Helper.toHexString(Account.getPrivateKeyFromWIF(wif));
    }

    public String signMessage(String message, String wif) throws Exception {
        OntSdk ontSdk = getOntSdk();
        Account account = new Account(Account.getPrivateKeyFromWIF(wif), ontSdk.getWalletMgr().getSignatureScheme());
        DataSignature sign = new DataSignature(SignatureScheme.SHA256WITHECDSA, account, message.getBytes());
        return Helper.toHexString(sign.signature());
    }
}
