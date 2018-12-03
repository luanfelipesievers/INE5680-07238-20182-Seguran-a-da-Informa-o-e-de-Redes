package br.com.ufsc.demo.service;

import br.com.ufsc.demo.mapper.UserMapper;
import br.com.ufsc.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private UserMapper userMapper;

    public void insert(User user) throws Exception {
        String password = this.passwordService.createHash(user.getPassword());
        this.userMapper.insertUser(user.getUsername(),password);
    }

    public void update(Integer id, User user) throws Exception {
        if (id == null){
            return;
        }
        String password = this.passwordService.createHash(user.getPassword());
        this.userMapper.updateUser(password, id);
    }

    public void delete(Integer id){
        if (id == null){
            return;
        }
        this.userMapper.deleteUser(id);
    }

    public User find(User user) throws Exception {
        User foundUser = this.userMapper.findUserByUserName(user.getUsername());
        if(foundUser == null){
            return null;
        }

        String originalPassword = foundUser.getPassword();
        String password = user.getPassword();

        return this.passwordService.verifyPassword(password, originalPassword) ? foundUser : null;
    }

}
