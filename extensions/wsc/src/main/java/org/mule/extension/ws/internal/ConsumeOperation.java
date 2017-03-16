/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.ws.internal;


import static org.apache.commons.lang.StringUtils.isBlank;

import org.mule.extension.ws.api.SoapMessageBuilder;
import org.mule.extension.ws.api.WscAttributes;
import org.mule.extension.ws.internal.metadata.ConsumeOutputResolver;
import org.mule.extension.ws.internal.metadata.MessageBuilderResolver;
import org.mule.extension.ws.internal.metadata.OperationKeysResolver;
import org.mule.extension.ws.internal.metadata.WscAttributesResolver;
import org.mule.runtime.extension.api.annotation.OnException;
import org.mule.runtime.extension.api.annotation.error.Throws;
import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.metadata.TypeResolver;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.annotation.param.NullSafe;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.runtime.operation.Result;
import org.mule.services.soap.api.client.SoapClient;
import org.mule.services.soap.api.message.SoapRequest;
import org.mule.services.soap.api.message.SoapRequestBuilder;
import org.mule.services.soap.api.message.SoapResponse;
import org.mule.services.soap.exception.SoapFaultException;

/**
 * The only {@link WebServiceConsumer} operation. the {@link ConsumeOperation} consumes an operation of the connected web service
 * and returns it's response.
 * <p>
 * The consume operation expects an XML body and a set of headers and attachments if required.
 * <p>
 *
 * @since 4.0
 */
public class ConsumeOperation {

  /**
   * Consumes an operation from a SOAP Web Service.
   *
   * @param connection the connection resolved to execute the operation.
   * @param operation  the name of the web service operation that aims to invoke.
   * @param message    the constructed SOAP message to perform the request.
   */
  @OnException(WscExceptionEnricher.class)
  @Throws(ConsumeErrorTypeProvider.class)
  @OutputResolver(output = ConsumeOutputResolver.class, attributes = WscAttributesResolver.class)
  public Result<Object, WscAttributes> consume(@Connection SoapClient connection,
                                               @MetadataKeyId(OperationKeysResolver.class) String operation,
                                               //TODO MULE-11235
                                               @NullSafe @Optional @TypeResolver(MessageBuilderResolver.class) SoapMessageBuilder message)
      throws SoapFaultException {
    SoapRequestBuilder requestBuilder = SoapRequest.builder();
    message.getAttachments().values().forEach(requestBuilder::withAttachment);
    requestBuilder.withOperation(operation);
    requestBuilder.withSoapHeaders(message.getHeaders());

    if (!isBlank(message.getBody())) {
      requestBuilder.withContent(message.getBody());
    }

    SoapResponse response = connection.consume(requestBuilder.build());
    return Result.<Object, WscAttributes>builder().output(response.getContent())
        .attributes(new WscAttributes(response.getSoapHeaders(), response.getTransportHeaders()))
        .build();
  }
}
