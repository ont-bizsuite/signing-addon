package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CentRegisterDto {
    @ApiModelProperty(name="userName",value = "userName",required = true)
    @NotBlank
    private String userName;
    @ApiModelProperty(name="password",value = "password",required = true)
    @NotBlank
    private String password;

}
