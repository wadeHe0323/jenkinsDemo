package com.wade.springsecuritydemo.controller;

import com.wade.springsecuritydemo.dao.MemberDao;
import com.wade.springsecuritydemo.dao.RoleDao;
import com.wade.springsecuritydemo.dto.SubscribeRequest;
import com.wade.springsecuritydemo.dto.UnsubscribeRequest;
import com.wade.springsecuritydemo.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SubscriptionController {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private RoleDao roleDao;

    @PostMapping("/subscribe")
    public String subscribe(@RequestBody SubscribeRequest subscribeRequest) {

        Integer memberId = subscribeRequest.getMemberId();
        List<Role> roleList = memberDao.getRolesByMemberId(memberId);

        boolean isSubscribe = checkSubscribeStatus(roleList);

        if (isSubscribe) {
            return "已訂閱，不需重複訂閱";
        } else {
            Role vipRole = roleDao.getRoleByName("ROLE_VIP_MEMBER");
            memberDao.addRoleForMemberId(memberId, vipRole);

            return "訂閱成功，請刪除 cookie 重新登入";
        }
    }

    @PostMapping("/unsubscribe")
    public String unsubscribe(@RequestBody UnsubscribeRequest unsubscribeRequest) {

        Integer memberId = unsubscribeRequest.getMemberId();
        List<Role> roleList = memberDao.getRolesByMemberId(memberId);

        boolean isSubscribe = checkSubscribeStatus(roleList);

        if (isSubscribe) {
            Role vipRole = roleDao.getRoleByName("ROLE_VIP_MEMBER");
            memberDao.removeRoleFromMemberId(memberId, vipRole);

            return "取消訂閱成功，請刪除 cookie 重新登入";
        } else {
            return "尚未訂閱，無法執行取消訂閱功能";
        }
    }

    private boolean checkSubscribeStatus(List<Role> roleList) {
        boolean isSubscribe = false;

        for (Role role : roleList) {
            if (role.getRoleName().equals("ROLE_VIP_MEMBER")) {
                isSubscribe = true;
            }
        }

        return isSubscribe;
    }
}
