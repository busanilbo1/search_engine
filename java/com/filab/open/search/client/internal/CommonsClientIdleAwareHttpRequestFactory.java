/*
��õ�߰輭�� �߰��� V1.3

Copyright �� 2014 kt.corp All rights reserved

This is a proprietary software of kt.corp, and you may not use this file except in
compliance with license agreement with kt corp.Any redistribution or use of this software with 
or without modification shall be strictly prohibited with out prior written approval of kt corp,
and the copyright notice above does not evidence any actual ot
intended publication of such software.

*/
package com.filab.open.search.client.internal;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.CommonsClientHttpRequestFactory;

/**
 * 
 * 
 * @author Jeff
 * @version v 1.0, $Revision$
 * @since Jeff, 2010. 11. 29.
 */
public class CommonsClientIdleAwareHttpRequestFactory extends CommonsClientHttpRequestFactory implements
		InitializingBean {

	private IdleConnectionTimeoutThread timeoutThread;

	private long timeoutInterval = 1000L;

	private long connectionTimeout = 3000L;

	public CommonsClientIdleAwareHttpRequestFactory() {
		super();
		
	}

	public CommonsClientIdleAwareHttpRequestFactory(HttpClient httpClient) {
		
		super(httpClient);
		
	}

	public void setTimeoutInterval(long timeoutInterval) {
		this.timeoutInterval = timeoutInterval;
	}

	public void setConnectionTimeout(long connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public void afterPropertiesSet() {
		IdleConnectionTimeoutThread thread = new IdleConnectionTimeoutThread();
		thread.setTimeoutInterval(timeoutInterval);
		thread.setConnectionTimeout(connectionTimeout);
		thread.addConnectionManager(getHttpClient().getHttpConnectionManager());
		thread.start();
		this.timeoutThread = thread;
	}

	@Override
	public void destroy() {
		this.timeoutThread.shutdown();
		super.destroy();
	}


}
