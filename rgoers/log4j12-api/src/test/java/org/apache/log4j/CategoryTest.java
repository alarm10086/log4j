/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.log4j;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ListAppender;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


import org.junit.Before;
import org.junit.Assert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;


/**
 * Tests of Category.
 */
public class CategoryTest {

    static ConfigurationFactory cf = new BasicConfigurationFactory();

    private ListAppender appender = new ListAppender("List");

    @BeforeClass
    public static void setupClass() {
        ConfigurationFactory.setConfigurationFactory(cf);
        LoggerContext ctx = (LoggerContext) org.apache.logging.log4j.LogManager.getContext();
        ctx.reconfigure();
    }

    @AfterClass
    public static void cleanupClass() {
        ConfigurationFactory.removeConfigurationFactory(cf);
    }

    /**
     * Tests Category.forcedLog.
     */
    @Test
    public void testForcedLog() {
        MockCategory category = new MockCategory("org.example.foo");
        category.setAdditivity(false);
        category.getLogger().addAppender(appender);
        category.info("Hello, World");
        int events = appender.getEvents().size();
        assertTrue("Number of events should be 1, was " + events, events == 1);
        appender.clear();
    }

    /**
     * Tests that the return type of getChainedPriority is Priority.
     *
     * @throws Exception thrown if Category.getChainedPriority can not be found.
     */
    @Test
    public void testGetChainedPriorityReturnType() throws Exception {
        Method method = Category.class.getMethod("getChainedPriority", (Class[]) null);
        assertTrue(method.getReturnType() == Priority.class);
    }

    /**
     * Tests l7dlog(Priority, String, Throwable).
     */
    @Test
    public void testL7dlog() {
        Logger logger = Logger.getLogger("org.example.foo");
        logger.setLevel(Level.ERROR);
        Priority debug = Level.DEBUG;
        logger.l7dlog(debug, "Hello, World", null);
        assertTrue(appender.getEvents().size() == 0);
    }

    /**
     * Tests l7dlog(Priority, String, Object[], Throwable).
     */
    @Test
    public void testL7dlog4Param() {
        Logger logger = Logger.getLogger("org.example.foo");
        logger.setLevel(Level.ERROR);
        Priority debug = Level.DEBUG;
        logger.l7dlog(debug, "Hello, World", new Object[0], null);
        assertTrue(appender.getEvents().size() == 0);
    }

    /**
     * Tests setPriority(Priority).
     *
     * @deprecated
     */
    @Test
    public void testSetPriority() {
        Logger logger = Logger.getLogger("org.example.foo");
        Priority debug = Level.DEBUG;
        logger.setPriority(debug);
    }

    /**
     * Derived category to check method signature of forcedLog.
     */
    private static class MockCategory extends Logger {
        /**
         * Create new instance of MockCategory.
         *
         * @param name category name
         */
        public MockCategory(final String name) {
            super(name);
        }

        /**
         * Request an info level message.
         *
         * @param msg message
         */
        public void info(final String msg) {
            Priority info = Level.INFO;
            forcedLog(MockCategory.class.toString(), info, msg, null);
        }
    }
}