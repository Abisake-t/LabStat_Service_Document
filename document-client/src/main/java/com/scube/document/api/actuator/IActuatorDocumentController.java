package com.scube.document.api.actuator;

import com.scube.client.ServiceUrlConstant;
import com.scube.client.annotation.AddToServiceClient;
import com.scube.client.annotation.GenerateHttpExchangeProxy;
import com.scube.client.annotation.HttpExchangeWebClient;
import org.springframework.web.service.annotation.GetExchange;

import java.util.Map;

@HttpExchangeWebClient(ServiceUrlConstant.DOCUMENT_SERVICE)
@AddToServiceClient(ServiceUrlConstant.DOCUMENT_SERVICE)
@GenerateHttpExchangeProxy
public interface IActuatorDocumentController {
    @GetExchange("/actuator/health")
    ActuatorHealth health();

    @GetExchange("/actuator/health/**")
    Object healthPath();

    @GetExchange("/actuator")
    Map<String, Map<String, Link>> links();
}