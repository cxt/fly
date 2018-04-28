import com.cxt.fly.client.RpcProxy;
import com.cxt.fly.model.User;
import com.cxt.fly.service.HelloWorldService2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * HelloWordClient2
 *
 * @author cxt
 * @date 2018/4/17
 */
public class HelloWorldClient2 {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = applicationContext.getBean(RpcProxy.class);
        HelloWorldService2 helloworld2 = rpcProxy.create(HelloWorldService2.class, "helloworld2");
        User user = new User();
        user.setName("cxt");
        user.setAge(26);
        String res = helloworld2.say(user);
        System.out.println(res);
    }
}
