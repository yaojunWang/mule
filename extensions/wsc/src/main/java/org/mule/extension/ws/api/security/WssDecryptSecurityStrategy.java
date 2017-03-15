/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.ws.api.security;

import static java.util.Optional.of;
import static org.apache.ws.security.WSPasswordCallback.DECRYPT;
import static org.apache.ws.security.handler.WSHandlerConstants.DEC_PROP_REF_ID;
import static org.apache.ws.security.handler.WSHandlerConstants.ENCRYPT;

import java.util.Map;
import java.util.Optional;

import org.mule.extension.ws.api.security.config.WssKeyStoreConfiguration;
import org.mule.runtime.extension.api.annotation.param.Parameter;
import org.mule.services.soap.api.security.DecryptSecurityStrategy;
import org.mule.services.soap.api.security.SecurityStrategy;

import com.google.common.collect.ImmutableMap;

/**
 * Decrypts an encrypted SOAP response, using the private key of the key-store in the provided TLS context.
 *
 * @since 4.0
 */
public class WssDecryptSecurityStrategy implements SecurityStrategyAdapter
{

  private static final String WS_DECRYPT_PROPERTIES_KEY = "decryptProperties";

  /**
   * The keystore to use when decrypting the message.
   */
  @Parameter
  private WssKeyStoreConfiguration keyStoreConfiguration;

  @Override
  public SecurityStrategyType securityType() {
    return INCOMING;
  }

  @Override
  public String securityAction() {
    return ENCRYPT;
  }

  @Override
  public Optional<WSPasswordCallbackHandler> buildPasswordCallbackHandler() {
    return of(new WSPasswordCallbackHandler(DECRYPT, cb -> cb.setPassword(keyStoreConfiguration.getKeyPassword())));
  }

  @Override
  public Map<String, Object> buildSecurityProperties() {
    return ImmutableMap.<String, Object>builder()
        .put(DEC_PROP_REF_ID, WS_DECRYPT_PROPERTIES_KEY)
        .put(WS_DECRYPT_PROPERTIES_KEY, keyStoreConfiguration.getConfigurationProperties())
        .build();
  }

  @Override
  public SecurityStrategy getSecurityStrategy()
  {
    new WssKeyStoreConfiguration()
    return new DecryptSecurityStrategy();
  }
}
