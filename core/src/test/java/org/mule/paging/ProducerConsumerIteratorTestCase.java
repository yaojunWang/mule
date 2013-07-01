/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.paging;

import org.mule.api.MuleException;
import org.mule.api.streaming.Consumer;
import org.mule.api.streaming.StreamingDelegate;
import org.mule.api.streaming.Producer;
import org.mule.streaming.ConsumerIterator;
import org.mule.streaming.ElementBasedPagingConsumer;
import org.mule.streaming.PagingDelegateProducer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

public class ProducerConsumerIteratorTestCase
{

    private static final int PAGE_SIZE = 100;
    private static final int TOP = 3000;

    private StreamingDelegate<String> delegate = new StreamingDelegate<String>()
    {

        long counter = 0;
        long numberOfBytes = 0;
        long numberOfMegabytes = 0;

        public List<String> getPage()
        {
            if (counter < TOP)
            {
                List<String> page = new ArrayList<String>(100);
                for (int i = 0; i < PAGE_SIZE; i++)
                {
                    counter++;
                    String value = RandomStringUtils.randomAlphabetic(5000);
                    numberOfBytes += value.length() * 2;
                    numberOfMegabytes = numberOfBytes / (1024 * 1024);
                    page.add(value);
                }

                return page;
            }

            return null;
        };

        public void close() throws MuleException
        {
        };
    };

    @Test
    public void iterateStreaming() throws Exception
    {
        Producer<String> producer = new PagingDelegateProducer<String>(this.delegate);
        Consumer<String> consumer = new ElementBasedPagingConsumer<String>(producer);

        ConsumerIterator<String> it = new ConsumerIterator<String>(consumer);

        int count = 0;
        while (it.hasNext())
        {
            it.next();
            count++;
        }

        Assert.assertEquals(count, TOP);
        it.close();
    }

}
