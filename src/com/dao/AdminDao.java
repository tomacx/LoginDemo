package com.dao;

import com.pojo.Admin;
public interface AdminDao {
    Admin findByNameAndPassword(Admin admin);
}
