package com.movision.mybatis.user.mapper;

import com.movision.mybatis.user.entity.*;
import org.apache.ibatis.annotations.Param;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

public interface UserMapper {

    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    User selectByPhone(User user);

    LoginUser selectLoginUserByPhone(User user);

    int updateUserPointsAdd(Map mapadd);

    int updateUserPointsMinus(Map map);

    int queryUserByPoints(int id);

    int isExistAccount(@Param("phone") String phone);

    int registerAccount(RegisterUser registerUser);

    UserVo queryUserInfo(int userid);

    User queryCircleMasterByPhone(String phone);

    List<User> queryCircleManagerList(int circleid);


    List<User> selectAllUser();

    int isAppAdminUser(@Param("phone") String phone);

    String queryUserByOpenid(Integer userid);

    String queryUserByNickname(Integer circleid);

    User queryUser(String phone);

    List<UserLike> likeQueryPostByNickname(String name, RowBounds rowBounds);

    User findAllUser(int id);
}