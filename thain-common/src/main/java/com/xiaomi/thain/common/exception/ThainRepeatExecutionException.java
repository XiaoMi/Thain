/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.common.exception;

import lombok.NonNull;

/**
 * Date 19-5-17 下午1:50
 *
 * @author liangyongrui@xiaomi.com
 */
public class ThainRepeatExecutionException extends Exception {
    public ThainRepeatExecutionException(@NonNull String message) {
        super(message);
    }

    public ThainRepeatExecutionException() {
    }

    public ThainRepeatExecutionException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }

    public ThainRepeatExecutionException(@NonNull Throwable cause) {
        super(cause);
    }

    public ThainRepeatExecutionException(@NonNull String message, @NonNull Throwable cause,
                                         boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
