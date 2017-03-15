/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.security.oauth.processor;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mule.security.oauth.OAuthProperties.BASE_EVENT_STATE_TEMPLATE;
import static org.mule.security.oauth.processor.BaseOAuth2AuthorizeMessageProcessor.CUSTOM_SUFFIX_PROPERTY;
import static org.mule.security.oauth.processor.OAuth2FetchAccessTokenProcessorWithCustomSuffixTestCase.STATE_AFTER_SEPARATOR;
import static org.mule.security.oauth.processor.OAuth2FetchAccessTokenProcessorWithCustomSuffixTestCase.STATE_BEFORE_SEPARATOR;

import org.mule.api.MuleContext;
import org.mule.api.MuleEvent;
import org.mule.api.callback.HttpCallback;
import org.mule.security.oauth.OAuth2Adapter;
import org.mule.security.oauth.OAuth2Manager;
import org.mule.tck.junit4.rule.SystemProperty;
;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class OAuth2AuthorizeMessageProcessorWithCustomSuffixTestCase
{

    @Rule
    public SystemProperty customSuffixProperty = new SystemProperty(CUSTOM_SUFFIX_PROPERTY, ",");

    private static String CUSTOM_EVENT_STATE_TEMPLATE_SUFFIX = "__";

    private AuthorizeMessageProcessorOverrideSuffix authorizeMessageProcessorOverrideSuffix = new AuthorizeMessageProcessorOverrideSuffix();
    private AuthorizeMessageProcessor authorizeMessageProcessorDefault = new AuthorizeMessageProcessor();
    private MuleEvent event = mock (MuleEvent.class, RETURNS_DEEP_STUBS);
    private OAuth2Manager manager = mock(OAuth2Manager.class, RETURNS_DEEP_STUBS);

    @Test
    public void testOverrideTemplateSuffixThroughImplementedMethod() throws Exception
    {
        String customSuffix = authorizeMessageProcessorOverrideSuffix.getSuffix();
        assertThat(customSuffix, is(CUSTOM_EVENT_STATE_TEMPLATE_SUFFIX));
    }

    @Test
    public void testOverrideTemplateSuffixThroughSystemProperty() throws Exception
    {
        String customSuffix = authorizeMessageProcessorDefault.getSuffix();
        assertThat(customSuffix, is(customSuffixProperty.getValue()));
    }

    @Test
    public void testSetState() throws Exception
    {
        final String expectedResult = format(BASE_EVENT_STATE_TEMPLATE + "%s%s", STATE_BEFORE_SEPARATOR, customSuffixProperty.getValue(), STATE_AFTER_SEPARATOR);
        final String[] processedState = {null};
        setMocks();
        doAnswer(new Answer()
        {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable
            {
                Map<String, String> map = (Map) invocation.getArguments()[0];
                processedState[0] = map.get("state");
                return null;
            }
        }).when(manager).buildAuthorizeUrl(any(Map.class), any(String.class), any(String.class));
        authorizeMessageProcessorDefault.doProcess(event);
        assertThat(processedState[0], is(expectedResult));
    }

    private void setMocks ()
    {
        HttpCallback callback = mock(HttpCallback.class, RETURNS_DEEP_STUBS);
        MuleContext context= mock(MuleContext.class, RETURNS_DEEP_STUBS);
        when(event.getId()).thenReturn(STATE_BEFORE_SEPARATOR);
        when(context.getExpressionManager().parse(STATE_AFTER_SEPARATOR, event.getMessage())).thenReturn(STATE_AFTER_SEPARATOR);
        authorizeMessageProcessorDefault.setState(STATE_AFTER_SEPARATOR);
        authorizeMessageProcessorDefault.setOauthCallback(callback);
        authorizeMessageProcessorDefault.setMuleContext(context);
        authorizeMessageProcessorDefault.setModuleObject(manager);
    }

    private class AuthorizeMessageProcessorOverrideSuffix extends
            BaseOAuth2AuthorizeMessageProcessor<OAuth2Manager<OAuth2Adapter>>
    {


        @Override
        protected Class<OAuth2Manager<OAuth2Adapter>> getOAuthManagerClass()
        {
            return null;
        }

        @Override
        protected String getSuffix()
        {
            return CUSTOM_EVENT_STATE_TEMPLATE_SUFFIX;
        }

        @Override
        protected String getAuthCodeRegex()
        {
            return null;
        }

    }

    private class AuthorizeMessageProcessor extends
            BaseOAuth2AuthorizeMessageProcessor<OAuth2Manager<OAuth2Adapter>>
    {

        @Override
        protected Class<OAuth2Manager<OAuth2Adapter>> getOAuthManagerClass()
        {
            return null;
        }

        @Override
        protected String getAuthCodeRegex()
        {
            return null;
        }

    }

}
