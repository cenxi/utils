package dynamicload.service;

import dynamicload.dao.TestDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author fengxi
 * @date 2022年07月01日 10:36
 */
public abstract class TestService {

    @Autowired
    protected TestDao dao;

    public abstract String sayHello(String msg);

}
