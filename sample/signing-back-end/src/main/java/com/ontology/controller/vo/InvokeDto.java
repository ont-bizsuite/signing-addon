package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;


@Data
public class InvokeDto {
    @ApiModelProperty(name="type",value = "type")
    private String type;
    @ApiModelProperty(name="ontid",value = "ontid")
    private String ontid;
    @ApiModelProperty(name="address",value = "address")
    private String address;
    @ApiModelProperty(name="signedTx",value = "signedTx")
    private String signedTx;
    @ApiModelProperty(name="extraData",value = "extraData")
    private Map<String,Object> extraData;

}
