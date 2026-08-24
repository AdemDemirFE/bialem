package com.bialem.backend.web.rest;

import com.bialem.backend.security.AuthoritiesConstants;
import com.bialem.backend.service.ManagementAuthorizationService;
import com.bialem.backend.service.ManagementDashboardService;
import com.bialem.backend.service.UserService;
import com.bialem.backend.service.dto.ManagementContextDTO;
import com.bialem.backend.service.dto.ManagementDashboardDTO;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_SUPER_ADMIN')")
public class ManagementResource {
    private final ManagementAuthorizationService authorization;
    private final ManagementDashboardService dashboard;
    private final UserService users;
    public ManagementResource(ManagementAuthorizationService authorization, ManagementDashboardService dashboard, UserService users) {
        this.authorization = authorization; this.dashboard = dashboard; this.users = users;
    }

    @GetMapping("/context")
    public ManagementContextDTO context() {
        var authorities = users.getUserWithAuthorities().orElseThrow().getAuthorities().stream().map(a -> a.getName()).collect(Collectors.toSet());
        return new ManagementContextDTO(true, authorities.contains(AuthoritiesConstants.SUPER_ADMIN), authorities,
            authorization.currentPermissions().stream().map(Enum::name).collect(Collectors.toSet()));
    }

    @GetMapping("/dashboard")
    public ManagementDashboardDTO dashboard() { return dashboard.getDashboard(); }

    @GetMapping("/users/{id}")
    public ResponseEntity<com.bialem.backend.service.dto.AdminUserDTO> getAdminUserById(@PathVariable("id") Long id) {
        return ResponseUtil.wrapOrNotFound(users.getManagedUser(id));
    }

    @PostMapping("/users/{id}/activate")
    @PreAuthorize("@managementAuthorization.has(T(com.bialem.backend.service.ManagementAuthorizationService.Permission).USER_ACTIVATE)")
    public void activate(@PathVariable Long id) { users.setActivated(id, true); }

    @PostMapping("/users/{id}/deactivate")
    @PreAuthorize("@managementAuthorization.has(T(com.bialem.backend.service.ManagementAuthorizationService.Permission).USER_ACTIVATE)")
    public void deactivate(@PathVariable Long id) { users.setActivated(id, false); }

    @PutMapping("/users/{id}/authority")
    @PreAuthorize("@managementAuthorization.has(T(com.bialem.backend.service.ManagementAuthorizationService.Permission).USER_EDIT)")
    public com.bialem.backend.service.dto.AdminUserDTO setAuthority(@PathVariable Long id, @RequestBody AuthorityRequest request) {
        return users.setAuthority(id, request.authority());
    }

    public record AuthorityRequest(String authority) {}
}
