package com.zlologin.zlologin.service;

import com.zlologin.zlologin.model.TempUser;
import com.zlologin.zlologin.repository.TempUserRepository;
import com.zlologin.zlologin.util.JwtTokenProvider;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TempUserService {

    private final TempUserRepository tempUserRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public TempUserService(TempUserRepository tempUserRepository, JwtTokenProvider jwtTokenProvider) {
        this.tempUserRepository = tempUserRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String createTempUser(String email, String phoneNumber) {
        // Gera o token JWT para o usuário temporário
        String token = jwtTokenProvider.generateTempUserToken(email, 15);

        // Verifica se já existe um registro com o mesmo email
        Optional<TempUser> existingTempUser = tempUserRepository.findByEmail(email);

        if (existingTempUser.isPresent()) {
            TempUser tempUser = existingTempUser.get();
            // Verifica se o token está expirado
            if (tempUser.getExpiresAt().isAfter(LocalDateTime.now())) {
                // Atualiza o registro existente se o token ainda for válido
                tempUser.setJwtToken(token);
                tempUser.setCreatedAt(LocalDateTime.now());
                tempUser.setExpiresAt(LocalDateTime.now().plusMinutes(15));
                tempUser.setPhoneNumber(phoneNumber); // Atualiza o telefone caso tenha mudado
                tempUserRepository.save(tempUser);
                return token;
            }
        }

        // Cria um novo registro se o token estiver expirado ou não houver registro existente
        TempUser newTempUser = new TempUser();
        newTempUser.setEmail(email);
        newTempUser.setPhoneNumber(phoneNumber);
        newTempUser.setJwtToken(token);
        newTempUser.setCreatedAt(LocalDateTime.now());
        newTempUser.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        newTempUser.setRole("ROLE_TEMPUSER");

        tempUserRepository.save(newTempUser);

        return token;
    }
}
