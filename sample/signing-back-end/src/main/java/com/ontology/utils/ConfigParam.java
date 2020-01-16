package com.ontology.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("ConfigParam")
public class ConfigParam {

	/**
	 *  SDK param
	 */
	@Value("${service.restfulUrl}")
	public String RESTFUL_URL;

	@Value("${payer.addr}")
	public String PAYER_ADDRESS;

	@Value("${signing.server.url}")
	public String SIGNING_SERVER_URL;

	@Value("${local.server.url}")
	public String LOCAL_SERVER_URL;

	@Value("${payer.wif}")
	public String PAYER_WIF;

	@Value("${claim.issuer}")
	public String CLAIM_ISSUER;
}