
package com.filab.open.search.service;

import com.filab.open.search.command.SearchRequest;
import com.filab.open.search.domain.Ssearch;

/**
 * 
 * 
 * @author Jeff
 * @version v 1.0, $Revision$
 * @since Jeff, 2010. 9. 2.
 */
public interface SearchService {

	public Ssearch getSearchData(SearchRequest request) throws Exception;
}
