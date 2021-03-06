package com.slashmobility.UserProoviders.infraestructure.config.auth;

import com.slashmobility.UserProoviders.application.query.QueryUser;
import com.slashmobility.UserProoviders.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
import java.util.ArrayList;
import java.util.Optional;

//Para filtrar todas las peticiones y validar el jwt
@Component
public class JWTFilterRequest extends OncePerRequestFilter {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private QueryUser queryUser;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizatonHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizatonHeader != null && authorizatonHeader.startsWith("Bearer")) {
            String jwt = authorizatonHeader.substring(7).trim();
            String username = jwtUtil.extractUserName(jwt);

            //Se verifica que no exista autenticación para este usuario
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = this.queryUser.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt, userDetails)) {
                    //Se envian todos los roles para el usuario
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    //Se le agrega los detalle de la conexión
                    //Esto con el fin de poder evluar el navegador, el horario, el S.O
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //Para que después no tenga que pasar todas estas validaciones
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            }
        }
        //Se valida el filtro
        filterChain.doFilter(request, response);
    }
}
