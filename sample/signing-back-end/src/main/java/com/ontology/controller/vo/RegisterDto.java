package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RegisterDto {
    @ApiModelProperty(name="userName",value = "userName",required = true)
    private String userName;

}
