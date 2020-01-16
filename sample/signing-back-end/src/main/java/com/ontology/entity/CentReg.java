package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_cent_reg")
@Data
public class CentReg {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String userName;
    private String password;
    private String ontid;
    private String owner;
    private Date createTime;

}
