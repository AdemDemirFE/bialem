package com.bialem.backend.service;

import com.bialem.backend.config.Constants;
import com.bialem.backend.domain.Authority;
import com.bialem.backend.domain.Profile;
import com.bialem.backend.domain.Role;
import com.bialem.backend.domain.User;
import com.bialem.backend.domain.UserRole;
import com.bialem.backend.repository.AuthorityRepository;
import com.bialem.backend.repository.ProfileRepository;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.repository.UserRoleRepository;
import com.bialem.backend.security.AuthoritiesConstants;
import com.bialem.backend.security.PasswordResetTokenHasher;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.service.dto.AdminUserDTO;
import com.bialem.backend.service.dto.UserDTO;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import tech.jhipster.security.RandomUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    /** Password-reset tokens expire after 30 minutes. */
    public static final int PASSWORD_RESET_TOKEN_VALIDITY_MINUTES = 30;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthorityRepository authorityRepository;

    private final CacheManager cacheManager;

    private final ProfileProvisioningService profileProvisioningService;
    private final SuperAdminProvisioningService superAdminProvisioningService;
    private final ProfileRepository profileRepository;
    private final UserRoleRepository userRoleRepository;

    public UserService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthorityRepository authorityRepository,
        CacheManager cacheManager,
        ProfileProvisioningService profileProvisioningService,
        SuperAdminProvisioningService superAdminProvisioningService,
        ProfileRepository profileRepository,
        UserRoleRepository userRoleRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
        this.cacheManager = cacheManager;
        this.profileProvisioningService = profileProvisioningService;
        this.superAdminProvisioningService = superAdminProvisioningService;
        this.profileRepository = profileRepository;
        this.userRoleRepository = userRoleRepository;
    }

    public Optional<User> activateRegistration(String key) {
        LOG.debug("Activating user for activation key {}", key);
        return userRepository
            .findOneByActivationKey(key)
            .map(user -> {
                // activate given user for the registration key.
                user.setActivated(true);
                user.setActivationKey(null);
                this.clearUserCaches(user);
                LOG.debug("Activated user: {}", user);
                return user;
            });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
        LOG.debug("Reset user password for reset key");
        String normalized = PasswordResetTokenHasher.normalizeResetSecret(key);
        if (StringUtils.isBlank(normalized)) {
            return Optional.empty();
        }
        String tokenHash = PasswordResetTokenHasher.hashToken(normalized);
        return userRepository
            .findOneByResetKey(tokenHash)
            .filter(user ->
                user.getResetDate() != null &&
                user.getResetDate().isAfter(Instant.now().minus(PASSWORD_RESET_TOKEN_VALIDITY_MINUTES, ChronoUnit.MINUTES))
            )
            .map(user -> {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setResetKey(null);
                user.setResetDate(null);
                user.setClearResetKey(null);
                this.clearUserCaches(user);
                return user;
            });
    }

    public Optional<User> requestPasswordReset(String mail) {
        return userRepository
            .findOneByEmailIgnoreCase(mail)
            .filter(User::isActivated)
            .map(user -> {
                String rawToken = PasswordResetTokenHasher.generateResetCode();
                user.setResetKey(PasswordResetTokenHasher.hashToken(rawToken));
                user.setResetDate(Instant.now());
                user.setClearResetKey(rawToken);
                this.clearUserCaches(user);
                return user;
            });
    }

    public User registerUser(AdminUserDTO userDTO, String password) {
        userRepository
            .findOneByLogin(userDTO.getLogin().toLowerCase())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new UsernameAlreadyUsedException();
                }
            });
        userRepository
            .findOneByEmailIgnoreCase(userDTO.getEmail())
            .ifPresent(existingUser -> {
                boolean removed = removeNonActivatedUser(existingUser);
                if (!removed) {
                    throw new EmailAlreadyUsedException();
                }
            });
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        // new user gets initially a generated password
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            newUser.setEmail(userDTO.getEmail().toLowerCase());
        }
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        newUser.setActivated(true);
        newUser.setActivationKey(null);
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        superAdminProvisioningService.ensureRequiredAuthorities(newUser);
        this.clearUserCaches(newUser);
        profileProvisioningService.createForUser(newUser, userDTO.getFirstName(), userDTO.getLogin());
        LOG.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
        this.clearUserCaches(existingUser);
        return true;
    }

    public User createUser(AdminUserDTO userDTO) {
        if (userDTO.getAuthorities() == null || userDTO.getAuthorities().isEmpty()) {
            userDTO.setAuthorities(Set.of(AuthoritiesConstants.USER));
        }
        validateAuthorityMutation(null, userDTO);
        User user = new User();
        user.setLogin(userDTO.getLogin().toLowerCase());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail().toLowerCase());
        }
        user.setImageUrl(userDTO.getImageUrl());
        if (userDTO.getLangKey() == null) {
            user.setLangKey(Constants.DEFAULT_LANGUAGE); // default language
        } else {
            user.setLangKey(userDTO.getLangKey());
        }
        String encryptedPassword = passwordEncoder.encode(RandomUtil.generatePassword());
        user.setPassword(encryptedPassword);
        String rawResetToken = PasswordResetTokenHasher.generateRawToken();
        user.setResetKey(PasswordResetTokenHasher.hashToken(rawResetToken));
        user.setClearResetKey(rawResetToken);
        user.setResetDate(Instant.now());
        user.setActivated(true);
        if (userDTO.getAuthorities() != null) {
            Set<Authority> authorities = userDTO
                .getAuthorities()
                .stream()
                .map(authorityRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            user.setAuthorities(authorities);
        }
        userRepository.save(user);
        superAdminProvisioningService.ensureRequiredAuthorities(user);
        this.clearUserCaches(user);
        profileProvisioningService.createForUser(user, userDTO.getFirstName(), userDTO.getLogin());
        LOG.debug("Created Information for User: {}", user);
        return user;
    }

    /**
     * Update all information for a specific user, and return the modified user.
     *
     * @param userDTO user to update.
     * @return updated user.
     */
    public Optional<AdminUserDTO> updateUser(AdminUserDTO userDTO) {
        return Optional.of(userRepository.findById(userDTO.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(user -> {
                validateAuthorityMutation(user, userDTO);
                this.clearUserCaches(user);
                user.setLogin(userDTO.getLogin().toLowerCase());
                user.setFirstName(userDTO.getFirstName());
                user.setLastName(userDTO.getLastName());
                if (userDTO.getEmail() != null) {
                    user.setEmail(userDTO.getEmail().toLowerCase());
                }
                user.setImageUrl(userDTO.getImageUrl());
                user.setActivated(userDTO.isActivated());
                user.setLangKey(userDTO.getLangKey());
                Set<Authority> managedAuthorities = user.getAuthorities();
                managedAuthorities.clear();
                userDTO
                    .getAuthorities()
                    .stream()
                    .map(authorityRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(managedAuthorities::add);
                userRepository.save(user);
                superAdminProvisioningService.ensureRequiredAuthorities(user);
                this.clearUserCaches(user);
                LOG.debug("Changed Information for User: {}", user);
                return user;
            })
            .map(AdminUserDTO::new);
    }

    public void deleteUser(String login) {
        userRepository
            .findOneByLogin(login)
            .ifPresent(user -> {
                if (superAdminProvisioningService.isProtected(user) || (hasAuthority(user, AuthoritiesConstants.SUPER_ADMIN) && !isSuperAdmin())) {
                    throw forbidden();
                }
                profileProvisioningService.deleteForUser(user);
                userRepository.delete(user);
                this.clearUserCaches(user);
                LOG.debug("Deleted User: {}", user);
            });
    }

    public void setActivated(Long id, boolean activated) {
        User user = userRepository.findOneWithAuthoritiesById(id).orElseThrow();
        if (!activated && (superAdminProvisioningService.isProtected(user) || (hasAuthority(user, AuthoritiesConstants.SUPER_ADMIN) && !isSuperAdmin()))) {
            throw forbidden();
        }
        user.setActivated(activated);
        userRepository.save(user);
        clearUserCaches(user);
        LOG.info("Management action USER_{} targetUserId={} actor={}", activated ? "ACTIVATE" : "DEACTIVATE", id,
            SecurityUtils.getCurrentUserLogin().orElse("unknown"));
    }

    public AdminUserDTO setAuthority(Long id, String authorityName) {
        Set<String> allowed = Set.of(
            AuthoritiesConstants.USER, AuthoritiesConstants.COMMUNITY_MANAGER, AuthoritiesConstants.EVENT_MANAGER,
            AuthoritiesConstants.MODERATOR, AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPER_ADMIN
        );
        if (!allowed.contains(authorityName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown platform role.");
        }
        User user = userRepository.findOneWithAuthoritiesById(id).orElseThrow();
        if (superAdminProvisioningService.isProtected(user) && !AuthoritiesConstants.SUPER_ADMIN.equals(authorityName)) throw forbidden();
        if (!isSuperAdmin() && (hasAuthority(user, AuthoritiesConstants.SUPER_ADMIN) || AuthoritiesConstants.SUPER_ADMIN.equals(authorityName))) throw forbidden();
        Authority authority = authorityRepository.findById(authorityName)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown platform role."));
        user.getAuthorities().clear();
        user.getAuthorities().add(authority);
        userRepository.save(user);
        clearUserCaches(user);
        LOG.info("Management action USER_ROLE_CHANGE targetUserId={} authority={} actor={}", id, authorityName,
            SecurityUtils.getCurrentUserLogin().orElse("unknown"));
        return new AdminUserDTO(user);
    }

    /**
     * Update basic information (first name, last name, email, language) for the current user.
     *
     * @param firstName first name of user.
     * @param lastName  last name of user.
     * @param email     email id of user.
     * @param langKey   language key.
     * @param imageUrl  image URL of user.
     */
    public void updateUser(String firstName, String lastName, String email, String langKey, String imageUrl) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                user.setFirstName(firstName);
                user.setLastName(lastName);
                if (email != null) {
                    user.setEmail(email.toLowerCase());
                }
                user.setLangKey(langKey);
                user.setImageUrl(imageUrl);
                userRepository.save(user);
                this.clearUserCaches(user);
                LOG.debug("Changed Information for User: {}", user);
            });
    }

    @Transactional
    public void changePassword(String currentClearTextPassword, String newPassword) {
        SecurityUtils.getCurrentUserLogin()
            .flatMap(userRepository::findOneByLogin)
            .ifPresent(user -> {
                String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new InvalidPasswordException();
                }
                String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                this.clearUserCaches(user);
                LOG.debug("Changed password for User: {}", user);
            });
    }

    @Transactional(readOnly = true)
    public Page<AdminUserDTO> getAllManagedUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllPublicUsers(Pageable pageable) {
        return userRepository.findAllByIdNotNullAndActivatedIsTrue(pageable).map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthoritiesByLogin(String login) {
        return userRepository.findOneWithAuthoritiesByLogin(login);
    }

    @Transactional(readOnly = true)
    public Optional<AdminUserDTO> getManagedUser(Long id) {
        return userRepository.findOneWithAuthoritiesById(id).map(AdminUserDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        return SecurityUtils.getCurrentUserLogin().flatMap(userRepository::findOneWithAuthoritiesByLogin);
    }

    /**
     * Not activated users should be automatically deleted after 3 days.
     * <p>
     * This is scheduled to get fired every day, at 01:00 (am).
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void removeNotActivatedUsers() {
        userRepository
            .findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant.now().minus(3, ChronoUnit.DAYS))
            .forEach(user -> {
                LOG.debug("Deleting not activated user {}", user.getLogin());
                userRepository.delete(user);
                this.clearUserCaches(user);
            });
    }

    /**
     * Gets a list of all the authorities.
     * @return a list of all the authorities.
     */
    @Transactional(readOnly = true)
    public List<String> getAuthorities() {
        return authorityRepository.findAll().stream().map(Authority::getName).toList();
    }

    @Transactional(readOnly = true)
    public Set<String> findAppRoleCodesForUser(User user) {
        if (user == null || user.getId() == null) {
            return Set.of();
        }
        Optional<Profile> profileOpt = profileRepository.findOneByUser_Id(user.getId());
        if (profileOpt.isEmpty()) {
            return Set.of();
        }
        return userRoleRepository
            .findByUser(profileOpt.orElseThrow())
            .stream()
            .map(UserRole::getRole)
            .filter(Objects::nonNull)
            .map(Role::getCode)
            .collect(Collectors.toSet());
    }

    private void clearUserCaches(User user) {
        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE)).evictIfPresent(user.getLogin());
        if (user.getEmail() != null) {
            Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE)).evictIfPresent(user.getEmail());
        }
    }

    private void validateAuthorityMutation(User existing, AdminUserDTO requested) {
        Set<String> requestedAuthorities = requested.getAuthorities() == null ? Set.of() : requested.getAuthorities();
        Set<String> allowedAuthorities = Set.of(
            AuthoritiesConstants.USER, AuthoritiesConstants.COMMUNITY_MANAGER, AuthoritiesConstants.EVENT_MANAGER,
            AuthoritiesConstants.MODERATOR, AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPER_ADMIN
        );
        if (requestedAuthorities.size() != 1 || !allowedAuthorities.containsAll(requestedAuthorities)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A user can have only one platform role.");
        }
        boolean actorIsSuperAdmin = isSuperAdmin();
        if (!actorIsSuperAdmin && requestedAuthorities.contains(AuthoritiesConstants.SUPER_ADMIN)) throw forbidden();
        if (existing != null && !actorIsSuperAdmin) {
            boolean protectedTarget = superAdminProvisioningService.isProtected(existing) || hasAuthority(existing, AuthoritiesConstants.SUPER_ADMIN);
            boolean removesSuperAdmin = hasAuthority(existing, AuthoritiesConstants.SUPER_ADMIN) && !requestedAuthorities.contains(AuthoritiesConstants.SUPER_ADMIN);
            if (protectedTarget && (!requested.isActivated() || removesSuperAdmin)) throw forbidden();
        }
        if (existing != null && superAdminProvisioningService.isProtected(existing)) {
            if (!requested.isActivated() || !requestedAuthorities.contains(AuthoritiesConstants.SUPER_ADMIN)) throw forbidden();
        }
    }

    private static boolean hasAuthority(User user, String authority) {
        return user.getAuthorities().stream().anyMatch(value -> authority.equals(value.getName()));
    }

    private static boolean isSuperAdmin() {
        return SecurityUtils.hasCurrentUserThisAuthority(AuthoritiesConstants.SUPER_ADMIN);
    }

    private static ResponseStatusException forbidden() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "SUPER ADMIN hesabı veya yetkisi değiştirilemez.");
    }
}
