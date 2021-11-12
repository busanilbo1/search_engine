
package com.filab.open.search.client;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownServiceException;
import java.util.Map;

import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.XmlMappingException;
import org.springframework.stereotype.Service;

import com.filab.open.search.command.SearchRequest;
import com.filab.open.search.domain.Ssearch;

@Service
public class SearchHttpClient implements SearchClient {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private Unmarshaller unmarshaller;

	private ClientHttpRequestFactory factory;

	private String uriEncoding = "UTF-8";

	private Map<Class<SearchRequest>, String> targetUrls;

	@Autowired
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	@Autowired
	public void setFactory(ClientHttpRequestFactory factory) {
		this.factory = factory;
	}

	public void setUriEncoding(String uriEncoding) {
		this.uriEncoding = uriEncoding;
	}

	public void setTargetUrls(Map<Class<SearchRequest>, String> targetUrls) {
		this.targetUrls = targetUrls;
	}

	public Ssearch execute(SearchRequest request) throws Exception {

		ClientHttpRequest httpRequest = null;
		ClientHttpResponse httpResponse = null;
		Ssearch response = null;
		long startTime = System.currentTimeMillis();
		try {
			
			
			
			httpRequest = createRequest(request);			
			httpResponse = executeRequest(httpRequest);
			response = unmarshalSearchData(httpResponse);
		}
		finally {
			if (httpResponse != null)
				httpResponse.close();
		}
		long elapsedTime = System.currentTimeMillis() - startTime;

		if (logger.isInfoEnabled()) {
			logger.info(createSearchLog(request, response, elapsedTime));
		}

		return response;
	}

	private ClientHttpRequest createRequest(SearchRequest request) throws Exception {
		String url = getTargetUrl(request);

		if (url == null)
			throw new NullPointerException("Server URL is null. Check configuration file.");

		URI uri = null;
		try {
			uri = new URI(url + request.toQueryString(uriEncoding));
			System.out.println("uri--->"+uri);
			
		}
		catch (UnsupportedEncodingException e) {
			throw new UnsupportedEncodingException("To encode query string of Request " + request.getParameterString()
					+ " fails. Cause: " + e.getMessage());
		}
		catch (URISyntaxException e) {
			throw new URISyntaxException(url, "Server URL is incorrect. Check configuration file. Cause: "
					+ e.getMessage());
		}

		return factory.createRequest(uri, HttpMethod.GET);
	}

	private ClientHttpResponse executeRequest(ClientHttpRequest request) throws Exception {
		ClientHttpResponse response = request.execute();
		HttpStatus status = response.getStatusCode();
		
		if (logger.isDebugEnabled())
			logger.debug("status: " + status);

		if (!HttpStatus.OK.equals(status)) {
			System.out.println("HTTP status code[" + status + "] has received.");
		}

		return response;
	}

	private Ssearch unmarshalSearchData(ClientHttpResponse response) throws Exception {
		Ssearch ssearch = null;
		System.out.println(response.getHeaders());
		//InputStream is = response.getBody();
		
		InputStreamReader in = new InputStreamReader((InputStream)response.getBody(), "UTF-8");
	
		if (in == null) {
			throw new NullPointerException("Any data didn't received from search server. Check server.");
		}

		try {
			
			ssearch = (Ssearch) unmarshaller.unmarshal(new StreamSource(in));
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if (in != null)
				in.close();
		}

		if (logger.isDebugEnabled())
			logger.debug("ssearch: " + ssearch);

		return ssearch;
	}

	private String createSearchLog(SearchRequest request, Ssearch response, long elapsedTime) {
		Writer stringWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(stringWriter);

		printRequest(request, elapsedTime, writer);
		printResponse(response, writer);

		writer.flush();

		return stringWriter.toString();
	}

	private void printRequest(SearchRequest request, long elapsedTime, PrintWriter writer) {
		writer.println("Request parameter: " + request.getParameterString());
		writer.println("Request elapsedTime: " + elapsedTime + " ms");
		request.log(writer);
	}

	private void printResponse(Ssearch response, PrintWriter writer) {
		if (response != null)
			response.log(writer);
		else
			writer.println("Response from Server is null.");
	}

	private String getTargetUrl(SearchRequest request) {
		return targetUrls.get(request.getClass());
	}
}
