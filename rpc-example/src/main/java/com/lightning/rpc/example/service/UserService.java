package com.lightning.rpc.example.service;

import com.lightning.rpc.example.model.User;


public interface UserService {
    
    
    User getUserById(Long id);
    
    
    Boolean createUser(User user);
    
    
    Boolean updateUser(User user);
    
    
    Boolean deleteUser(Long id);
}
