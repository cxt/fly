import com.cxt.fly.client.RpcProxy;
import com.cxt.fly.service.HelloWorldService;
import com.cxt.fly.service.HelloWorldService2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * HelloWordClient2
 * @author cxt
 * @date   2018/4/17
 */
public class HelloWordClient2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(HelloWordClient2.class);
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = applicationContext.getBean(RpcProxy.class);
        HelloWorldService2 helloworld2 = rpcProxy.create(HelloWorldService2.class, "helloworld2");
        String res = helloworld2.say("cxt");
        System.out.println(res);
    }
}
