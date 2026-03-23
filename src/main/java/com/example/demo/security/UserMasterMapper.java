package com.example.demo.security;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMasterMapper {

    UserMaster findByUserName(String userName);

    UserMaster findByUserId(String userId);

    int insertUser(UserMaster userMaster);
}
