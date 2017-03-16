/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.extension.dsl;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import org.mule.functional.junit4.MuleArtifactFunctionalTestCase;
import org.mule.runtime.api.app.declaration.ArtifactDeclaration;
import org.mule.runtime.api.dsl.DslResolvingContext;
import org.mule.runtime.config.spring.dsl.api.XmlArtifactDeclarationLoader;
import org.mule.test.runner.ArtifactClassLoaderRunnerConfig;

import org.junit.Before;
import org.junit.Test;

/**
 * //TODO
 */
@ArtifactClassLoaderRunnerConfig(
  sharedRuntimeLibs = {"org.apache.derby:derby"},
  plugins = {
    "org.mule.modules:mule-module-sockets",
    "org.mule.modules:mule-module-http-ext",
    "org.mule.modules:mule-module-db",
    "org.mule.modules:mule-module-jms"},
  providedInclusions = "org.mule.modules:mule-module-sockets")
public class DeclarationLoaderTestCase extends MuleArtifactFunctionalTestCase {

  private DslResolvingContext dslContext;

  @Override
  protected String getConfigFile() {
    return "integration-multi-config-dsl-app.xml";
  }

  @Before
  public void setup() throws Exception {
    dslContext = DslResolvingContext.getDefault(muleContext.getExtensionManager().getExtensions());
  }

  @Test
  public void loader(){
    ArtifactDeclaration artifact = XmlArtifactDeclarationLoader.getDefault(dslContext).load("integration-multi-config-dsl-app.xml");

    assertThat(artifact, is(notNullValue()));

  }



}
