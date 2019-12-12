package com.xiaomi.thain.server.service

import com.xiaomi.thain.common.model.FlowModel
import com.xiaomi.thain.common.model.JobModel
import com.xiaomi.thain.common.model.dr.FlowDr
import com.xiaomi.thain.common.model.rq.AddFlowRq
import com.xiaomi.thain.common.model.rq.AddJobRq
import com.xiaomi.thain.server.model.sp.FlowListSp
import org.springframework.stereotype.Service

/**
 * @author liangyongrui
 */
@Service
interface FlowService {
    fun getFlowList(flowListSp: FlowListSp): List<FlowModel?>
    fun getFlowListCount(flowListSp: FlowListSp): Long
    /**
     * 创建或更新任务
     */
    @Throws(com.xiaomi.thain.common.exception.ThainException::class, java.text.ParseException::class, org.quartz.SchedulerException::class)
    fun add(addFlowRq: AddFlowRq, addJobRqList: List<AddJobRq>, appId: String): Long

    /**
     * 删除
     */
    @Throws(org.quartz.SchedulerException::class)
    fun delete(flowId: Long): Boolean

    /**
     * 立即执行一次, 返回flow execution id
     */
    @Throws(com.xiaomi.thain.common.exception.ThainException::class, com.xiaomi.thain.common.exception.ThainRepeatExecutionException::class)
    fun start(flowId: Long): Long

    fun getFlow(flowId: Long): FlowDr?
    fun getJobModelList(flowId: Long): List<JobModel>
    fun getComponentDefineStringMap(): Map<String, String>
    @Throws(com.xiaomi.thain.common.exception.ThainException::class, org.quartz.SchedulerException::class, java.io.IOException::class)
    fun scheduling(flowId: Long)

    @Throws(com.xiaomi.thain.common.exception.ThainException::class)
    fun pause(flowId: Long)

    @Throws(com.xiaomi.thain.common.exception.ThainException::class, java.text.ParseException::class, org.quartz.SchedulerException::class, java.io.IOException::class)
    fun updateCron(flowId: Long, cron: String?)
}