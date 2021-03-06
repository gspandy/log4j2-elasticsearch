package org.appenders.log4j2.elasticsearch;

/*-
 * #%L
 * log4j2-elasticsearch
 * %%
 * Copyright (C) 2018 Rafal Foltynski
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppenderRefFailoverPolicyTest {

    @Test
    public void deliversToAppenderRef() {

        // given
        Appender appender = mock(Appender.class);
        when(appender.isStarted()).thenReturn(true);
        Configuration configuration = mock(Configuration.class);
        String testAppenderRef = "testAppenderRef";
        when(configuration.getAppender(testAppenderRef)).thenReturn(appender);

        FailoverPolicy<String> failoverPolicy = createTestFailoverPolicy(testAppenderRef, configuration);

        String failedMessage = "test failed message";

        // when
        failoverPolicy.deliver(failedMessage);

        // then
        verify(appender, times(1)).append(any(LogEvent.class));
    }

    @Test
    public void resolvesAppenderRefOnlyOnce() {

        // given
        Appender appender = mock(Appender.class);
        when(appender.isStarted()).thenReturn(true);
        Configuration configuration = mock(Configuration.class);
        String testAppenderRef = "testAppenderRef";
        when(configuration.getAppender(testAppenderRef)).thenReturn(appender);

        FailoverPolicy<String> failoverPolicy = createTestFailoverPolicy(testAppenderRef, configuration);

        String failedMessage = "test failed message";

        // when
        failoverPolicy.deliver(failedMessage);
        failoverPolicy.deliver(failedMessage);

        // then
        verify(configuration, times(1)).getAppender(anyString());
        verify(appender, times(2)).append(any(LogEvent.class));
    }

    @Test(expected = ConfigurationException.class)
    public void throwsExceptionOnUnresolvedAppender() {

        // given
        Appender appender = mock(Appender.class);
        when(appender.isStarted()).thenReturn(true);
        Configuration configuration = mock(Configuration.class);
        String testAppenderRef = "testAppenderRef";
        when(configuration.getAppender(testAppenderRef)).thenReturn(null);

        FailoverPolicy<String> failoverPolicy = createTestFailoverPolicy(testAppenderRef, configuration);

        String failedMessage = "test failed message";

        // when
        failoverPolicy.deliver(failedMessage);

    }
    public static  FailoverPolicy<String> createTestFailoverPolicy(String testAppenderRef, Configuration configuration) {
        AppenderRefFailoverPolicy.Builder builder = AppenderRefFailoverPolicy.newBuilder();
        builder.withAppenderRef(AppenderRef.createAppenderRef(
                testAppenderRef, Level.ALL, null));
        builder.withConfiguration(configuration);
        return builder.build();
    }
}
