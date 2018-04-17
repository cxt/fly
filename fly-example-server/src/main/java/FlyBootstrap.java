import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author cxt
 * @date   2018/4/17
 */
public class FlyBootstrap {
    private static final Logger LOGGER = LoggerFactory.getLogger(FlyBootstrap.class);

    public static void main(String[] args) {
        LOGGER.info("server starting ...");
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
