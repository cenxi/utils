package dynamicload.dao;

import org.springframework.stereotype.Component;

/**
 * @author fengxi
 * @date 2022年07月01日 10:35
 */
@Component
public class TestDao {

    public String query(String msg) {
        return "msg:"+msg;
    }

}
