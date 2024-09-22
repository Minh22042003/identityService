package com.example.identityService.service;

import com.example.identityService.dto.request.AuthenticationRequest;
import com.example.identityService.dto.request.IntrospectRequest;
import com.example.identityService.dto.request.InvalidTokenRequest;
import com.example.identityService.dto.response.AuthenticationResponse;
import com.example.identityService.dto.response.IntrospectResponse;
import com.example.identityService.entity.InvalidToken;
import com.example.identityService.entity.User;
import com.example.identityService.exception.AppException;
import com.example.identityService.exception.ErrorCode;
import com.example.identityService.repository.InvalidTokenRepository;
import com.example.identityService.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    UserRepository userRepository;
    InvalidTokenRepository invalidTokenRepository;

    @NonFinal
    @Value("${jwt.signKey}")
    protected String Signer_key;

    public AuthenticationResponse authenticated(AuthenticationRequest authenticationRequest){
        var user = userRepository.findByUserName(authenticationRequest.getUserName()).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED_BY_USERNAME));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!authenticated){
            throw new AppException(ErrorCode.Authenticated_fail);
        }

        var generate_token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(generate_token)
                .authenticated(true)
                .build();
    }

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        var token = request.getToken();
        var jwtToken = verifyToken(token);
        return IntrospectResponse.builder()
                .valid(true)
                .build();
    }

    public void logOut(InvalidTokenRequest request) throws ParseException, JOSEException {
        var signToken = verifyToken(request.getToken());
        String jwt = signToken.getJWTClaimsSet().getJWTID();
        Date exp = signToken.getJWTClaimsSet().getExpirationTime();
        InvalidToken invalidToken = InvalidToken.builder()
                .id(jwt)
                .exp(exp)
                .build();
        invalidTokenRepository.save(invalidToken);
    }

    private SignedJWT verifyToken(String token) throws JOSEException, ParseException {

        JWSVerifier verifier = new MACVerifier(Signer_key.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify(verifier);
        if (!(verified && expityTime.after(new Date()))){
            throw new AppException(ErrorCode.Authenticated_fail);
        }
        if (!(invalidTokenRepository.findById(signedJWT.getJWTClaimsSet().getJWTID()).isEmpty())){
            throw new AppException(ErrorCode.Authenticated_fail);
        }
        return signedJWT;
    }

    private String generateToken(User user){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserName())
                .issuer("myDomain")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(Signer_key));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can't create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles())){
            user.getRoles().forEach(s -> {
                stringJoiner.add(s.getName());
                if (!CollectionUtils.isEmpty(s.getPermissions())){
                    s.getPermissions().forEach(p -> {
                        stringJoiner.add(p.getName());
                    });
                }
            });
        }
        return stringJoiner.toString();
    }
}
