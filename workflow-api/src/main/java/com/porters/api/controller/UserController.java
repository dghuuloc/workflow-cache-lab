package com.porters.api.controller;

import com.porters.api.model.User;
import com.porters.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired UserService userService;

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PostMapping("/{id}/hash")
    public String saveUserHash(@PathVariable Long id) {
        userService.saveUserHash(new User(id, "Tom", 20));
        return "Saved as HASH";
    }

    @GetMapping("/{id}/hash")
    public Object getUserHash(@PathVariable Long id) {
        return userService.getUserHash(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    // invalidate cache of 1 user
    @DeleteMapping("/{id}/cache")
    public String invalidate(@PathVariable Long id) {
        userService.invalidateUser(id);
        return "Cache invalidated for user " + id;
    }

    // clear all cache
    @DeleteMapping("/cache")
    public String clearAll() {
        userService.clearAllUsersCache();
        return "All cache cleared";
    }

}
