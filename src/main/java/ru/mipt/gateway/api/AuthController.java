package ru.mipt.gateway.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mipt.gateway.dto.LoginRequest;
import ru.mipt.gateway.dto.LoginResponse;
import ru.mipt.gateway.security.JwtTokenProvider;

/**
 * Handles user authentication.  Accepts a username/password pair, verifies
 * credentials via Spring Security's AuthenticationManager, and returns
 * a signed JWT on success.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final String pepper;

    public AuthController(AuthenticationManager authManager,
                          JwtTokenProvider tokenProvider,
                          @Value("${app.pepper}") String pepper) {
        this.authManager = authManager;
        this.tokenProvider = tokenProvider;
        this.pepper = pepper;
    }

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param request login credentials
     * @return JWT token wrapped in a LoginResponse, or 401 ProblemDetail
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password() + pepper));

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String token = tokenProvider.generateToken(userDetails);
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException ex) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED, "Invalid username or password");
            problem.setTitle("Authentication Failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
        }
    }
}
