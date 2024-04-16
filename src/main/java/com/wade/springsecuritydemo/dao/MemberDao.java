package com.wade.springsecuritydemo.dao;

import com.wade.springsecuritydemo.model.Member;
import com.wade.springsecuritydemo.model.Role;

import java.util.List;

public interface MemberDao {
    Member getMemberByEmail(String email);
    Integer createMember(Member member);
    List<Role> getRolesByMemberId(Integer memberId);
    void addRoleForMemberId(Integer memberId, Role role);
    void removeRoleFromMemberId(Integer memberId, Role role);
}
