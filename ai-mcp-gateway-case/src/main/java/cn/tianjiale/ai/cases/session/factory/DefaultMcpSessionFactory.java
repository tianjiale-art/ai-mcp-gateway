package cn.tianjiale.ai.cases.session.factory;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.tianjiale.ai.domain.session.model.valobj.SessionConfigVO;
import cn.tianjiale.ai.cases.session.node.RootNode;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class DefaultMcpSessionFactory {
    @Resource
    private RootNode rootNode;
    public StrategyHandler<String,DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> strategyHandler(){
        return rootNode;
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext{
        private SessionConfigVO sessionConfigVO;
    }

}
