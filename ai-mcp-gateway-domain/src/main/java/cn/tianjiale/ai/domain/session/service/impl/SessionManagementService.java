package cn.tianjiale.ai.domain.session.service.impl;


import cn.tianjiale.ai.domain.session.model.valobj.SessionConfigVO;
import cn.tianjiale.ai.domain.session.service.ISessionManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Service
public class SessionManagementService implements ISessionManagementService {
    /**
     * 会话超时时间-也可以把配置抽取到yml里
     */
    private static final long session_timeout_minutes = 30;
    /**
     * 定时任务调度
     */
    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
    /**
     * 活跃会话存储器，key->sessionId,ConcurrentHashMap 确保线程安全
     */
    private final Map<String,SessionConfigVO> activeSessions = new ConcurrentHashMap();
    public SessionManagementService(){

        //单线程定时任务器，定时延迟执行，延迟时间5分钟，间隔时间5分钟
        cleanupScheduler.scheduleAtFixedRate(this::cleanupExpiredSessions,5,5, TimeUnit.MINUTES);
        log.info("会话管理服务已启动，会话超时时间：{} 分钟",session_timeout_minutes);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public SessionConfigVO createSession(String gatewayId) {
        log.info("创建会话 gatewayId:{}",gatewayId);

        String sessionId = UUID.randomUUID().toString();

        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();

        // 发送端点消息-告知客户端消息请求地址（客户端第二次会使用messageEndpoint 进行请求会话）
        String messageEndpoint = "/" + gatewayId + "/mcp/message?sessionId=" + sessionId;
        //将构建好的事件真正发射出去的动作
        sink.tryEmitNext(ServerSentEvent.<String>builder()
                        .event("endpoint")
                        .data(messageEndpoint)
                .build());

        SessionConfigVO sessionConfigVO = new SessionConfigVO(sessionId, sink);

        activeSessions.put(sessionId,sessionConfigVO);

        log.info("创建会话 gatewayId:{} sessionId:{},当前会话活跃数:{}",gatewayId,sessionId,activeSessions.size());
        return sessionConfigVO;
    }

    @Override
    public void removeSession(String sessionId) {
        SessionConfigVO sessionConfigVO = activeSessions.remove(sessionId);

        if (null == sessionId) return;

        sessionConfigVO.markInactive();

        try{
           // 这是Reactor Sinks的安全关闭 / 完成推送方法，专门用来优雅结束 SSE 长连接、断开消息流
            sessionConfigVO.getSink().tryEmitComplete();
        }catch (Exception e){
            log.warn("关闭会话失败",e);
        }

    }

    @Override
    public SessionConfigVO getSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()){
            return null;
        }
        SessionConfigVO sessionConfigVO = activeSessions.get(sessionId);
        if (sessionConfigVO != null && sessionConfigVO.isActive()){
            sessionConfigVO.updateLastAccessed();
            return sessionConfigVO;
        }
        return null;
    }

    @Override
    public void cleanupExpiredSessions() {
        int cleanedCount = 0;
        for(Map.Entry<String,SessionConfigVO> entry : activeSessions.entrySet()){
            SessionConfigVO sessionConfigVO = entry.getValue();
            if(!sessionConfigVO.isActive() || sessionConfigVO.isExpired(session_timeout_minutes)){
                removeSession(sessionConfigVO.getSessionId());
                cleanedCount++;
            }
        }
        if(cleanedCount > 0){
            log.info("清理了{}",cleanedCount);
        }
    }

    @Override
    public void shutdown() {
    log.info("关闭会话管理服务...");
    for(String sessionId: activeSessions.keySet()){
        removeSession(sessionId);
    }
    //关闭清理调度器
        cleanupScheduler.shutdown();
    //等待5秒让正在执行的任务完成
        try {
            if(!cleanupScheduler.awaitTermination(5,TimeUnit.SECONDS)){
                //超时强制关闭
                cleanupScheduler.shutdown();
            }
        } catch (InterruptedException e) {
            //异常强制关闭
            cleanupScheduler.shutdown();
            Thread.currentThread().interrupt();
        }
        log.info("关闭会话管理服务完成");
    }
}
