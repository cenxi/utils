package common.utils.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.utils.auth.entity.dto.req.UpdateUserPassReq;
import common.utils.auth.entity.model.CcSysUser;

/**
* @author Administrator
* @description 针对表【cc_sys_user(系统用户)】的数据库操作Service
* @createDate 2022-12-01 09:46:19
*/
public interface CcSysUserService extends IService<CcSysUser> {

    CcSysUser findByName(String name);

    void updatePwd(UpdateUserPassReq userPassReq) throws Exception;

    void updatePass(String username, String encode);
}
