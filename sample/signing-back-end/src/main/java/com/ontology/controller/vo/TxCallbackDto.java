package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TxCallbackDto {
    @ApiModelProperty(name="id",value = "id",required = true)
    private String id;

    @ApiModelProperty(name="signedTx",value = "signedTx",required = true)
    private String signedTx;
}
