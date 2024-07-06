package com.example.userms.security;

import com.example.userms.model.entity.User;
import com.example.userms.repository.UserRepository;
import com.example.userms.service.impl.security.JwtService;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final SecurityHelper securityHelper;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authHeader=request.getHeader(HttpHeaders.AUTHORIZATION);

        if (securityHelper.servletPathIsAuth(request) || !securityHelper.authHeaderIsValid(authHeader)){
            filterChain.doFilter(request,response);
            return;
        }

        String jwt=authHeader.substring(7);
        String username=jwtService.extractUsername(jwt);

        if (securityHelper.isJwtUsedFirst(username)){
            User userDetails=userRepository.findUserByUsernameOrEmail(username)
                    .orElseThrow(() -> new RuntimeException("Username doesn't exist: " + username));

            if (jwtService.isTokenValid(jwt,userDetails)){
                UsernamePasswordAuthenticationToken userAuth=new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                userAuth.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(userAuth);
            }
        }
        filterChain.doFilter(request,response);

    }
}







