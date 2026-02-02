package com.david.readme.services;

import com.david.readme.dtos.AuthResponse;
import com.david.readme.dtos.LoginRequest;
import com.david.readme.dtos.RegisterRequest;
import com.david.readme.exceptions.ResourceNotFoundException;
import com.david.readme.exceptions.UnauthorizedException;
import com.david.readme.exceptions.UsernameAlreadyExistsException;
import com.david.readme.models.Cart;
import com.david.readme.models.User;
import com.david.readme.repositories.CartRepository;
import com.david.readme.repositories.UserRepository;
import com.david.readme.utils.AuthUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;

    public AuthService(UserRepository usr, CartRepository cartRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, AuthUtil authUtil) {
        this.userRepository = usr;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.authUtil = authUtil;
    }

    public AuthResponse register(RegisterRequest registerRequest) {

        if(userRepository.existsByUsername(registerRequest.username())){
            throw new UsernameAlreadyExistsException("Username is already in use");
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        Cart cart = new Cart();
        cart.setUser(savedUser);

        cartRepository.save(cart);

        return new AuthResponse(
            savedUser.getId(),
            savedUser.getUsername(),
            savedUser.getRole(),
            "Successfully registered new user"
        );
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // get user details
            User user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            return new AuthResponse(
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    "Login successful"
            );

        } catch (Exception e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    public AuthResponse getCurrentUser() {
        User user = authUtil.getCurrentUser();

        return new AuthResponse(
            user.getId(),
            user.getUsername(),
            user.getRole(),
            "User retrieved successfully"
        );
    }

}
