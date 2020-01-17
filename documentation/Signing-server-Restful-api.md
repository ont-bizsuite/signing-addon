# Signing-server-Restful-api

[TOC]

## signing-server 接口规范：

### 验证并获取二维码参数

```text
url：/api/v2/ontid/verify
method：POST
```

- 请求：

```json
{
	"id": "12345678",
	"domain": "hello.app.ont",
	"action": "",
	"signature":""
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| id   | String | app方提供的id，用于标识唯一的交易。OntAuth会根据id从app方获取交易参数，signing-server会带上该id回调app    |
|domain|String|app对应的域名|
|action|String|注册的动作|
|signature|String|``domain``对应的ontid对``action``的签名|

- 响应：

```json
{
	"action": "action",
	"version": "v1",
	"error": 0,
	"desc": "SUCCESS",
	"result": {
		"dataUrl": "http://18.141.44.15:7878/api/v1/app/invoke/params/0796e069-00d3-49dc-b05c-9006c86d3ff2",
		"chainNet": "Testnet",
		"ons": "hello.app.ont",
		"signature": "",
		"expire": 1579231126,
		"callbackUrl": "https://sign.saas.ont.io/api/v2/ontid/invoke",
		"id": "af2ab01a-a767-4da1-acc2-2d601fb8f008",
		"type": "ontid",
		"version": "v2.0.0"
	}
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 动作标志                      |
| version    | String | 版本号                        |
| error       | int    | 错误码                        |
| desc        | String | 成功为SUCCESS，失败为错误描述 |
| result     | Map | 成功返回二维码参数，失败返回""     |
| dataUrl     | String | OntAuth获取交易参数的地址（接口）     |
| chainNet     | String | 网络标识     |
| ons     | String | app域名，同``domain``     |
| signature     | String | ``ons``对应的ontid（app的ontid）对交易参数的签名。signing-server不会返回该值，需要app自行签名并补充     |
| expire     | Long | 二维码过期时间，单位秒     |
| callbackUrl     | String | OntAuth签名后的回调地址     |
| id     | String | 二维码的唯一标识     |
| type     | String | 签名账户的类型     |
| version     | String | 二维码版本号     |


### 验证OntAuth签名并回调给app方(OntAuth调用)

```text
url：/api/v2/ontid/invoke
method：POST
```

- 请求：

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

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
| type   | String | 签名的账户类型    |
|ontid|String|若``type``为``ontid``,则为签名的ontid|
|address|String|若``type``为``address``,则为签名的address|
|signedTx|String|签名后的交易hex|
|extraData|Map|额外数据|
|id|String|二维码id|

- 响应：

```json
{
	"action": "action",
	"version": "v1",
	"error": 0,
	"desc": "SUCCESS",
	"result": "SUCCESS"
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 动作标志                      |
| version    | String | 版本号                        |
| error       | int    | 错误码                        |
| desc        | String | 成功为SUCCESS，失败为错误描述 |
| result     | String | 成功返回"SUCCESS"，失败返回""     |


### signing-server回调app告知验签结果

- 请求：
```text
method：POST
```
```json
{
	"ontid": "did:ont:xxx",
	"address": "xxx",
	"hash": "",
	"id": "12345678",
	"verified": true,
	"extraData": {
		"id": "10ba038e-48da-487b-96e8-8d3b99b6d18a"
	}
}
```

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
|ontid|String|签名的ontid|
|address|String|签名的address|
| hash   | String | 上链交易成功的hash    |
|id|String|app方提供的id，请求的唯一标识|
|verified|boolean|验签是否通过|
|extraData|Map|额外数据|
|id|String|二维码id|


### 查询二维码的扫码结果

```text
url：/api/v2/ontid/result/{id}
method：GET
```

- 请求：

| Field_Name | Type   | Description |
|:-----------|:-------|:------------|
|id|String|二维码id|

- 响应：

```json
{
	"action": "action",
	"version": "v1",
	"error": 0,
	"desc": "SUCCESS",
	"result": {
	"action": "action",
	"id": "10ba038e-48da-487b-96e8-8d3b99b6d18a",
	"ontid": "did:ont:xxx",
	"success": "1"
	}
}
```

| Field_Name | Type   | Description                   |
|:-----------|:-------|:------------------------------|
| action     | String | 动作标志                      |
| version    | String | 版本号                        |
| error       | int    | 错误码                        |
| desc        | String | 成功为SUCCESS，失败为错误描述 |
| result     | Map | 成功返回，失败返回""     |
| action        | String | 二维码动作 |
| id        | String | 二维码id |
| ontid        | String | 扫码签名者ontid |
| success        | String | 验签成功返回"1",验签失败"0",未验签返回null |
