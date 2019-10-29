/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.model.rq;

import lombok.Builder;
import lombok.NonNull;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author wangsimin3@xiaomi.com
 * @date 2019/10/24
 */
@Builder
public class X5ConfigRq {
    @NonNull
    public final String appId;
    @NonNull
    public final String appKey;
    @NonNull
    public final String appName;
    @NonNull
    public final List<String> principals;
    @Nullable
    public final String description;
}
