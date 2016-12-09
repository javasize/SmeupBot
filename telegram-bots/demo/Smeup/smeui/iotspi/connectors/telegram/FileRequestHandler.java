
/**
 *
 * Copyright (C) 2015 Roberto Dominguez Estrada and Juan Carlos Sedano Salas
 *
 * This material is provided "as is", with absolutely no warranty expressed
 * or implied. Any use is at your own risk.
 *
 */
package Smeup.smeui.iotspi.connectors.telegram;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;

import io.github.nixtabyte.telegram.jtelebot.client.HttpClientFactory;
import io.github.nixtabyte.telegram.jtelebot.client.HttpProxy;
import io.github.nixtabyte.telegram.jtelebot.client.RequestHandler;
import io.github.nixtabyte.telegram.jtelebot.exception.JsonParsingException;
import io.github.nixtabyte.telegram.jtelebot.exception.TelegramServerException;
import io.github.nixtabyte.telegram.jtelebot.request.TelegramRequest;
import io.github.nixtabyte.telegram.jtelebot.response.json.TelegramResponse;
/**
*
* This is the default request handler
*
* @since 0.0.1
*/
public class FileRequestHandler implements RequestHandler {

	// TODO This should be in a CommonConstants class
	private static final String URL_GET_FILE_TEMPLATE = "https://api.telegram.org/bot{0}/getFile?file_id={1}";
    private static final String URL_FILE_TEMPLATE = "https://api.telegram.org/file/bot{0}/{1}";

	private HttpClient httpClient;
	private HttpProxy httpProxy;
	private String token;

	/**
	 * <p>Constructor for DefaultRequestHandler.</p>
	 */
	public FileRequestHandler() {
		httpClient = HttpClientFactory.createHttpClient();
	}
	
	public FileRequestHandler(final String token,final HttpProxy httpProxy) {
		this(token);
		this.httpProxy = httpProxy;
	}

	/**
	 * <p>Constructor for DefaultRequestHandler.</p>
	 *
	 * @param token a {@link java.lang.String} object.
	 */
	public FileRequestHandler(final String token) {
		this();
		this.token = token;
	}

//	/** {@inheritDoc} 
//	 * @throws JsonParsingException 
//	 * @throws TelegramServerException */
//	@Override
//	public TelegramResponse<?> sendRequest(TelegramRequest telegramRequest) throws JsonParsingException, TelegramServerException {
//		TelegramResponse<?> telegramResponse = null;
//		final String response = callHttpService(telegramRequest);
//
//		telegramResponse = parseJsonResponse(response, telegramRequest
//				.getRequestType().getResultClass());
//
//		return telegramResponse;
//	}
//



	private String callHttpService(String file_id) throws TelegramServerException {
		final String url_get_file = MessageFormat.format(URL_GET_FILE_TEMPLATE, token, file_id);

		final HttpPost request = new HttpPost(url_get_file);
//		if (telegramRequest.getFile() != null) {
//			final MultipartEntityBuilder mpeb = MultipartEntityBuilder.create();
//			mpeb.addBinaryBody(telegramRequest.getFileType(),
//					telegramRequest.getFile());
//			for (BasicNameValuePair bnvp : telegramRequest.getParameters()) {
//				mpeb.addTextBody(bnvp.getName(), bnvp.getValue());
//			}
//
//			request.setEntity(mpeb.build());
//		} else {
//			request.setEntity(new UrlEncodedFormEntity(telegramRequest
//					.getParameters(), Consts.UTF_8));
//		}
		try {
			// PROXY Usage
			if (httpProxy != null) {
				HttpHost proxyHost = new HttpHost(httpProxy.getHost(),
						httpProxy.getPort(), httpProxy.getProtocol());
				RequestConfig config = RequestConfig.custom()
						.setProxy(proxyHost).build();
				request.setConfig(config);
			}
			
			final HttpResponse response = httpClient.execute(request);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer get_result = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				get_result.append(line);
			}

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new TelegramServerException(get_result.toString());
			}

			String get_result_string= get_result.toString();
			return get_result_string;
		} catch (IOException e) {
			throw new TelegramServerException(e);
		}

	}
	
//	   private TelegramResponse<?> parseJsonResponse(final String jsonResponse,
//	               final Class<?> resultTypeClass) throws JsonParsingException {
//	           try {
//	               final TelegramResponse<?> telegramResponse = (TelegramResponse<?>) MapperHandler.INSTANCE
//	                       .getObjectMapper().readValue(
//	                               jsonResponse,
//	                               MapperHandler.INSTANCE
//	                                       .getObjectMapper()
//	                                       .getTypeFactory()
//	                                       .constructParametricType(
//	                                               TelegramResponse.class,
//	                                               resultTypeClass));
//	               return telegramResponse;
//
//	           } catch (IOException e) {
//	               throw new JsonParsingException(e);
//	           }
//
//	       }


	   private String callHttpService(String file_id, String file_name) throws TelegramServerException {
	        final String url_get_file = MessageFormat.format(URL_FILE_TEMPLATE, token, file_name);

	        final HttpGet request = new HttpGet(url_get_file);
//	      if (telegramRequest.getFile() != null) {
//	          final MultipartEntityBuilder mpeb = MultipartEntityBuilder.create();
//	          mpeb.addBinaryBody(telegramRequest.getFileType(),
//	                  telegramRequest.getFile());
//	          for (BasicNameValuePair bnvp : telegramRequest.getParameters()) {
//	              mpeb.addTextBody(bnvp.getName(), bnvp.getValue());
//	          }
	//
//	          request.setEntity(mpeb.build());
//	      } else {
//	      }
	        try {
	            // PROXY Usage
//	            if (httpProxy != null) {
//	                HttpHost proxyHost = new HttpHost(httpProxy.getHost(),
//	                        httpProxy.getPort(), httpProxy.getProtocol());
//	                RequestConfig config = RequestConfig.custom()
//	                        .setProxy(proxyHost).build();
//	                request.setConfig(config);
//	            }
	            
	            final HttpResponse response = httpClient.execute(request);

	            BufferedReader reader = new BufferedReader(new InputStreamReader(
	                    response.getEntity().getContent()));

	            StringBuffer result = new StringBuffer();
	            String line = "";
	            while ((line = reader.readLine()) != null) {
	                result.append(line);
	            }

	            if (response.getStatusLine().getStatusCode() != 200) {
	                throw new TelegramServerException(result.toString());
	            }

	            return result.toString();

	        } catch (IOException e) {
	            throw new TelegramServerException(e);
	        }

	    }

//	// TODO This method should be implemented in a ResponseParser class
//	private TelegramResponse<?> parseJsonResponse(final String jsonResponse,
//			final Class<?> resultTypeClass) throws JsonParsingException {
//		try {
//			final TelegramResponse<?> telegramResponse = (TelegramResponse<?>) MapperHandler.INSTANCE
//					.getObjectMapper().readValue(
//							jsonResponse,
//							MapperHandler.INSTANCE
//									.getObjectMapper()
//									.getTypeFactory()
//									.constructParametricType(
//											TelegramResponse.class,
//											resultTypeClass));
//			LOG.trace(telegramResponse.toString());
//			return telegramResponse;
//
//		} catch (IOException e) {
//			throw new JsonParsingException(e);
//		}
//
//	}

	/**
	 * <p>Getter for the field <code>token</code>.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getToken() {
		return token;
	}

	/**
	 * <p>Setter for the field <code>token</code>.</p>
	 *
	 * @param token a {@link java.lang.String} object.
	 */
	public void setToken(String token) {
		this.token = token;
	}

    @Override
    public TelegramResponse<?> sendRequest(TelegramRequest aTelegramRequest)
                throws JsonParsingException, TelegramServerException
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String sendRequest(String file_id)
                throws JsonParsingException, TelegramServerException
    {
        // TODO Auto-generated method stub
        return callHttpService(file_id);
    }

    public String sendRequest(String file_id, String file_name)
                throws JsonParsingException, TelegramServerException
    {
        // TODO Auto-generated method stub
        return callHttpService(file_id, file_name);
    }

}
