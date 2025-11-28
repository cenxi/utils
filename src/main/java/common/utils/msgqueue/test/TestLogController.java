package common.utils.msgqueue.test;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fengxi
 * @className TestLogController
 * @description
 * @date 2025年11月26日 15:35
 */
@RequestMapping("/test-log")
@RestController
public class TestLogController {

    @Autowired
    TestProduceLog testProduceLog;

    @PostMapping("/produce")
    public Object produce(@RequestBody Object o) {
        testProduceLog.produce(JSON.toJSONString(o));

        return "";
    }

}
