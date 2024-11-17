# ZLO Login - Microserviço de Autenticação

### O que é o ZLO Login?
O ZLO Login é uma solução em formato de microserviço pensada em servir toda a aplicação ZLO, uma plataforma de identificação e ajuda a pessoas com doenças as quais os tornam dependentes. Por fato de ser uma aplicação com diversos módulos, a autenticação foi pensada de forma independente, para que não tenham possibilidades de cair juntamente aos demais módulos da aplicação e assim tornando-a eficiente e segura.

### Preparação de ambiente para a aplicação:
1. Confira alguns dos requisitos necessários para poder rodar a aplicação em sua máquina local:
    - Possuir em seu ambiente de desenvolvimento JAVA 17 como predefinida, assim como também o Maven
    - Banco de dados PostgreSQL instalado e inicializado com um banco de dados "zloLogin" criado
    - Postman ou outro software para enviar requisições ao serviço
2. Importe o projeto em sua IDE e confira detalhadamente os dados de conexão com o banco de dados local (Arquivo application.properties no projeto)
3. Inicie o projeto em sua IDE de preferência e confira no console os LOGs acerca da inicialização do serviço Spring Boot
4. Quando iniciado corretamente o serviço, envie as requisições desejadas especificadas a seguir.

### Endpoints disponíveis

1. Registro de um novo usuário:
{HOST}/auth/register

Tipo da requisição: POST
Corpo da requisição (JSON):
```json
{
  "email": "<emailDesejado>",
  "password": "<senhaDesejada>",
  "role": "RESPONSÁVEL | CUIDADOR | ADMIN"
}
```
- Retorno esperado: STATUS 200 OK | Usuário criado com sucesso!
- Exceções:
    - Tentativa de criação de usuário duplicado
    - Corpo da requisição inválido / formato não suportado
 
2. Login de usuário:
{HOST}/auth/login

Tipo da requisição: POST
Corpo da requisição (JSON):

```
{
  "email": "<emailCorreto>",
  "password": "<senhaCorreta>"
}
```

- Retorno esperado: STATUS 200 OK | Retorno do token JWT referente ao Login!
- Exceções:
    - Credenciais inválidas
    - 403 Forbidden caso a sessão (Token JWT) tenha expirado
    - Corpo da requisição inválido / formato não suportado

3. Acessar endpoint de acordo com o nível de usuário/role (Apenas para fins de teste de permissão da ROLE em específico):
Nessa parte temos disponíveis três endpoints, um para ADMIN, outro para CUIDADOR e outro para RESPONSÁVEL, substitua de acordo com o nível de usuário que você optou por cadastrar.

{HOST}api/<nivelAcesso>/dashboard

Tipo da requisição: GET
Envie a requisição com o Token JWT no Header como "Bearer <tokenJWT>", ou na aba "Authorization" com o formato Bearer Token, e o token no campo designado.

- Retorno esperado: STATUS 200 OK | Bem-vindo ao dashboard <nivelAcesso>!
- Exceções:
    - 403 Forbidden caso você tente entrar em algum dashboard que não seja o do seu nível de usuário
    - 403 Forbidden caso o Token JWT esteja incorreto
 
### Acesso ao endpoint público:
O endpoint público dessa aplicação pode estar ou não disponível dependendo da fase de desenvolvimento, visando redução de custos com nuvem, portanto, em caso de disponibilidade, você pode testar todos os endpoints citados anteriormente pelo seguinte URL:
`http://zlo-login-microservice-env-2.eba-cm4nxyyj.us-east-1.elasticbeanstalk.com`/<endpointDesejado>

4. Recuperação de senha sem estar logado (Envio de e-mail para recuperação através de link):

{HOST}/auth/forgot-password

Tipo da requisição: POST
Envie a requisição com um JSON no body, com o seguinte corpo na requisição:

```
{
  "email": "<emailDesejado>"
}
```

Lembrando que nesse caso deve-se enviar na requisição um e-mail previamente cadastrado.

5. Redefinir senha com base no token e nova senha desejada, nesse caso o token deve ser pego ou através da geração no Login, ou no token que é enviado no link do e-mail de recuperação.

{HOST}/auth/reset-password

Tipo da requisição: POST
Envie a requisição com um JSON no body, com o seguinte corpo da requisição:

```
{
  "token": "<tokenJwtValido>",
  "newPassword": "<novaSenhaDesejada>"
}
```

## Agradecimentos

Agradeço por dedicar seu tempo para ler sobre este projeto de microserviço de autenticação! Espero que ele seja útil e agregue valor ao seu trabalho ou aprendizado. Qualquer feedback é bem-vindo e ficarei feliz em conectar!

**Desenvolvedor:** [Victor Hugo Bosse Kühn](https://www.linkedin.com/in/victorbkuhn/)
