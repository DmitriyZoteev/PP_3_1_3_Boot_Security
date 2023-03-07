package ru.kata.spring.boot_security.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.RoleService;

@Controller
@RequestMapping(value = "/admin")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping(value = "/allRoles")
    public String getRoles(Model model) {
        model.addAttribute("rolesList", roleService.getRoles());
        return "roles";
    }

    @RequestMapping("/addNewRole")
    public String addNewRole(Model model) {
        model.addAttribute("role", new Role());
        return "roles-info";
    }

    @PostMapping("/saveRole")
    public String saveRole(@ModelAttribute("role") Role role) {
        roleService.saveOrChangeRole(role);
        return "redirect:/admin/allRoles";
    }

    @RequestMapping("/updateInfoRole/{id}")
    public String updateRole(@PathVariable("id") Long id, Model model) {
        Role role = roleService.getRoleById(id);
        model.addAttribute("role", role);
        return "roles-info";
    }

    @DeleteMapping("/deleteRole/{id}")
    public String deleteRole(@PathVariable("id") Long id) {
        roleService.deleteRole(id);
        return "redirect:/admin/allRoles";
    }
}
