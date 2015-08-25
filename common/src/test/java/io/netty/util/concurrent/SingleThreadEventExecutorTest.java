/*
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.util.concurrent;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class SingleThreadEventExecutorTest {

    @Test
    public void testThreadDetails() {
        final AtomicReference<Thread> threadRef = new AtomicReference<Thread>();
        SingleThreadEventExecutor executor = new SingleThreadEventExecutor(
                null, new DefaultThreadFactory("test"), false) {
            @Override
            protected void run() {
                threadRef.set(Thread.currentThread());
                while (!confirmShutdown()) {
                    Runnable task = takeTask();
                    if (task != null) {
                        task.run();
                    }
                }
            }
        };
        ThreadDetails threadDetails = executor.threadDetails();
        Assert.assertSame(threadDetails, executor.threadDetails());

        Thread thread = threadRef.get();
        Assert.assertEquals(thread.getId(), threadDetails.id());
        Assert.assertEquals(thread.getName(), threadDetails.name());
        Assert.assertEquals(thread.getPriority(), threadDetails.priority());
        Assert.assertEquals(thread.getState(), threadDetails.state());
        Assert.assertEquals(thread.isAlive(), threadDetails.isAlive());
        Assert.assertEquals(thread.isDaemon(), threadDetails.isDaemon());
        Assert.assertEquals(thread.isInterrupted(), threadDetails.isInterrupted());
        Assert.assertTrue(threadDetails.stackTrace().length > 0);
        executor.shutdownGracefully();
    }
}
