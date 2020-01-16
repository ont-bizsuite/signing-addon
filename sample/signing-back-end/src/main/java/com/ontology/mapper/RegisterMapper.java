package com.ontology.mapper;

import com.ontology.entity.Register;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


@Component
public interface RegisterMapper extends Mapper<Register> {
    List<Register> selectByOntidAndUserName(String ontid, String userName);

}
