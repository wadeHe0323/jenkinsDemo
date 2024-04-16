package com.wade.springsecuritydemo.dao;

import com.wade.springsecuritydemo.model.Role;

public interface RoleDao {

    Role getRoleByName(String roleName);
}
