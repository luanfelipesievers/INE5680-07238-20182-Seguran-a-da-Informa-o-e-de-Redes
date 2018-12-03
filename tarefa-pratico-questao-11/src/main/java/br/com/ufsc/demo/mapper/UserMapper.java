package br.com.ufsc.demo.mapper;

import br.com.ufsc.demo.model.User;
import org.apache.ibatis.annotations.*;

public interface UserMapper {


    @Select("select * from users where username = #{username}")
    User findUserByUserName(@Param("username") String username);

    @Insert("insert into users(username,password) values(#{username},#{password})")
    void insertUser(@Param("username") String username, @Param("password") String password);

    @Update("update users set password = #{password} where id = #{id}")
    void updateUser(@Param("password") String password, @Param("id") Integer id);

    @Delete("delete from users where id = #{id}")
    void deleteUser(@Param("id") Integer id);


}
