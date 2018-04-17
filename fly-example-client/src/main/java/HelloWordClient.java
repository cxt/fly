import com.cxt.fly.client.RpcProxy;
import com.cxt.fly.service.HelloWorldService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * HelloWordClient
 * @author cxt
 * @date   2018/4/17
 */
public class HelloWordClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWordClient.class);
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = applicationContext.getBean(RpcProxy.class);
        HelloWorldService helloWorldService = rpcProxy.create(HelloWorldService.class);
        String res = helloWorldService.say("cxt");
        System.out.println(res);
    }
}
