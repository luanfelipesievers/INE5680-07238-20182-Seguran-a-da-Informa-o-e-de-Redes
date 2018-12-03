## Parte teórica
Descrever as escolhas feitas.

Utilizamos o framework Spring Boot devido ao seu amplo uso no mercado, assim como o framework mybatis.

Utilizamos uma base de dados temporária (h2) por se tratar de um exercício.

Conforme o guia do site [CrackStastion](https://crackstation.net/hashing-security.htm), o hash da senha foi feito com a geração de um Salt, utilizando ainda o CSPRNG do Java SecureRandom.

**Conforme instruído no site:**

*Na criação:*
- Geramos um Salt
- Appendamos o Salt a senha
- Salvamos ambos na base dados

*Na validação:*
- Obtem-se a senha com salt da base
- Aplica-se a mesma função da criação para a verificação da nova senha.
- Comparamos o hash da nova senha com a da base.

Ainda adicionamos a função

        boolean slowEquals(byte[] a, byte[] b)

Que tem como objetivo dificultar o crack das senhas através da comparação lenta entre as senhas, para que mesmo uma GPU rápida seja inútil visto que a comparação lenta será sempre feita.


## Setup  
O projeto foi feito com Maven, portanto a importação em qualquer IDE se dá pela importação de Maven Project.  
  
**No IntelliJ IDEA**

 - *Import Project*
 - Selecionar a pasta do projeto
 - Selecionar *Import project from external model*
 - Selecionar *Maven* e clique em **next**
 - Na próxima aba as configurações *default* podem permanecer do jeito que estão, clique em **next** até a ultima aba para finalizar a importação

**Lombok**
O projeto utiliza o plugin Lombok. A instalação do plugin deve ser realizada em ordem do funcionamento do projeto.
[Lombok Project Website](https://projectlombok.org/) 

**Insomnia**
O insomnia foi utilizado para testar as chamadas rest:

    curl --request POST \
      --url http://localhost:8080/insert \
      --header 'content-type: application/json' \
      --data '{
    	"username":"luansievers",
    	"password":"123456789"
    }'
    
    curl --request POST \
      --url http://localhost:8080/authenticate \
      --header 'content-type: application/json' \
      --data '{
    	"username":"luansievers",
    	"password":"987654321"
    }'

    curl --request PUT \
      --url http://localhost:8080/update/1 \
      --header 'content-type: application/json' \
      --data '{
    	"username":"luansievers",
    	"password":"987654321"
    }'

    curl --request DELETE \
      --url http://localhost:8080/delete/1 \
      --header 'content-type: application/json'
