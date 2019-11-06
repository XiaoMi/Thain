/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.thread.pool;

import lombok.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

/**
 * Date 19-5-17 下午7:47
 * 线程池
 *
 * @author liangyongrui@xiaomi.com
 */
public class ThainThreadPool implements Executor {

    @NonNull
    private final ThreadPoolExecutor threadPoolExecutor;

    private ThainThreadPool(@NonNull String threadName, int corePoolSize, int maximumPoolSize, long keepAliveSecond) {
        threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveSecond,
                TimeUnit.SECONDS, new LinkedBlockingDeque<>(), runnable -> new Thread(runnable, threadName));
    }

    public static ThainThreadPool getInstance(@NonNull String threadName, int corePoolSize) {
        return new ThainThreadPool(threadName, corePoolSize, corePoolSize, 60);
    }

    @Override
    public void execute(@Nonnull Runnable command) {
        threadPoolExecutor.execute(command);
    }
}
