reactor-core = 响应式流的核心实现 + Flux/Mono + 操作符 + 背压 + 调度，是构建Java高并发，非阻塞，微服务/流处理应用的基础依赖，也是spring响应式技术栈的基石
Sinks是Project Reactor提供的一个用于创建数据“汇”sink的工厂类。数据的源头。Sinks允许你主动向数据流中推送emit数据。

Sinks.Many<T>表示这个水龙头可以发射多个数据项（Many），ServerSentEvent<String>>:这是数据流中承载的数据类型。
ServerSentEvent是SpringWebFlux中用于SSE的标准数据载体，它封装了要推送给客户端的数据。

multicast():这是一个多播流。可以有多个订阅者向这个sink订阅
onBAckpressureBuffer() 这是处理“背压”的策略。用于解决数据生产者速度快于消费者处理数度的问题。

