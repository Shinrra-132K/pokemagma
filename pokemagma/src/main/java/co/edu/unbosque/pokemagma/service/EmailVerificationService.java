package co.edu.unbosque.pokemagma.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.springframework.stereotype.Service;

import co.edu.unbosque.pokemagma.model.EmailVerificationToken;
import co.edu.unbosque.pokemagma.model.User;
import co.edu.unbosque.pokemagma.repository.EmailVerificationTokenRepository;
import co.edu.unbosque.pokemagma.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class EmailVerificationService {
	
	private static final Duration CODE_TTL = Duration.ofMinutes(10);
	private static final int MAX_ATTEMPTS = 5;
	
	private final SecureRandom random = new SecureRandom();
	private final EmailVerificationTokenRepository tokenRepo;
	private final UserRepository userRepo;
	private final EmailService emailService;
	
	public EmailVerificationService(EmailVerificationTokenRepository tokenRepo, UserRepository userRepo,
			EmailService emailService) {
		super();
		this.tokenRepo = tokenRepo;
		this.userRepo = userRepo;
		this.emailService = emailService;
	}
	
	@Transactional
	public void sendCode(User user) {
		String code = String.format("%06d", random.nextInt(1_000_000));

		EmailVerificationToken t = tokenRepo.findByUser(user).orElseGet(EmailVerificationToken::new);
		t.setUser(user);
		t.setToken(code);
		t.setExpiresAt(Instant.now().plus(CODE_TTL));
		t.setAttemps(0);
		tokenRepo.save(t);

		emailService.sendVerificationCode(user.getEmail(), user.getUsername(), code);
	}
	
	@Transactional
	public int resend(String email) {
		Optional<User> found = userRepo.findByEmail(email);
		if (found.isEmpty()) {
			return 1;
		}
		if (found.get().isEmailVerified()) {
			return 2;
		}
		sendCode(found.get());
		return 0;
	}
	
	@Transactional
	public int verify(String email, String code) {
		Optional<User> found = userRepo.findByEmail(email);
		if (found.isEmpty()) {
			return 1;
		}
		User user = found.get();
		if (user.isEmailVerified()) {
			return 0;
		}

		Optional<EmailVerificationToken> opt = tokenRepo.findByUser(user);
		if (opt.isEmpty()) {
			return 1;
		}
		EmailVerificationToken t = opt.get();

		if (t.getExpiresAt().isBefore(Instant.now())) {
			return 2;
		}
		if (t.getAttemps() >= MAX_ATTEMPTS) {
			return 4;
		}

		boolean matches = MessageDigest.isEqual(t.getToken().getBytes(StandardCharsets.UTF_8),
				code.getBytes(StandardCharsets.UTF_8));
		if (!matches) {
			t.setAttemps(t.getAttemps() + 1); // se guarda al hacer commit
			return 3;
		}

		user.verifyEmail();
		userRepo.save(user);
		tokenRepo.delete(t);
		return 0;
	}
	
	
}
