package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AccessDto {
    @ApiModelProperty(name="token",value = "token",required = true)
    @NotBlank
    private String accessToken;
}
