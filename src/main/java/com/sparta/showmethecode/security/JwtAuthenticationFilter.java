package com.sparta.showmethecode.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
//        log.info(authorization);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
//            log.info("token: {}", token);

            if (SecurityContextHolder.getContext().getAuthentication() == null && jwtUtils.isValidToken(token)) {
                TokenDto userInfo = jwtUtils.getUserInfo(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userInfo.getUsername());
//                log.info("userDetails username: {}", userDetails.getUsername());
//                log.info("userDetails authority: {}", userDetails.getAuthorities());
                Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                for (GrantedAuthority authority : authorities) {
//                    log.info("filter authority: {}", authority.getAuthority());
                }

                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else {
                if (SecurityContextHolder.getContext().getAuthentication() != null){
//                    log.info("authentication: {}", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
                }
            }
        }

        filterChain.doFilter(request, response);

    }
}
