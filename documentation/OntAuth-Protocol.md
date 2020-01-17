# OntAuth-Protocol

[TOC]

## OntAuth 协议规范：


### 协议规范

#### QRcode 二维码规范
```swift
{
    "version": "v1.0.0",
    "id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
    "ons": "ons domain", // ONS 域名
    "signature": "ons signature" // ONS 对 dataUrl 接口数据的签名
    "type": "ontid/address", // 标记签名的是 ONTID 还是 钱包，固定为 "ontid" 或 "address"
    "ontid": "did:ont:xxx", // 指定扫码操作的 ONTID（可不指定）
    "address": "xxx", // 指定扫码操作的钱包（可不指定）
    "dataUrl": "https://xxx", // 扫码展示及操作的数据参数（即签名的 body）
    "callbackUrl": "https://xxx", // 签名后的回调接口
    "expire": 1546415363, // 二维码过期时间
    "chainNet": "Testnet" // 所属网络
}
```

#### DataUrl 接口规范（body）
```source-json
{
    "action": "action name", // signMessage、signTransaction、getClaim、authorizeClaim
    "params": {
        "contractHash": "0000000000000000000000000000000000000000",
        "functions": [{
            "operation": "method name",
            "args": [{ // 根据 action 的不同，此参数列表也不同
                "name": "arg0",
                "value": "String:did:ont:AR9NDnK3iMSZodbENnt7eX5TJ2s27fnHra", // 若 value 为 "String:%ontid"，则替换 %ontid 为 ONTAuth 用户所选的 ONTID
                }, {
                "name": "arg1",
                "value": "Address:AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ", // 若 value 为 "Address:%address"，则替换 %address 为 ONTAuth 用户所选的 ONTID 或钱包的地址
                }, {
                "name": "arg2",
                "value": 100
                }, {
                "name": "arg3",
                "value": "String:helloworld"
                }, { // 该字段可选，看三方APP是否需要验证回调数据，合约（contractHash）是否支持
                "name": "extraDataHash", // 此 name 固定
                "value": "Hash:%hash" // 此 value 固定（构造交易回填为 CallbackUrl 里面 “extraData” 字段的哈希值（Sha256 两次））
            }]
        }],
        "payer": "AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ", // 若 payer 为 %address 先替换 %address 为 ONTID 或钱包地址。若 payer 与 ONTID 或钱包地址一致，ONTID 或钱包签名；如果不一致，ONTID 或钱包不签名。
        "gasLimit": 20000,
        "gasPrice": 500
    }
}
```

#### CallbackUrl 接口规范（回调）
```source-json
{
    "type": "ontid/address", // QRcode 二维码里的 "type" 字段
    "ontid": "did:ont:xxx", // 哪个 ONTID 签的名
    "address": "xxx", // 哪个钱包签的名
    "signedTx": "hexstring", // 签名交易的 HexString
    "extraData": { // 回调接口需要的其他数据
        "id": "10ba038e-48da-487b-96e8-8d3b99b6d18a", // DataUrl 接口里的 ”id“ 字段 
        "publickey": "xxx", // 若 ”type“ = "address", 需要回传公钥，否则传空
        "key": "value", // 其他需要回传的参数
        "key": "value"
        //...
    }
}
```


### 示例

#### 1、Sign Message（签名消息）

**QRcode:**
```json
{
    "version": "v1.0.0",
    "id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
    "ons": "ons domain",
    "signature": "ons signature",
    "type": "ontid",
    "ontid": "did:ont:xxx",
    "address": "",
    "dataUrl": "https://xxx",
    "callbackUrl": "https://xxx",
    "expire": 1546415363,
    "chainNet": {
        "chain": "Testnet",
        "node": "http://polaris1.ont.io:20336",
        "payer": "AT2PfiQZcPu5JgDTnyRVqBCHVtjueXHToa"
     }
}
```

**DataUrl:**
```json
{
    "action": "signMessage",
    "params": {
        "contractHash": "0000000000000000000000000000000000000000",
        "functions": [{
            "operation": "signMessage",
            "args": [{
                "name": "message",
                "value": "String:helloworld",
            }]
        }],
        "payer": "AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ",
        "gasLimit": 20000,
        "gasPrice": 500
    }
}
```

**CallbackUrl:**
```json
{
    "type": "ontid",
    "ontid": "did:ont:xxx",
    "address": "",
    "signedTx": "hexstring",
    "extraData": {
        "id": "10ba038e-48da-487b-96e8-8d3b99b6d18a"
    }
}
```

#### 2、Sign Transaction（签名交易）

**QRCode:**
```json
{
    "version": "v1.0.0",
    "id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
    "ons": "ons domain",
    "signature": "ons signature",
    "type": "address",
    "ontid": "",
    "address": "xxx",
    "dataUrl": "https://xxx",
    "callbackUrl": "https://xxx",
    "expire": 1546415363,
    "chainNet": {
        "chain": "Testnet",
        "node": "http://polaris1.ont.io:20336",
        "payer": "AT2PfiQZcPu5JgDTnyRVqBCHVtjueXHToa"
     }
}
```

**DataUrl:**
```json
{
    "action": "signTransaction",
    "params": {
        "contractHash": "16edbe366d1337eb510c2ff61099424c94aeef02",
        "functions": [{
            "operation": "transfer",
            "args": [{
                "name": "from",
                "value": "Address:%address"
                }, {
                "name": "to",
                "value": "Address:AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ"
                }, {
                "name": "amount",
                "value": 100
            }]
        }],
        "payer": "AUr5QUfeBADq6BMY6Tp5yuMsUNGpsD7nLZ",
        "gasLimit": 20000,
        "gasPrice": 500
    }
}
```

**CallbackUrl:**
```json
{
    "type": "address",
    "ontid": "",
    "address": "xxx",
    "signedTx": "hexstring",
    "extraData": {
        "id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
        "publickey": "xxx"
    }
}
```