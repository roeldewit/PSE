package com.pse.fotoz.payments;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

/**
 *
 * @author René
 */
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {
//        //private static final Logger log = LoggerFactory.getLogger(LoggingRequestInterceptor.class);
//
//
//
//    private void log(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
//        Logger.getLogger(LoggingRequestInterceptor.class.getName())
//                .log(Level.SEVERE, request.toString());
//              Logger.getLogger(LoggingRequestInterceptor.class.getName())
//                .log(Level.SEVERE, new String(body, "utf-8"));
//              
//              
//        
//    }
//
//    @Override
//    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution chre) throws IOException {
//          ClientHttpResponse response = chre.execute(request, body);
//
//        log(request,body,response);
//
//        return response;
//    }
    
    
    
     final static org.slf4j.Logger logger = LoggerFactory.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        logger.error("===========================request begin================================================");

        logger.error("URI : " + request.getURI());
        logger.error("Method : " + request.getMethod());
        logger.error("Request Body : " + new String(body, "UTF-8"));
        logger.error("==========================request end================================================");
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {
        StringBuilder inputStringBuilder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
        String line = bufferedReader.readLine();
        while (line != null) {
            inputStringBuilder.append(line);
            inputStringBuilder.append('\n');
            line = bufferedReader.readLine();
        }
        logger.error("============================response begin==========================================");
        logger.error("status code: " + response.getStatusCode());
        logger.error("status text: " + response.getStatusText());
        logger.error("Response Body : " + inputStringBuilder.toString());
        logger.error("=======================response end=================================================");
    }

}
