package com.campusbridge.auth;

import com.campusbridge.auth.dto.AuthResponse;
import com.campusbridge.auth.dto.InvitationCodeResponse;
import com.campusbridge.auth.dto.LoginRequest;
import com.campusbridge.auth.dto.RegisterRequest;
import com.campusbridge.auth.dto.RegisterResponse;
import com.campusbridge.common.BusinessRuleException;
import com.campusbridge.common.NotFoundException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    static final Duration VERIFICATION_VALIDITY = Duration.ofHours(24);

    private final UserRepository users;
    private final InvitationCodeService invitationCodes;
    private final EmailDomainPolicy domainPolicy;
    private final EmailSender emailSender;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;
    private final String verificationLinkBase;

    public AuthServiceImpl(UserRepository users, InvitationCodeService invitationCodes,
                           EmailDomainPolicy domainPolicy, EmailSender emailSender,
                           TokenService tokenService, PasswordEncoder passwordEncoder, Clock clock,
                           @Value("${campusbridge.auth.verification-link-base}") String verificationLinkBase) {
        this.users = users;
        this.invitationCodes = invitationCodes;
        this.domainPolicy = domainPolicy;
        this.emailSender = emailSender;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
        this.clock = clock;
        this.verificationLinkBase = verificationLinkBase;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (users.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessRuleException("EMAIL_IN_USE", "An account with this e-mail already exists");
        }
        boolean institutional = domainPolicy.isInstitutional(request.email());
        boolean hasCode = request.invitationCode() != null && !request.invitationCode().isBlank();
        if (!institutional && !hasCode) {
            throw new BusinessRuleException("NOT_ELIGIBLE",
                    "Use your @etu.edu.tr e-mail or an invitation code from a local student");
        }
        if (!institutional && request.userType() != UserType.ERASMUS) {
            throw new BusinessRuleException("NOT_ELIGIBLE", "Invitation codes are only for Erasmus students");
        }

        Instant now = clock.instant();
        User user = new User(request.email(), passwordEncoder.encode(request.password()), request.userType(), now);
        String token = UUID.randomUUID().toString();
        user.startVerification(token, now.plus(VERIFICATION_VALIDITY));
        user = users.save(user);

        if (!institutional) {
            invitationCodes.consume(request.invitationCode(), user);
        }
        emailSender.sendVerificationEmail(user.getEmail(), verificationLinkBase + "?token=" + token);
        return new RegisterResponse(user.getId(), "Verification e-mail sent");
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        User user = users.findByVerificationToken(token)
                .orElseThrow(() -> new NotFoundException("Verification link is not valid"));
        if (!user.verify(token, clock.instant())) {
            throw new BusinessRuleException("LINK_EXPIRED", "Verification link has expired");
        }
    }

    /** noRollbackFor: the failed-attempt counter must be saved even when login fails. */
    @Override
    @Transactional(noRollbackFor = BusinessRuleException.class)
    public AuthResponse login(LoginRequest request) {
        Instant now = clock.instant();
        User user = users.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new BusinessRuleException("BAD_CREDENTIALS", "Wrong e-mail or password"));
        if (user.isLocked(now)) {
            throw new BusinessRuleException("ACCOUNT_LOCKED", "Too many failed attempts. Try again in 15 minutes");
        }
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.registerFailedLogin(now);
            throw new BusinessRuleException("BAD_CREDENTIALS", "Wrong e-mail or password");
        }
        if (!user.isActive()) {
            throw new BusinessRuleException("NOT_VERIFIED", "Please verify your e-mail first");
        }
        user.registerSuccessfulLogin();
        return new AuthResponse(tokenService.issueToken(user), user.getId(), user.getUserType());
    }

    @Override
    @Transactional
    public InvitationCodeResponse createInvitationCode(Long userId) {
        User creator = users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        InvitationCode code = invitationCodes.generate(creator);
        return new InvitationCodeResponse(code.getCode(), code.getExpiresAt());
    }
}
