package com.example.userms.service.impl;


import com.example.commonexception.enums.ExceptionsEnum;
import com.example.commonexception.exceptions.GeneralException;
import com.example.userms.model.dto.request.AuthenticationRequest;
import com.example.userms.model.dto.request.EmailRequestDto;
import com.example.userms.model.dto.request.PasswordRequestDto;
import com.example.userms.model.dto.request.UserRequestDto;
import com.example.userms.model.dto.response.AuthenticationResponse;
import com.example.userms.model.entity.ConfirmationToken;
import com.example.userms.model.entity.Role;
import com.example.userms.model.entity.User;
import com.example.userms.model.enums.Constant;
import com.example.userms.model.enums.RoleType;
import com.example.userms.repository.ConfirmationTokenRepository;
import com.example.userms.repository.RoleRepository;
import com.example.userms.repository.UserRepository;
import com.example.userms.security.SecurityHelper;
import com.example.userms.service.IUserService;
import com.example.userms.service.impl.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.commonnotification.dto.request.KafkaRequest;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService, IUserService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SecurityHelper securityHelper;
    private final KafkaTemplate<String, KafkaRequest> kafkaTemplate;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByUsernameOrEmail(username);

        return new org.springframework.security.core.userdetails.User(
                user.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username))
                        .getUsername(),
                user.get().getPassword(),
                Collections.singleton(new SimpleGrantedAuthority(user.get().getRole().getName().name())));
    }

    @Override
    public ResponseEntity<String> saveUser(UserRequestDto request) {
        Role role = roleRepository.findRoleByName(RoleType.USER)
                .orElseThrow(() -> new GeneralException(ExceptionsEnum.ROLE_NOT_FOUND));
        Optional<User> userByUsernameOrEmail = userRepository.findUserByUsername(request.getUsername().trim());
        if (userByUsernameOrEmail.isPresent()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username already defined!");
        }

        saveUserToDatabase(request, role,"confirm-topic");
        log.info("{} -> user created",request.getUsername());
        return ResponseEntity.ok().body(Constant.SAVE_IS_SUCCESSFULLY.getMessage());
    }

    private void saveUserToDatabase(UserRequestDto request, Role role,String topicName) {
        User user = User.builder()
                .username(request.getUsername().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail().trim())
                .role(role)
                .enabled(true)
                .build();
        userRepository.save(user);

        sendConfirmationLink(UUID.randomUUID().toString(),user,topicName);
    }

    @Override
    public ResponseEntity<AuthenticationResponse> authenticateUser(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            log.error("Authentication failed: {}", e.getMessage());
            if (e.getMessage().equals("User is disabled")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            } else if (e.getMessage().equals("Bad credentials")){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }

        Optional<User> userOptional = userRepository.findUserByUsernameOrEmail(request.getUsername());
        User user = userOptional.orElseThrow(() -> new GeneralException(ExceptionsEnum.USER_NOT_FOUND));

        String accessToken=jwtService.generateToken(user);
        String refreshToken=jwtService.generateRefreshToken(user);

        log.info("{} -> user loging",request.getUsername());
        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .message(user.getEmail() + " login is successfully")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return ResponseEntity.ok().body(authenticationResponse);
    }

    //-----Renew Password------
    @Override
    public ResponseEntity<String> sendOTP(String token) {
        securityHelper.authHeaderIsValid(token);
        String jwt = token.substring(7);
        if (!jwtService.isTokenExpired(jwt)) {
            String username = jwtService.extractUsername(jwt);
            Optional<User> user = userRepository.findUserByUsernameOrEmail(username);
            String otp = createAndSaveOtp(user);

            KafkaRequest kafkaRequest = KafkaRequest.builder()
                    .email(user.orElseThrow(() -> new GeneralException(ExceptionsEnum.Token_NOT_FOUND)).getEmail())
                    .token(otp)
                    .build();
            kafkaTemplate.send("otp-topic",kafkaRequest);
            return ResponseEntity.ok().body("We are send OTP to " + user.get().getEmail());
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired!");
        }
    }

    private String createAndSaveOtp(Optional<User> user) {
        Random random = new Random();
        String otp = String.valueOf(random.nextInt(9000) + 1000);

        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(otp)
                .user(user.orElseThrow(() -> new GeneralException(ExceptionsEnum.USER_NOT_FOUND)))
                .createdAt(LocalDateTime.now())
                .build();
        confirmationTokenRepository.save(confirmationToken);
        return otp;
    }

    @Override
    public ResponseEntity<String> checkOtp(String username,String otp) {
        Optional<User> user = userRepository.findUserByUsernameOrEmail(username);
        if (user.isEmpty()){
            throw new GeneralException(ExceptionsEnum.USERNAME_NOT_FOUND);
        }

        Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findConfirmationTokenByToken(otp);
        if (confirmationToken.isPresent()){
            return ResponseEntity.ok().body("Otp is true");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Otp is wrong");
        }
    }

    //----------------------------

    @Override
    public ResponseEntity<String> resetsPassword(String token, PasswordRequestDto passwordRequestDto) {
        if (passwordRequestDto.getNewPassword().equals(passwordRequestDto.getRepeatPassword())){
            User user = findUserWithToken(token);
            if (user == null){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
            } else if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("");
            }

            resetUserPassword(user,passwordRequestDto.getNewPassword());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Password changing is successfully!");
        }else {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body("Password changing is not successfully!");
        }
    }

    @Override
    public ResponseEntity<String> checkResetToken(String token) {
        Optional<ConfirmationToken> userByToken = confirmationTokenRepository.findConfirmationTokenByToken(token);
        if (userByToken.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } else {
            return ResponseEntity.ok().body("");
        }
    }

    private User findUserWithToken(String token) {
        Optional<ConfirmationToken> userByToken = confirmationTokenRepository.findConfirmationTokenByToken(token);
        if (userByToken.isEmpty()){
            return null;
        }
        ConfirmationToken confirmationToken = userByToken.get();
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);

        return confirmationToken.getUser();
    }

    private void resetUserPassword(User user,String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public ResponseEntity<String> checkEmailInDatabase(String email){
        log.info("email -> {}",email);
        Optional<User> user = userRepository.findUserByEmail(email);
        if (user.isEmpty()){
            log.info("User from database -> {}",user.orElseThrow(() -> new GeneralException(ExceptionsEnum.USER_NOT_FOUND))
                    .getUsername());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } else if (!user.get().isEnabled()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String token = UUID.randomUUID().toString();
        saveConfirmationToken(token,user.get());

        KafkaRequest kafkaRequest = KafkaRequest.builder()
                .email(user.get().getEmail())
                .token(token)
                .build();
        kafkaTemplate.send("email-topic",kafkaRequest);
        return ResponseEntity.ok().body("We send link to your email for password changing");

    }

    @Override
    public AuthenticationResponse refreshToken(String token) {
        if (!securityHelper.authHeaderIsValid(token)){
            throw new GeneralException(ExceptionsEnum.TOKEN_IS_WRONG);
        }

        String jwt = token.substring(7);
        String username = jwtService.extractUsername(jwt);
        Optional<User> userByName = userRepository.findUserByUsernameOrEmail(username);

        if (username != null){
            if (userByName.orElseThrow(() -> new GeneralException(ExceptionsEnum.USER_NOT_FOUND))
                    .getUsername().equals(username)){
                if(jwtService.isTokenValid(jwt,userByName.orElseThrow())){
                    String accessToken=jwtService.generateToken(userByName.get());
                    String refreshToken=jwtService.generateRefreshToken(userByName.get());

                    log.info("{} token refreshing is successfully",username);
                    return AuthenticationResponse.builder()
                            .message("Refreshing is successfully!")
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .build();
                }
            }
        }else {
            throw new GeneralException(ExceptionsEnum.USERNAME_IS_NULL);
        }
        return null;
    }

    @Override
    public void sendConfirmationLink(String token,User user,String topicName) {
        KafkaRequest request = KafkaRequest.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .token(token)
                .build();
        saveConfirmationToken(token, user);
        kafkaTemplate.send(topicName,request);
    }

    @Override
    public ResponseEntity<String> sendConfirmationLinkToUser(EmailRequestDto requestDto) {
        Optional<User> userByEmail = userRepository.findUserByUsernameOrEmail(requestDto.getEmail());

        if (userByEmail.isPresent() && !userByEmail.get().isEnabled()){
            Optional<ConfirmationToken> confirmationTokenByUser = confirmationTokenRepository.findConfirmationTokenByUser(userByEmail.get());
            if (confirmationTokenByUser.isPresent() && !confirmationTokenByUser.get().isExpire()){
                confirmationTokenByUser.get().setExpire(true);
                confirmationTokenRepository.save(confirmationTokenByUser.get());
            }

            sendConfirmationLink(UUID.randomUUID().toString(),userByEmail.get(),"confirm-topic");
            return ResponseEntity.ok().body("Email sent");
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }
    }

    private void saveConfirmationToken(String token, User user) {
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .token(token)
                .build();
        confirmationTokenRepository.save(confirmationToken);
        log.info("Confirmation token saved => {}",confirmationToken.getToken());
    }

    @Override
    public ResponseEntity<String> confirmAccount(String token) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findConfirmationTokenByToken(token);

        if (confirmationToken.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");
        } else if (confirmationToken.get().isExpire()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        } else if(confirmationToken.get().getConfirmedAt() != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Token already confirmed");
        }

        Optional<User> user = userRepository.findUserByUsernameOrEmail(confirmationToken.orElseThrow(() -> new RuntimeException("Token not found!"))
                .getUser().getUsername());
        user.orElseThrow(() -> new GeneralException(ExceptionsEnum.USER_NOT_FOUND))
                .setEnabled(true);
        confirmationToken.get().setConfirmedAt(LocalDateTime.now());

        confirmationTokenRepository.save(confirmationToken.get());
        userRepository.save(user.get());
        return ResponseEntity.ok().body("User confirmed!");
    }

    @Override
    public String passwordSetPage(String token) {
        Optional<ConfirmationToken> confirmationToken = confirmationTokenRepository.findConfirmationTokenByToken(token);
        Optional<User> user = userRepository.findUserByUsernameOrEmail(confirmationToken.orElseThrow(() -> new RuntimeException("Token not found!"))
                                                                            .getUser().getUsername());
        user.orElseThrow(() -> new GeneralException(ExceptionsEnum.USER_NOT_FOUND))
                .setEnabled(true);

        return user.get().getUsername() + " plese send POST request to '/auth/set-password' link!";
    }
}
