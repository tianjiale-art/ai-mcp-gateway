package cn.tianjiale.ai.cases.session;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.tianjiale.ai.cases.IMcpSessionService;
import cn.tianjiale.ai.cases.session.factory.DefaultMcpSessionFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Service
@Slf4j
public class McpSessionService implements IMcpSessionService {
    @Resource
    private DefaultMcpSessionFactory defaultMcpSessionFactory;
    @Override
    public Flux<ServerSentEvent<String>> createMcpSession(String gatewayId) throws Exception {
        StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> stringDynamicContextFluxStrategyHandler = defaultMcpSessionFactory.strategyHandler();
        return stringDynamicContextFluxStrategyHandler.apply(gatewayId,new DefaultMcpSessionFactory.DynamicContext());
    }
}
