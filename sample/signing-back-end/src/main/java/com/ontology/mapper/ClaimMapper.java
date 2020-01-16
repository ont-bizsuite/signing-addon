package com.ontology.mapper;

import com.ontology.entity.Claim;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;


@Component
public interface ClaimMapper extends Mapper<Claim> {
}
