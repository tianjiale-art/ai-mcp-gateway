package cn.tianjiale.ai.cases.session.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.tianjiale.ai.cases.session.AbstractMcpSessionSupport;
import cn.tianjiale.ai.cases.session.factory.DefaultMcpSessionFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Slf4j
@Service
public class VerifyNode extends AbstractMcpSessionSupport {
    @Resource
    private SessionNode sessionNode;

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String s, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        return router(s,dynamicContext);
    }

    @Override
    public StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String s, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        return sessionNode;
    }
}
