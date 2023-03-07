package ru.kata.spring.boot_security.demo.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Controller
public class UserController {

    private final UserService userService;

    private final RoleService roleService;

    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping(value = "/admin/allUsers")
    public String getUsers(Model model) {
        model.addAttribute("usersList", userService.getUsers());
        return "users";
    }

    @RequestMapping("/admin/addNewUser")
    public String addNewUser(Model model) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.getRoleByName("ROLE_USER"));
        User user = new User();
        user.setRoles(roles);
        model.addAttribute("user", user);
        model.addAttribute("rolesList", userService.getRoles());
        return "user-info";
    }

    @PostMapping("/admin/saveUser")
    public String saveUser(@RequestParam("roles") String checkedRoles, Model model, @Valid @ModelAttribute("user") User user
            , BindingResult bindingResult) {
        Set<Role> roles = new HashSet<>();
        Set<Role> rolesFromBD = userService.getRoles();
        for (Role role : rolesFromBD) {
            if (checkedRoles.contains(role.getName())) {
                roles.add(role);
            }
        }
        user.setRoles(roles);
        try {
            userService.saveOrChangeUser(user);
        } catch (DataIntegrityViolationException e) {
            if (Objects.requireNonNull(e.getRootCause()).toString().contains('\'' + user.getEmail() + '\'')) {
                bindingResult.addError(new ObjectError("user", user.getEmail() + " уже существует"));
            }
            if (Objects.requireNonNull(e.getRootCause()).toString().contains('\'' + user.getUsername() + '\'')) {
                bindingResult.addError(new ObjectError("user", user.getUsername() + " уже существует"));
            }
        }
        if ((bindingResult.hasFieldErrors("roles") && bindingResult.getErrorCount() > 1)
                || (bindingResult.hasErrors() && !bindingResult.hasFieldErrors("roles"))) {
            model.addAttribute("rolesList", rolesFromBD);
            return "user-info";
        }
        return "redirect:/admin/allUsers";
    }

    @RequestMapping("/admin/updateInfo/{id}")
    public String updateUser(@PathVariable("id") Long id, Model model) {
        User user = userService.getUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("rolesList", userService.getRoles());
        return "user-info";
    }

    @DeleteMapping("/admin/deleteUser/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/allUsers";
    }

    @RequestMapping(value = "/user")
    public String pageForUser(Model model, Principal principal) {
        User user = userService.getUserByUserName(principal.getName());
        model.addAttribute("user", user);
        return "/user";
    }
}
