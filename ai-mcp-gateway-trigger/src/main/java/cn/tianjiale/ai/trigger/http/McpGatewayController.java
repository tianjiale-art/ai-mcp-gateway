package cn.tianjiale.ai.trigger.http;

import cn.tianjiale.ai.api.IMcpGatewayService;
import cn.tianjiale.ai.types.enums.ResponseCode;
import cn.tianjiale.ai.types.exception.AppException;
import cn.tianjiale.ai.cases.IMcpSessionService;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * MCP网关服务接口管理
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*",allowedHeaders = "*",methods = {RequestMethod.DELETE,RequestMethod.GET,RequestMethod.HEAD,RequestMethod.OPTIONS,RequestMethod.POST})
@RequestMapping("/")
public class McpGatewayController implements IMcpGatewayService {
    @Resource
    private IMcpSessionService mcpSessionService;


    /***
     * 建立sse连接，创建会话
     * http://localhost:8091/api-gateway/test10001/mcp/sse
     * @param gatewayId 网关id
     * @return
     * @throws Exception
     */
    @GetMapping(value = "{gatewayId}/mcp/sse",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Override
    public Flux<ServerSentEvent<String>> establishSSEConnection(@PathVariable("gatewayId") String gatewayId) throws Exception {
        try{
            log.info("建立MCP连接:{}",gatewayId);
            if(StringUtils.isBlank(gatewayId)){
                log.info("gatewayId:{} is null",gatewayId);
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            return mcpSessionService.createMcpSession(gatewayId);
        }catch (Exception e){
            log.info("建立MCP 连接失败:{}",gatewayId);
            throw e;
        }
    }
}
