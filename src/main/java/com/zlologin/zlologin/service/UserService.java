package com.zlologin.zlologin.service;

import com.zlologin.zlologin.dto.RegisterDTO;
import com.zlologin.zlologin.model.User;
import com.zlologin.zlologin.model.Role;
import com.zlologin.zlologin.repository.UserRepository;
import com.zlologin.zlologin.util.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, JavaMailSender mailSender) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.mailSender = mailSender;
    }

    // Método de registro de novos usuários
    public User registerUser(RegisterDTO registerDTO) {
        if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("E-mail já registrado!");
        }

        User newUser = new User();
        newUser.setEmail(registerDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        newUser.setRole(Role.valueOf(registerDTO.getRole().toUpperCase()));

        return userRepository.save(newUser);
    }

    // Implementação do método exigido pelo Spring Security
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();
    }

    // Método para envio do token de redefinição de senha
    public void sendPasswordResetToken(String email) {
        // Verifica se o usuário existe
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + email));

        // Gera o token JWT para a redefinição de senha
        String token = jwtTokenProvider.generateTokenWithExpiry(email, 15); // Token válido por 15 minutos

        // Monta o link de redefinição de senha
        String resetLink = frontendUrl + "/reset-password?token=" + token;

        // Envia o e-mail com o link de redefinição
        sendEmail(email, resetLink);
    }

    // Método auxiliar para enviar o e-mail
    private void sendEmail(String recipientEmail, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(recipientEmail);
            helper.setSubject("Redefinição de Senha");
            helper.setText("<p>Para redefinir sua senha, clique no link abaixo:</p>" +
                    "<p><a href=\"" + resetLink + "\">Redefinir Senha</a></p>" +
                    "<br><p>Este link é válido por 15 minutos.</p>", true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail de redefinição de senha", e);
        }
    }

    // Método para redefinir a senha usando o token de redefinição
    public void resetPassword(String token, String newPassword) {
        // Valida o token e extrai o e-mail do usuário
        String email = jwtTokenProvider.getUserEmailFromToken(token);
        if (email == null) {
            throw new IllegalArgumentException("Token inválido ou expirado.");
        }

        // Encontra o usuário pelo e-mail
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado para o e-mail fornecido."));

        // Atualiza a senha com a nova senha (codificada)
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
