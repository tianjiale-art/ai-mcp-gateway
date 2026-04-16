package cn.tianjiale.ai.api;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface IMcpGatewayService {
    Flux<ServerSentEvent<String>> establishSSEConnection(String gatewayId) throws Exception;
}
