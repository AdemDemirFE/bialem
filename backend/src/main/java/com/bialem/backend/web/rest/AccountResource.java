package com.bialem.backend.web.rest;

import com.bialem.backend.domain.User;
import com.bialem.backend.repository.UserRepository;
import com.bialem.backend.security.PasswordPolicy;
import com.bialem.backend.security.SecurityUtils;
import com.bialem.backend.service.MailService;
import com.bialem.backend.service.PasswordResetRateLimiter;
import com.bialem.backend.service.UserService;
import com.bialem.backend.service.dto.AdminUserDTO;
import com.bialem.backend.service.dto.PasswordChangeDTO;
import com.bialem.backend.web.rest.errors.*;
import com.bialem.backend.web.rest.vm.KeyAndPasswordVM;
import com.bialem.backend.web.rest.vm.ManagedUserVM;
import com.bialem.backend.web.rest.vm.PasswordResetMessageVM;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static class AccountResourceException extends RuntimeException {

        private AccountResourceException(String message) {
            super(message);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(AccountResource.class);

    private final UserRepository userRepository;

    private final UserService userService;

    private final MailService mailService;

    private final PasswordResetRateLimiter passwordResetRateLimiter;

    public AccountResource(
        UserRepository userRepository,
        UserService userService,
        MailService mailService,
        PasswordResetRateLimiter passwordResetRateLimiter
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.passwordResetRateLimiter = passwordResetRateLimiter;
    }

    /**
     * {@code POST  /register} : register the user.
     *
     * @param managedUserVM the managed user View Model.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the password is incorrect.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws LoginAlreadyUsedException {@code 400 (Bad Request)} if the login is already used.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (!PasswordPolicy.isValid(managedUserVM.getPassword())) {
            throw new PasswordPolicyException();
        }
        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        mailService.sendActivationEmail(user);
    }

    /**
     * {@code GET  /activate} : activate the registered user.
     *
     * @param key the activation key.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be activated.
     */
    @GetMapping("/activate")
    public void activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new AccountResourceException("No user was found for this activation key");
        }
    }

    /**
     * {@code GET  /account} : get the current user.
     *
     * @return the current user.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user couldn't be returned.
     */
    @GetMapping("/account")
    public AdminUserDTO getAccount() {
        return userService
            .getUserWithAuthorities()
            .map(user -> new AdminUserDTO(user, userService.findAppRoleCodesForUser(user)))
            .orElseThrow(() -> new AccountResourceException("User could not be found"));
    }

    /**
     * {@code POST  /account} : update the current user information.
     *
     * @param userDTO the current user information.
     * @throws EmailAlreadyUsedException {@code 400 (Bad Request)} if the email is already used.
     * @throws RuntimeException {@code 500 (Internal Server Error)} if the user login wasn't found.
     */
    @PostMapping("/account")
    public void saveAccount(@Valid @RequestBody AdminUserDTO userDTO) {
        String userLogin = SecurityUtils.getCurrentUserLogin()
            .orElseThrow(() -> new AccountResourceException("Current user login not found"));
        Optional<User> existingUser = userRepository.findOneByEmailIgnoreCase(userDTO.getEmail());
        if (existingUser.isPresent() && (!existingUser.orElseThrow().getLogin().equalsIgnoreCase(userLogin))) {
            throw new EmailAlreadyUsedException();
        }
        Optional<User> user = userRepository.findOneByLogin(userLogin);
        if (!user.isPresent()) {
            throw new AccountResourceException("User could not be found");
        }
        userService.updateUser(
            userDTO.getFirstName(),
            userDTO.getLastName(),
            userDTO.getEmail(),
            userDTO.getLangKey(),
            userDTO.getImageUrl()
        );
    }

    /**
     * {@code POST  /account/change-password} : changes the current user's password.
     *
     * @param passwordChangeDto current and new password.
     * @throws InvalidPasswordException {@code 400 (Bad Request)} if the new password is incorrect.
     */
    @PostMapping(path = "/account/change-password")
    public void changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        if (!PasswordPolicy.isValid(passwordChangeDto.getNewPassword())) {
            throw new PasswordPolicyException();
        }
        userService.changePassword(passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
    }

    /**
     * {@code POST   /account/reset-password/init} : Send an email to reset the password of the user.
     *
     * @param mail the mail of the user.
     */
    @PostMapping(path = "/account/reset-password/init")
    public PasswordResetMessageVM requestPasswordReset(@RequestBody String mail, HttpServletRequest request) {
        String email = normalizeEmailPayload(mail);
        passwordResetRateLimiter.checkAllowed(email, resolveClientIp(request));

        Optional<User> user = userService.requestPasswordReset(email);
        if (user.isPresent()) {
            mailService.sendPasswordResetMail(user.orElseThrow());
        } else {
            // Pretend the request has been successful to prevent checking which emails really exist
            // but log that an invalid attempt has been made
            LOG.warn("Password reset requested for non existing mail");
        }
        return PasswordResetMessageVM.accepted();
    }

    /**
     * {@code POST   /account/reset-password/finish} : Finish to reset the password of the user.
     *
     * @param keyAndPassword the generated key and the new password.
     * @throws PasswordPolicyException {@code 400 (Bad Request)} if the password policy fails.
     * @throws InvalidResetKeyException {@code 400 (Bad Request)} if the reset key is invalid or expired.
     */
    @PostMapping(path = "/account/reset-password/finish")
    public void finishPasswordReset(@RequestBody KeyAndPasswordVM keyAndPassword) {
        if (!PasswordPolicy.isValid(keyAndPassword.getNewPassword())) {
            throw new PasswordPolicyException();
        }
        if (
            StringUtils.isNotBlank(keyAndPassword.getConfirmPassword()) &&
            !Objects.equals(keyAndPassword.getNewPassword(), keyAndPassword.getConfirmPassword())
        ) {
            throw new BadRequestAlertException("Şifreler birbiriyle eşleşmiyor.", "userManagement", "passwordmismatch");
        }
        Optional<User> user = userService.completePasswordReset(keyAndPassword.getNewPassword(), keyAndPassword.getKey());

        if (user.isEmpty()) {
            throw new InvalidResetKeyException();
        }
    }

    private static String normalizeEmailPayload(String mail) {
        if (mail == null) {
            return "";
        }
        String value = mail.trim();
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        return value.trim();
    }

    private static String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
