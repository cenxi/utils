package common.utils.validate;

import common.utils.validate.group.AddGroup;
import common.utils.validate.group.UpdateGroup;

/**
 * @author fengxi
 * @className ValidatorTest
 * @description
 * @date 2024年11月14日 11:13
 */
public class ValidatorTest {

    public static void main(String[] args) {
        SysUser user = new SysUser();
        ValidatorUtils.validateEntity(user, UpdateGroup.class);

    }
}
