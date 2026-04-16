package cn.tianjiale.ai.cases.session.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.tianjiale.ai.cases.session.AbstractMcpSessionSupport;
import cn.tianjiale.ai.cases.session.factory.DefaultMcpSessionFactory;
import cn.tianjiale.ai.domain.session.model.valobj.SessionConfigVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;


@Slf4j
@Service
public class EndNode extends AbstractMcpSessionSupport {
    @Override
    protected Flux<ServerSentEvent<String>> doApply(String s, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        log.info("创建会话-EndNode:{}",s);

        //获取上下文
        SessionConfigVO sessionConfigVO = dynamicContext.getSessionConfigVO();
        String sessionId = sessionConfigVO.getSessionId();

        Sinks.Many<ServerSentEvent<String>> sink = sessionConfigVO.getSink();

        return sink.asFlux()
                .mergeWith(
                        Flux.interval(Duration.ofSeconds(60))
                                .map(i -> ServerSentEvent.<String>builder()
                                        .event("ping")
                                        .data("ping")
                                        .build())
                )
                .doOnCancel(() ->{
                    log.info("SSE连接取消，会话Id：{}",sessionId);
                    sessionManagementService.removeSession(sessionId);
                })
                .doOnTerminate(()  ->{
                    log.info("SSE连接终止，会话ID：{}",sessionId);
                    sessionManagementService.removeSession(sessionId);
                });
    }

    @Override
    public StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String s, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        return defaultStrategyHandler;
    }
}
