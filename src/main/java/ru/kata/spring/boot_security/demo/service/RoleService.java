package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

public interface RoleService {
    void saveOrChangeRole(Role role);

    void deleteRole(Long id);

    List<Role> getRoles();

    Role getRoleById(Long id);

    Role getRoleByName(String name);
}
