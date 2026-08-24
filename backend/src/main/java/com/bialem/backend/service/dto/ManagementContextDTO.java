package com.bialem.backend.service.dto;

import java.util.Set;

public record ManagementContextDTO(boolean managementAccess, boolean superAdmin, Set<String> authorities, Set<String> permissions) {}
