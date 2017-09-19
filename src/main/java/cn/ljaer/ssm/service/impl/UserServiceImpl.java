package cn.ljaer.ssm.service.impl;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import cn.ljaer.ssm.po.User;
import cn.ljaer.ssm.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jaer on 2016/9/12.
 */
@Slf4j
public class UserServiceImpl implements UserService{


    @Override
    public User selectByPrimaryKey(Integer id) throws Exception {
        return new User();
    }

	@Override
	public void saveMethod() {
		log.info("--------------------saveMethod-----------------------");
	}

	@Override
	public void selectMethod() {
		log.info("--------------------selectMethod-----------------------");
	}
}
