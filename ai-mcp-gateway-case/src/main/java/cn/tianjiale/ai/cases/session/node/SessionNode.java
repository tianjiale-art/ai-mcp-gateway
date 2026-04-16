package cn.tianjiale.ai.cases.session.node;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.tianjiale.ai.cases.session.AbstractMcpSessionSupport;
import cn.tianjiale.ai.cases.session.factory.DefaultMcpSessionFactory;
import cn.tianjiale.ai.domain.session.model.valobj.SessionConfigVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;


@Slf4j
@Service
public class SessionNode extends AbstractMcpSessionSupport {
    @Resource
    private EndNode endNode;
    @Override
    protected Flux<ServerSentEvent<String>> doApply(String s, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        log.info("创建会话-SessionNode:{}",s);
        //创建会话服务
        SessionConfigVO sessionConfigVO = sessionManagementService.createSession(s);

        //写入上下文中
        dynamicContext.setSessionConfigVO(sessionConfigVO);
        return  router(s,dynamicContext);
    }

    @Override
    public StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String s, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        return endNode;
    }
}
