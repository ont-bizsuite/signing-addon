package com.ontology.mapper;

import com.ontology.entity.Login;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;


@Component
public interface LoginMapper extends Mapper<Login> {
}
