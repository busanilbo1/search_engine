
package com.filab.open.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.filab.open.search.client.SearchClient;
import com.filab.open.search.command.SearchRequest;
import com.filab.open.search.domain.Ssearch;


@Service
public class SearchServiceImpl implements SearchService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private SearchClient client;

	@Autowired
	public void setClient(SearchClient client) {
		this.client = client;
	}

	public Ssearch getSearchData(SearchRequest request) throws Exception {

		if (logger.isDebugEnabled())
			logger.debug("request: " + request);

		return client.execute(request);
	}
	

}
