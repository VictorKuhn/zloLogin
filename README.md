
# ZLO Login - Microserviço de Autenticação

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=VictorKuhn_zloLogin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=VictorKuhn_zloLogin)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=VictorKuhn_zloLogin&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=VictorKuhn_zloLogin)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=VictorKuhn_zloLogin&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=VictorKuhn_zloLogin)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=VictorKuhn_zloLogin&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=VictorKuhn_zloLogin)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=VictorKuhn_zloLogin&metric=bugs)](https://sonarcloud.io/summary/new_code?id=VictorKuhn_zloLogin)

## O que é o ZLO Login?
O ZLO Login é um microserviço de autenticação robusto, escalável e independente, desenvolvido para atender a plataforma ZLO. Ele foi projetado para garantir alta disponibilidade, segurança avançada e facilidade de integração com os módulos da aplicação.

A independência do ZLO Login como microserviço permite que ele continue operando mesmo se outros módulos da plataforma enfrentarem problemas, garantindo autenticações seguras e controle de acesso contínuo. Este projeto também adota as melhores práticas de segurança, utilizando tokens JWT, integração segura com o AWS Elastic Beanstalk e PostgreSQL, além de auditorias detalhadas para monitoramento de acessos.

---

## Funcionalidades Principais
- **Autenticação Segura com JWT**: Todos os usuários recebem tokens JWT que incluem informações como expiração, ROLE e identificadores únicos.
- **Gestão de Usuários Temporários**: Permite acessos rápidos com validade limitada a 15 minutos, renováveis automaticamente.
- **Recuperação de Senhas**: Suporte a redefinição de senhas com tokens de segurança via e-mail.
- **Controle Granular de Permissões**: Acesso baseado em papéis (RESPONSÁVEL, CUIDADOR, ADMIN) com verificação nos endpoints.
- **Auditoria de Acessos**: Registros detalhados de todos os acessos e renovações de tokens, garantindo rastreabilidade e conformidade.
- **Escalabilidade na Nuvem**: Integração com a AWS, garantindo que o sistema suporte crescimento sem comprometimento de desempenho.

---

## Estrutura do Projeto
```plaintext
src/
├── main/
│   ├── java/
│   │   └── com.zlologin.zlologin/
│   │       ├── config/        # Configurações de segurança e sistema
│   │       ├── controller/    # Endpoints REST expostos
│   │       ├── dto/           # Objetos de transferência de dados
│   │       ├── exception/     # Manipulação de erros e exceções
│   │       ├── model/         # Entidades do banco de dados
│   │       ├── repository/    # Operações com o banco de dados
│   │       ├── service/       # Regras de negócio
│   │       └── util/          # Métodos auxiliares
│   └── resources/
│       ├── static/            # Arquivos estáticos
│       ├── templates/         # Templates de e-mail
│       └── application.properties  # Configurações do Spring
└── test/
    └── java/
```

---

## Endpoints Disponíveis
Os endpoints foram projetados para atender a diversas operações, mantendo segurança e flexibilidade.

### 1. Registro de um Novo Usuário
**Endpoint**: `POST {HOST}/auth/register`  
**Descrição**: Cria um novo usuário com base nas informações fornecidas.

**Exemplo de Requisição**:
```json
{
  "email": "usuario@example.com",
  "password": "senhaSegura123",
  "role": "RESPONSÁVEL"
}
```

**Respostas**:
- **200 OK**: Usuário criado com sucesso.
- **Erros**:
  - **409 Conflict**: Usuário já existe.
  - **400 Bad Request**: Dados inválidos.

---

### 2. Login de Usuário
**Endpoint**: `POST {HOST}/auth/login`  
**Descrição**: Autentica o usuário e retorna um token JWT.

**Exemplo de Requisição**:
```json
{
  "email": "usuario@example.com",
  "password": "senhaSegura123"
}
```

**Respostas**:
- **200 OK**: Token JWT retornado com sucesso.
- **Erros**:
  - **403 Forbidden**: Credenciais inválidas.
  - **400 Bad Request**: Formato da requisição inválido.

---

### 3. Acessar Dashboard por Role
**Endpoint**: `GET {HOST}/api/{role}/dashboard`  
**Descrição**: Retorna uma página específica com base na ROLE do usuário.

**Autorização**: Token JWT no cabeçalho da requisição.

**Respostas**:
- **200 OK**: Acesso permitido.
- **403 Forbidden**: Acesso negado.

---

### 4. Recuperação de Senha
**Endpoint**: `POST {HOST}/auth/forgot-password`  
**Descrição**: Envia um e-mail com link para redefinição de senha.

**Exemplo de Requisição**:
```json
{
  "email": "usuario@example.com"
}
```

**Respostas**:
- **200 OK**: E-mail enviado com sucesso.
- **Erros**:
  - **404 Not Found**: E-mail não encontrado.

---

### 5. Redefinição de Senha
**Endpoint**: `POST {HOST}/auth/reset-password`  
**Descrição**: Atualiza a senha com base no token JWT.

**Exemplo de Requisição**:
```json
{
  "token": "jwtTokenValido",
  "newPassword": "novaSenha123"
}
```

**Respostas**:
- **200 OK**: Senha alterada com sucesso.
- **403 Forbidden**: Token JWT inválido ou expirado.

---

### 6. Criar Usuário Temporário
**Endpoint**: `POST {HOST}/auth/temp-user`  
**Descrição**: Gera um token JWT temporário com validade de 15 minutos.

**Exemplo de Requisição**:
```json
{
  "email": "tempuser@example.com",
  "phoneNumber": "5547999999999"
}
```

**Respostas**:
- **200 OK**: Token gerado com sucesso.
- **400 Bad Request**: Dados inválidos.

---

## Escalabilidade e Segurança
O ZLO Login foi projetado com base em princípios de escalabilidade e segurança:  
- **Hospedagem AWS**: Elastic Beanstalk para fácil implantação e RDS PostgreSQL para persistência robusta.  
- **Segurança**: Uso de grupos de segurança AWS, roles específicas e criptografia de senhas (BCrypt).  
- **Auditoria**: Registro de todas as ações de usuários temporários para rastreabilidade.

---

## Tecnologias Utilizadas
- **Java 17** com **Spring Boot**: Framework moderno para desenvolvimento robusto.  
- **PostgreSQL**: Banco de dados relacional de alta performance.  
- **AWS Elastic Beanstalk**: Gerenciamento de infraestrutura automatizado.

---

## Contribuições
Contribuições são bem-vindas! Para contribuir, abra uma *issue* ou envie um *pull request*.

---

## Aplicação disponível para acesso em:
http://zlo-login-microservice-env-2.eba-cm4nxyyj.us-east-1.elasticbeanstalk.com/{endpointDesejado}

## Desenvolvedores
- **Victor Hugo Bosse Kühn**
- [Perfil no LinkedIn](https://www.linkedin.com/in/victorbkuhn/)
