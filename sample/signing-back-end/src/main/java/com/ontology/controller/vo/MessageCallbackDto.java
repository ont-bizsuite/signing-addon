package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;


@Data
public class MessageCallbackDto {
    @ApiModelProperty(name="id",value = "id",required = true)
    private String id;

    @ApiModelProperty(name="ontid",value = "ontid",required = true)
    private String ontid;
    @ApiModelProperty(name="address",value = "address",required = true)
    private String address;

    @ApiModelProperty(name="verified",value = "verified",required = true)
    private Boolean verified;

    @ApiModelProperty(name="extraData",value = "extraData",required = true)
    private Map<String,Object> extraData;

    @ApiModelProperty(name="hash",value = "hash",required = true)
    private String hash;
}
