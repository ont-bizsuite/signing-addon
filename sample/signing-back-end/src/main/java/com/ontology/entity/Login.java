package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_login")
@Data
public class Login {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String ontid;
    private String userName;
    private Integer state;
    private Date createTime;

}
