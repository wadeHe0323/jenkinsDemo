package com.wade.springsecuritydemo.dao;

import com.wade.springsecuritydemo.model.Member;
import com.wade.springsecuritydemo.model.Role;
import com.wade.springsecuritydemo.rowmapper.MemberRowMapper;
import com.wade.springsecuritydemo.rowmapper.RoleRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MemberDaoImpl implements MemberDao{

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private MemberRowMapper memberRowMapper;

    @Autowired
    private RoleRowMapper roleRowMapper;

    @Override
    public Member getMemberByEmail(String email) {

        String sql = "SELECT member_id, email, password, name, age FROM member WHERE email = :email";
        Map<String, Object> map = new HashMap<>();
        map.put("email", email);

        List<Member> members = namedParameterJdbcTemplate.query(sql, map, memberRowMapper);

        if (!members.isEmpty()) {
            return members.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Integer createMember(Member member) {
        String sql = "INSERT INTO member(email, password, name, age) VALUES (:email, :password, :name, :age)";

        Map<String, Object> map = new HashMap<>();
        map.put("email", member.getEmail());
        map.put("password", member.getPassword());
        map.put("name", member.getName());
        map.put("age", member.getAge());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

        int memberId = keyHolder.getKey().intValue();

        return memberId;
    }

    @Override
    public List<Role> getRolesByMemberId(Integer memberId) {
        String sql = "SELECT r.role_id, r.role_name" +
                " FROM role r JOIN member_has_role mhr ON r.role_id = mhr.role_id" +
                " WHERE mhr.member_id = :member_Id";
        Map<String, Object> map = new HashMap<>();
        map.put("member_Id", memberId);

        List<Role> roleList = namedParameterJdbcTemplate.query(sql, map, roleRowMapper);

        if (!roleList.isEmpty()) {
            return roleList;
        } else {
            return null;
        }
    }

    @Override
    public void addRoleForMemberId(Integer memberId, Role role) {
        String sql = "INSERT INTO member_has_role(member_id, role_id) VALUES (:memberId, :roleId)";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("roleId", role.getRoleId());

        namedParameterJdbcTemplate.update(sql, map);
    }

    @Override
    public void removeRoleFromMemberId(Integer memberId, Role role) {
        String sql = "DELETE FROM member_has_role WHERE member_id = :memberId AND role_id = :roleId";

        Map<String, Object> map = new HashMap<>();
        map.put("memberId", memberId);
        map.put("roleId", role.getRoleId());

        namedParameterJdbcTemplate.update(sql, map);
    }
}
