/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.server.model.rp;

import com.xiaomi.thain.common.model.FlowModel;
import com.xiaomi.thain.common.model.JobModel;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;

/**
 * Date 19-7-1 上午10:33
 * flow model 和 jobModel list
 *
 * @author liangyongrui@xiaomi.com
 */
@Builder
public class FlowAllInfoRp {
    @NonNull
    public final FlowModel flowModel;
    @NonNull
    public final List<JobModel> jobModelList;
}
