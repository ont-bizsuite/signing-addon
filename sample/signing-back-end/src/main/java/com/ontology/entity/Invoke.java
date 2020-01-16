package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tbl_invoke")
@Data
public class Invoke {
    @Id
    @GeneratedValue(generator = "JDBC")
    private String id;

    private String params;
    private String ontid;
    private Integer ontidIndex;
    private String txHash;
    private Integer state;
}
