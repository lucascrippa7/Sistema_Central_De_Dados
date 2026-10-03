# System LC

Base do sistema interno da empresa: cadastro de clientes, senhas de portais
fiscais (Giss Online, Ginfes, Nota do Milhão etc.), permissões por perfil de
usuário e log de auditoria. Feito para rodar na rede interna (intranet),
acessado por qualquer navegador — não precisa instalar nada nos 45
computadores da empresa.

## O que já está pronto nesta etapa

- **Estrutura do projeto** (Spring Boot + Maven)
- **Modelagem do banco de dados** completa (`src/main/resources/db/migration`),
  incluindo: usuários, clientes, CNAEs do cliente, códigos de serviço,
  credenciais de portais e log de auditoria
- **Login e permissões**: tela de login funcionando, senhas de usuário com
  hash (BCrypt), 4 perfis de acesso (`VISUALIZADOR`, `EDITOR`,
  `EDITOR_SENHAS`, `ADMINISTRADOR`) já ligados a regras de acesso por URL
- **Criptografia automática das senhas dos portais** (AES-GCM) — a senha do
  cliente no Giss Online/Ginfes/etc. nunca é gravada em texto puro no banco
- **Tema claro/escuro** com Bootstrap 5 (botão no topo do dashboard)
- **Módulo completo de Clientes**: listar (com busca por nome/UF), cadastrar,
  editar, excluir, e gerenciar CNAEs e códigos de serviço de cada cliente.
  Inclui inscrição municipal, estadual, e responsável (escolhido a partir
  dos usuários cadastrados). Os botões de editar/excluir só aparecem pra
  quem tem permissão, e toda criação/edição/exclusão já cai automaticamente
  no log de auditoria
- **Módulo completo de Usuários** (só administrador): listar, cadastrar
  novo funcionário com senha inicial, editar dados e permissão, redefinir
  senha de qualquer usuário. Qualquer usuário logado também pode trocar a
  própria senha em "Minha conta" (exige a senha atual)
- **Módulo completo de Credenciais**: cada cliente tem sua própria seção de
  credenciais de portais (Giss Online, Ginfes, Nota do Milhão etc.), visível
  só para `ADMINISTRADOR`/`EDITOR_SENHAS`. Senha sempre criptografada no
  banco (AES-GCM), e só aparece em texto quando alguém clica em "revelar" —
  ação que fica registrada no log de auditoria com o que foi visualizado
- **Exportar/Importar Excel**: exporta a lista de clientes (respeitando o
  filtro da tela) para `.xlsx`, com modelo pronto pra baixar. A importação
  lê uma planilha e cadastra vários clientes de uma vez, mostrando um
  relatório de quantas linhas deram certo e o que precisa corrigir nas que
  falharam (linha a linha, sem que uma falha derrube as outras). Antes de
  baixar, dá pra escolher quais colunas entram na planilha (Razão Social
  sempre entra; o resto — CNAEs, cidade, regime, etc. — é opcional). A
  planilha de **usuários** também pode ser exportada (só admin), com o
  mesmo esquema de escolher colunas
- **Módulo completo de Calendário**: grade mensal (estilo Google Calendar)
  com entregas de imposto (vinculadas a um cliente), reuniões e eventos
  gerais. Cada funcionário vê só os próprios eventos; o administrador vê os
  de todo mundo. Suporta:
  - **Recorrência mensal**: marcar "repetir todo mês neste mesmo dia" gera
    automaticamente as ocorrências dos próximos meses, para sempre, até
    alguém pausar a recorrência (o histórico já gerado nunca é apagado
    sozinho)
  - **Notificações**: todo evento avisa automaticamente no próprio dia, e
    dá pra configurar avisos extras com X dias de antecedência (ex: "3, 4"
    dias antes). Os avisos aparecem no sininho no topo da tela (atualiza
    sozinho) e, opcionalmente, por e-mail — o envio por e-mail fica
    desligado por padrão até alguém configurar o SMTP da empresa (veja
    `application.properties`)

## O que já foi melhorado numa segunda rodada

- **Tela inicial (dashboard)** com dados de verdade: clientes ativos, próximos
  eventos dos próximos 7 dias, clientes cadastrados recentemente e atalhos rápidos
- **Tratamento de erro global**: nenhuma tela quebra mais com a "Whitelabel
  Error Page" técnica do Spring — erros esperados (algo não encontrado, sem
  permissão) voltam com um aviso amigável; qualquer erro inesperado mostra
  uma tela educada em vez da tela técnica
- Cada funcionário pode cadastrar/trocar o **próprio e-mail** em "Minha conta",
  sem depender do administrador
- **Paginação** na lista de clientes (20 por página) — a planilha e a
  exportação continuam mostrando tudo de uma vez, de propósito
- Botão **"Meus clientes"** pra filtrar rápido só os clientes de quem está logado
- **Anexos por cliente** (contrato social, procuração, etc) — guardados
  direto no banco, limite de 10 MB por arquivo, visíveis por qualquer
  funcionário, gerenciados por quem já pode editar clientes
- O log de auditoria agora mostra **o que mudou, campo a campo**, quando um
  cliente é editado (ex: `Cidade: 'Guarulhos' -> 'São Paulo'`), não só que
  "o cliente foi editado"
- Primeiros **testes automatizados** do projeto (ver seção própria abaixo)

## Módulo Reforma Tributária (simulador de regime)

Tela nova em `/reforma-tributaria`: compara a carga tributária atual do
cliente (Simples Nacional, Lucro Presumido ou Lucro Real) com a carga
projetada depois da Reforma Tributária (CBS/IBS), e dá uma recomendação
textual considerando o quanto das vendas é B2B (que tende a aproveitar
crédito) versus consumidor final.

**Importante:** é uma estimativa de referência, não substitui a análise do
contador — os valores usados (alíquota de referência CBS/IBS, faixas do
Simples, fator de absorção do crédito B2B) estão documentados no código
(`SimulacaoRegimeApplicationService`) e vão precisar de ajuste conforme a
regulamentação da reforma for avançando até 2033. Ainda não persiste nada
no banco (Fase 1) — as próximas fases (cadastro de fornecedores + situação
cadastral, leitura de XML de NF-e para crédito real) ficam pra depois.

Fica isolado no pacote `com.empresa.sistema.reforma`, sem depender de
`Cliente`/`ClienteUseCase` — só usa o que o projeto já tinha, nenhuma
dependência nova no `pom.xml`.

## O que ainda falta (ideias pra próxima rodada)

- Autoatendimento de permissões mais granular (hoje é só por perfil, ver
  `TipoAcesso`)
- Mais testes automatizados, cobrindo os módulos que ainda não têm (Usuário,
  Credencial, Excel)
- Deploy de verdade (hoje roda local, pela IDE) — ver seção "Backup e
  produção" abaixo

## Como rodar localmente

### Pré-requisitos
- Java 17+
- Maven
- PostgreSQL rodando (local ou em um servidor da empresa)

### Passo a passo

1. Crie o banco de dados:
   ```sql
   CREATE DATABASE sistema_clientes;
   ```

2. Edite `src/main/resources/application.properties` com o usuário/senha do
   seu PostgreSQL (ou defina como variáveis de ambiente).

3. Defina a chave de criptografia (usada só para as senhas dos portais).
   **Não deixe isso no application.properties em produção**:
   ```bash
   export APP_CHAVE_CRIPTOGRAFIA="uma-chave-bem-forte-e-secreta"
   ```

3.1. (Opcional) Para as notificações de calendário saírem também por
   e-mail, configure o SMTP da empresa em `application.properties`
   (`spring.mail.*`) e mude `app.notificacoes.email-habilitado` para
   `true`. Sem isso, as notificações continuam funcionando normalmente
   dentro do sistema (sininho no topo da tela) — só o e-mail fica desligado.

4. Rode o projeto:
   ```bash
   mvn spring-boot:run
   ```

5. Acesse `http://localhost:8080` (ou o IP do servidor, para os outros
   computadores da rede).

6. Primeiro login (o Flyway já cria esse usuário automaticamente):
   - **Usuário:** `admin`
   - **Senha:** `TrocarSenha123!`

   Troque essa senha assim que a tela de Usuários (próxima etapa) estiver
   pronta.

## Estrutura de pastas (Clean Architecture)

O código está organizado em camadas concêntricas. A regra de ouro: **uma
camada só pode depender de camadas mais internas, nunca o contrário.**

```
src/main/java/com/empresa/sistema/
│
├── domain/                    ← camada mais interna, o coração do sistema
│   ├── model/                  Cliente, Usuario, ClienteCnae... objetos Java
│   │                           puros, SEM anotação de JPA ou Spring
│   └── exception/               Erros de negócio (ClienteNaoEncontradoException)
│
├── application/               ← as regras de negócio (casos de uso)
│   ├── port/in/                 Interfaces que a infraestrutura chama
│   │                           (ex: ClienteUseCase)
│   ├── port/out/                Interfaces que a aplicação espera do mundo
│   │                           externo, sem saber como são implementadas
│   │                           (ex: ClienteRepositoryPort)
│   └── service/                 Implementação das regras de negócio de
│                               verdade (ex: ClienteApplicationService)
│
└── infrastructure/            ← tudo que depende de framework, fica isolado
    ├── persistence/
    │   ├── entity/               Entidades JPA (ClienteJpaEntity...) —
    │   │                         só essa pasta sabe que existe Hibernate
    │   ├── repository/           Interfaces Spring Data JPA
    │   ├── mapper/                Converte entre domínio e entidade JPA
    │   └── adapter/               Implementa as portas de saída usando JPA
    ├── security/                 Spring Security, login, UserDetails
    └── web/controller/           Controllers Thymeleaf (HTTP)
```

**Por que separar assim?** Se um dia você quisesse trocar o PostgreSQL por
outro banco, ou até tirar o Thymeleaf e botar uma API REST na frente, só a
pasta `infrastructure/` seria tocada — as regras de negócio em `domain/` e
`application/` não sabem (e não precisam saber) que Spring ou JPA existem.

**Bug corrigido nessa refatoração:** editar um cliente que já tinha CNAEs ou
códigos de serviço cadastrados estava apagando esses dados, porque o
formulário de edição só manda os campos básicos de volta. Isso já foi
corrigido em `ClienteApplicationService.salvar()` — agora os CNAEs e códigos
existentes são preservados quando você só edita os dados básicos do cliente.

## Sobre segurança das senhas dos portais

- A senha de cada credencial (Giss Online, Ginfes, Nota do Milhão...) é
  criptografada com AES-GCM antes de ir para o banco — veja
  `infrastructure/persistence/converter/CriptografiaConverter.java`.
- Quem pode **ver** essa senha em texto é qualquer funcionário logado (é uso
  interno da empresa). O que continua controlado por perfil
  (`ADMINISTRADOR` e `EDITOR_SENHAS`) é quem pode **cadastrar/remover**
  credenciais — e, separadamente, quem pode **editar** o cadastro do cliente
  em si. Toda vez que alguém revela uma senha, isso fica registrado no log
  de auditoria (`/auditoria`), então dá pra ver quem olhou o quê.
- Guarde a chave de criptografia (`APP_CHAVE_CRIPTOGRAFIA`) em local seguro,
  fora do controle de versão. Se ela for perdida, as senhas já salvas não
  podem ser recuperadas.

## Testes automatizados

O projeto tem testes unitários dos pontos de negócio mais arriscados de
quebrar sem perceber: as regras de recorrência e permissão do calendário
(`EventoApplicationServiceTest`) e o histórico de auditoria campo a campo dos
clientes (`ClienteApplicationServiceTest`). Eles não sobem Spring nem banco de
dados — só testam a regra de negócio isolada, mockando as portas de saída
(`ClienteRepositoryPort`, `EventoRepositoryPort`, etc), então rodam em
segundos.

Pra rodar:

```bash
mvn test
```

**Isso ainda não cobre o projeto inteiro** — os módulos de Usuário, Credencial
e Excel não têm testes ainda. É uma boa próxima etapa: sempre que corrigir um
bug relatado (como os que já corrigimos nas conversas anteriores), vale a pena
escrever um teste que reproduza o problema antes de corrigir, pra ele não
voltar sem ninguém perceber.

## Backup e produção

Este projeto ainda roda em modo desenvolvimento (pela IDE, banco local). Antes
de colocar as 45 pessoas usando de verdade, valem estes cuidados:

**Backup do banco.** Todos os dados do sistema — incluindo os anexos de
cliente, que agora ficam guardados direto no banco — estão no PostgreSQL.
Configure uma rotina de backup automática, por exemplo com `pg_dump`:

```bash
# Backup diário (agendar via Agendador de Tarefas do Windows ou cron/systemd
# se o banco rodar em Linux):
pg_dump -U postgres -F c -f backup_sistema_clientes_%data%.dump sistema_clientes
```

Guarde os backups em outro lugar (não no mesmo disco do servidor) — um HD
externo, outro servidor, ou um serviço de nuvem já resolve.

**Rodar como serviço, não pela IDE.** Hoje o sistema sobe quando alguém aperta
"play" no IntelliJ. Pra produção, gere o `.jar` (`mvn clean package`) e rode
com `java -jar`, configurado pra iniciar sozinho com o Windows/Linux (via NSSM
no Windows, ou um serviço systemd no Linux) — assim ele continua no ar mesmo
se a máquina reiniciar.

**HTTPS.** Sendo uma intranet, dá pra viver sem por enquanto, mas se alguém
for acessar de fora da rede da empresa (ex: home office), vale colocar atrás
de um proxy reverso (nginx, Caddy) com certificado HTTPS — sem isso, login e
senha trafegam sem criptografia na rede.

**Variáveis sensíveis.** Confirme que `APP_CHAVE_CRIPTOGRAFIA`, a senha do
banco e a senha do SMTP (se ligar e-mail) NUNCA ficam no `application.properties`
versionado — sempre como variável de ambiente do servidor de produção.
