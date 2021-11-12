package com.filab.open.search.client;

import com.filab.open.search.command.SearchRequest;
import com.filab.open.search.domain.Ssearch;

public interface SearchClient {

	public Ssearch execute(SearchRequest request) throws Exception;
}
