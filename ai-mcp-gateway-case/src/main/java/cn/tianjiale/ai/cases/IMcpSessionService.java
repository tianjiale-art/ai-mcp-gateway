package cn.tianjiale.ai.cases;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface IMcpSessionService {
    /**
     * 创建MCP会话服务
     */
    Flux<ServerSentEvent<String>> createMcpSession(String gatewayId) throws Exception;
}
