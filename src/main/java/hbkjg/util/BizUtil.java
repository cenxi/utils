package hbkjg.util;

import hbkjg.client.Client;
import hbkjg.common.Constants;
import hbkjg.dto.req.ContactInfo;
import hbkjg.dto.req.ContactPageReq;
import hbkjg.dto.req.OrderReq;
import hbkjg.dto.req.TouristInfo;
import hbkjg.dto.resp.CommonPostRes;
import hbkjg.dto.resp.ContactPageResp;
import hbkjg.dto.resp.ContactResp;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fengxi
 * @className BizUtil
 * @description
 * @date 2023年07月13日 15:18
 */
public class BizUtil {

    public static void setTourist(OrderReq orderReq) {

        ContactPageReq contactPageReq = new ContactPageReq();
        contactPageReq.setUserId(Constants.userId);
        CommonPostRes<ContactPageResp> contactPageRes = Client.contactPage(contactPageReq);
        ContactPageResp contactPageResp = contactPageRes.getData();
        List<ContactResp> contacts = contactPageResp.getList();

        List<TouristInfo> touristInfo = new ArrayList<>();
        ContactInfo contactInfo = new ContactInfo();
        for (ContactResp contact : contacts) {
            if (contact.getFidNo().contains("1992")) {
                contactInfo.setPhone(contact.getFmobile());
                contactInfo.setName(contact.getFname());
                contactInfo.setCardId(contact.getFidNo());
            }
            TouristInfo tour = new TouristInfo();
            tour.setName(contact.getFname());
            tour.setPhone(contact.getFmobile());
            tour.setCardId(contact.getFidNo());
            touristInfo.add(tour);
        }

        orderReq.setContactInfo(contactInfo);
        orderReq.setTouristInfo(touristInfo);
        orderReq.setQuantity(touristInfo.size());
    }
}
