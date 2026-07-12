package fu.edu.mss301.digilib.member;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.cloud.discovery.enabled=false"
})
class MemberServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
