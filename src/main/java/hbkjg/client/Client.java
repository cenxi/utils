package hbkjg.client;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import hbkjg.common.Constants;
import hbkjg.dto.req.ContactPageReq;
import hbkjg.dto.req.OrderReq;
import hbkjg.dto.req.UserReq;
import hbkjg.dto.resp.*;
import hbkjg.util.BizUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengxi
 * @className Client
 * @description
 * @date 2023年07月13日 14:08
 */
public class Client {

    static Map<String, String> commmonHeaders = new HashMap<>();
    // 1秒超时
    static int TIMEOUT = 1000 * 1000;
    static {
        commmonHeaders.put("wh-tour-token", "569408692c7fdd7a18cea4a52ad8bf21");
        commmonHeaders.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36 MicroMessenger/7.0.20.1781(0x6700143B) NetType/WIFI MiniProgramEnv/Windows WindowsWechat/WMPF XWEB/6945");
    }

    /**
     * 根据用户ID查询用户接口
     * @param userReq
     * @return
     */
    public static CommonPostRes<UserResp> findUserById(UserReq userReq) {

        String url = "https://minapp.tyymt.com/userservice/user-account/v1/findUserById";
        HttpResponse response = HttpRequest.post(url)
                .headerMap(commmonHeaders, true)
                .body(JSON.toJSONString(userReq))
                .timeout(TIMEOUT)
                .execute();

        return JSON.parseObject(response.body(), new TypeReference<CommonPostRes<UserResp>>() {
        });
    }

    public static CommonPostRes<ContactPageResp> contactPage(ContactPageReq contactPageReq) {
        String url = "https://minapp.tyymt.com/userservice/userContact/v2/list";

        HttpResponse response = HttpRequest.post(url)
                .headerMap(commmonHeaders, true)
                .body(JSON.toJSONString(contactPageReq))
                .timeout(TIMEOUT)
                .execute();

        return JSON.parseObject(response.body(), new TypeReference<CommonPostRes<ContactPageResp>>() {
        });
    }

    public static CommonGetRes<ProductTitleResp> productList(int scienceId) {
        String url = "https://minapp.tyymt.com/scenic_info/v3/productList?id=" + scienceId;

        HttpResponse response = HttpRequest.get(url)
                .headerMap(commmonHeaders, true)
                .timeout(TIMEOUT)
                .execute();
        return JSON.parseObject(response.body(), new TypeReference<CommonGetRes<ProductTitleResp>>() {
        });
    }

    public static CommonGetRes<List<AvalibleDate>> moredate(int productId) {
        String url = "https://minapp.tyymt.com/scenic_info/moredate?productid=" + productId;

        HttpResponse response = HttpRequest.get(url)
                .headerMap(commmonHeaders, true)
                .timeout(TIMEOUT)
                .execute();
        return JSON.parseObject(response.body(), new TypeReference<CommonGetRes<List<AvalibleDate>>>() {
        });
    }

    public static CommonGetRes order(OrderReq orderReq) {
        String url = "https://minapp.tyymt.com/ymt-oms/sopi/orders";

        HttpResponse response = HttpRequest.post(url)
                .headerMap(commmonHeaders, true)
                .body(JSON.toJSONString(orderReq))
                .timeout(TIMEOUT)
                .execute();
        return JSON.parseObject(response.body(), new TypeReference<CommonGetRes>() {
        });
    }


    public static void main(String[] args) {
        // 查找用户
        UserReq userReq = new UserReq();
        userReq.setUserId(Constants.userId);
        System.out.println(JSON.toJSONString(findUserById(userReq)));

        // 查询通讯录
        ContactPageReq contactPageReq = new ContactPageReq();
        contactPageReq.setUserId(Constants.userId);
        CommonPostRes<ContactPageResp> contactPageRes=contactPage(contactPageReq);
        System.out.println(JSON.toJSONString(contactPageRes));

        // 查上下午产品列表
        System.out.println(JSON.toJSONString(productList(Constants.scienceId),true));
        System.out.println(JSON.toJSONString(moredate(2266),true));

        // 订票
        OrderReq orderReq = new OrderReq();
        orderReq.setTargetTime("2023-07-16");
        BizUtil.setTourist(orderReq);
        orderReq.setProductId("2266");
        System.out.println(JSON.toJSONString(order((orderReq))));
    }

}
