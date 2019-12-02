/*
 * Copyright (c) 2019, Xiaomi, Inc.  All rights reserved.
 * This source code is licensed under the Apache License Version 2.0, which
 * can be found in the LICENSE file in the root directory of this source tree.
 */
package com.xiaomi.thain.core.scheduler;

import com.xiaomi.thain.common.exception.ThainRuntimeException;
import com.xiaomi.thain.common.exception.scheduler.ThainSchedulerInitException;
import com.xiaomi.thain.common.exception.scheduler.ThainSchedulerStartException;
import com.xiaomi.thain.common.model.dr.FlowDr;
import com.xiaomi.thain.core.process.ProcessEngine;
import com.xiaomi.thain.core.scheduler.job.CleanJob;
import com.xiaomi.thain.core.scheduler.job.FlowJob;
import com.xiaomi.thain.core.scheduler.job.RecoveryJob;
import com.xiaomi.thain.core.scheduler.job.SlaJob;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.time.Instant;
import java.util.Date;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Date 19-5-17 下午1:41
 *
 * @author liangyongrui@xiaomi.com
 */
@Slf4j
public class SchedulerEngine {

    @NonNull
    private final Scheduler scheduler;

    private static final String SYSTEM_GROUP = "system";

    private SchedulerEngine(@NonNull SchedulerEngineConfiguration schedulerEngineConfiguration,
                            @NonNull ProcessEngine processEngine)
            throws ThainSchedulerInitException {
        try {
            val factory = new StdSchedulerFactory();
            factory.initialize(schedulerEngineConfiguration.properties);
            this.scheduler = factory.getScheduler();
            scheduler.setJobFactory((bundle, ignore) -> {
                try {
                    val method = Class.forName(bundle.getJobDetail().getJobClass().getName())
                            .getMethod("getInstance", ProcessEngine.class);
                    return (Job) method.invoke(null, processEngine);
                } catch (Exception e) {
                    throw new ThainRuntimeException(e);
                }
            });
            initCleanUp();
            initRecovery();
        } catch (Exception e) {
            log.error("thain init failed", e);
            throw new ThainSchedulerInitException(e.getMessage());
        }
    }

    private void initRecovery() throws SchedulerException {
        JobDetail jobDetail = newJob(RecoveryJob.class)
                .withIdentity("job_recovery", SYSTEM_GROUP)
                .build();
        Trigger trigger = newTrigger()
                .withIdentity("trigger_recovery", SYSTEM_GROUP)
                .withSchedule(cronSchedule("0 * * * * ?").withMisfireHandlingInstructionDoNothing())
                .build();
        scheduler.deleteJob(jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    private void initCleanUp() throws SchedulerException {
        JobDetail jobDetail = newJob(CleanJob.class)
                .withIdentity("job_clean_up", SYSTEM_GROUP)
                .build();
        Trigger trigger = newTrigger()
                .withIdentity("trigger_clean_up", SYSTEM_GROUP)
                .withSchedule(cronSchedule("0 0 * * * ?"))
                .build();
        scheduler.deleteJob(jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public static SchedulerEngine getInstance(@NonNull SchedulerEngineConfiguration schedulerEngineConfiguration,
                                              @NonNull ProcessEngine processEngine)
            throws ThainSchedulerInitException {
        return new SchedulerEngine(schedulerEngineConfiguration, processEngine);
    }

    public void addSla(long flowExecutionId, @NonNull FlowDr flowDr) throws SchedulerException {
        JobDetail jobDetail = newJob(SlaJob.class)
                .withIdentity("flowExecution_" + flowExecutionId, "flowExecution")
                .usingJobData("flowExecutionId", flowExecutionId)
                .usingJobData("flowId", flowDr.id)
                .build();
        Trigger trigger = newTrigger()
                .withIdentity("trigger_" + flowExecutionId, "flowExecution")
                .startAt(Date.from(Instant.now().plusSeconds(flowDr.slaDuration)))
                .build();
        scheduler.deleteJob(jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void start() throws ThainSchedulerStartException {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("start", e);
            throw new ThainSchedulerStartException(e.getMessage());
        }
    }

    /**
     * 添加指定任务，加入调度
     *
     * @param flowId flow id
     * @param cron   cron
     */
    public void addFlow(long flowId, @NonNull String cron) throws SchedulerException {
        JobDetail jobDetail = newJob(FlowJob.class)
                .withIdentity("flow_" + flowId, "flow")
                .usingJobData("flowId", flowId)
                .build();
        if (StringUtils.isBlank(cron)) {
            scheduler.deleteJob(jobDetail.getKey());
            return;
        }
        Trigger trigger = newTrigger()
                .withIdentity("trigger_" + flowId, "flow")
                .withSchedule(cronSchedule(cron))
                .build();
        scheduler.deleteJob(jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 删除调度
     */
    public void deleteFlow(long flowId) throws SchedulerException {
        scheduler.deleteJob(new JobKey("flow_" + flowId, "flow"));
    }
}
