package ru.kata.spring.boot_security.demo.init;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;


@Component
public class InitUsers {

    private final UserService userService;

    private final RoleService roleService;

    public InitUsers(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @PostConstruct
    public void init() {
        User user = new User("Dmitry1234", "$2a$12$kK6MniN.c2MQgE9DpUUrYObilYpdwxtPIKxIHqjepZEOSMUuAxS5O"
                , "Dmitry", "Zoteev", "Nike3.68@mail.ru");
        saveNewUser(user, "ROLE_USER");
        user = new User("Admin", "$2a$12$pAyKNmohSt4hGgC4jnUZe.mqmFu4zHdvAfZjWXjYpnNw9KkdVWOTe"
                , "Sergey", "Ivanov", "SergeyAdmin@gmail.ru");
        saveNewUser(user, "ROLE_ADMIN", "ROLE_USER");
    }

    public void saveNewUser(User user, String... rolesNames) {
        try {
            userService.getUserByUserName(user.getUsername());
        } catch (EmptyResultDataAccessException e) {
            userService.saveOrChangeUser(user);
        }
        Set<Role> roles = new HashSet<>();
        for (String roleName : rolesNames) {
            try {
                roleService.getRoleByName(roleName);
            } catch (EmptyResultDataAccessException e) {
                roleService.saveOrChangeRole(new Role(roleName));
            }
            roles.add(roleService.getRoleByName(roleName));
        }
        user = userService.getUserByUserName(user.getUsername());
        user.setRoles(roles);
        userService.saveOrChangeUser(user);
    }
}
