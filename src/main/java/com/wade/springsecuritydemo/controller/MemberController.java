package com.wade.springsecuritydemo.controller;

import com.wade.springsecuritydemo.dao.MemberDao;
import com.wade.springsecuritydemo.dao.RoleDao;
import com.wade.springsecuritydemo.model.Member;
import com.wade.springsecuritydemo.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class MemberController {

    @Autowired
    private MemberDao memberDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public String register(@RequestBody Member member) {
        // 忽略參數驗證

        // 密碼加密
        String hashedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(hashedPassword);

        // 插入 member 數據
        Integer memberId = memberDao.createMember(member);

        // 添加基本權限
        Role normalRole = roleDao.getRoleByName("ROLE_NORMAL_MEMBER");
        memberDao.addRoleForMemberId(memberId, normalRole);

        return "註冊成功";
    }

    @PostMapping("/userLogin")
    public String userLogin(Authentication authentication) {
        String username = authentication.getName();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        return "登入成功 帳號: " + username + ", 權限為: " + authorities;
    }
}
