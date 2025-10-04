package com.lightning.rpc.example.service.impl;

import com.lightning.rpc.example.model.User;
import com.lightning.rpc.example.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


public class UserServiceImpl implements UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    
    private final ConcurrentHashMap<Long, User> userMap = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    @Override
    public User getUserById(Long id) {
        logger.info("Getting user by id: {}", id);
        User user = userMap.get(id);
        if (user == null) {
            logger.warn("User not found with id: {}", id);
        }
        return user;
    }
    
    @Override
    public Boolean createUser(User user) {
        logger.info("Creating user: {}", user);
        try {
            if (user.getId() == null) {
                user.setId(idGenerator.getAndIncrement());
            }
            userMap.put(user.getId(), user);
            logger.info("User created successfully: {}", user);
            return true;
        } catch (Exception e) {
            logger.error("Failed to create user", e);
            return false;
        }
    }
    
    @Override
    public Boolean updateUser(User user) {
        logger.info("Updating user: {}", user);
        try {
            if (userMap.containsKey(user.getId())) {
                userMap.put(user.getId(), user);
                logger.info("User updated successfully: {}", user);
                return true;
            } else {
                logger.warn("User not found for update: {}", user.getId());
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to update user", e);
            return false;
        }
    }
    
    @Override
    public Boolean deleteUser(Long id) {
        logger.info("Deleting user with id: {}", id);
        try {
            User removed = userMap.remove(id);
            if (removed != null) {
                logger.info("User deleted successfully: {}", removed);
                return true;
            } else {
                logger.warn("User not found for deletion: {}", id);
                return false;
            }
        } catch (Exception e) {
            logger.error("Failed to delete user", e);
            return false;
        }
    }
}
