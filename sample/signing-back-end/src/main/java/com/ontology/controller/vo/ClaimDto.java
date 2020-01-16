package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ClaimDto {
    @ApiModelProperty(name="id",value = "id")
    private String id;
    @ApiModelProperty(name="dappName",value = "dappName")
    private String dappName;
    @ApiModelProperty(name="issueBy",value = "issueBy")
    private String dappIcon;
    @ApiModelProperty(name="issueBy",value = "issueBy")
    private String issueBy;
    @ApiModelProperty(name="callback",value = "callback")
    private String callback;
}
