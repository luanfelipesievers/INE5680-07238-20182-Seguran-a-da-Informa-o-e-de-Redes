# Tarefa Prática de Criptografia Assimétrica: Chat Seguro usando Sockets baseado no protocolo “The Double Ratchet Algorithm”

Você deve implementar um Chat Seguro usando Sockets. Alice e Bob devem conseguir se comunicar usando este Chat. Alice e Bob possuem, cada um, um par de chaves da criptografia assimétrica (chave pública e chave privada).
As seguintes etapas devem acontecer na implementação:
1. As chaves assimétricas devem ser criadas ou lidas de um arquivo. Toda a comunicação entre Alice e Bob deve acontecer usando sockets;
2. Deve acontecer um processo de autenticação mútua entre Alice e Bob usando o RSA. Você deve decidir como fazer a autenticação mútua entre ambos. Veja no material da disciplina o que significa “autenticação usando criptografia assimétrica”;
3. Depois da autenticação mútua, deve ser usado o Diffie-Hellmann (DH) para estabelecer chave de sessão simétrica entre Alice e Bob. Essas chaves DH devem formar uma “catraca” de chaves, de acordo com o documento https://signal.org/docs/specifications/doubleratchet/, seção 2.3 (figura abaixo), explicado em sala de aula;
4. Existem vários exemplos de códigos que podem ser usados:
a. O projeto Sockets (classes Cliente e Servidor) tem exemplo de uso de Sockets em Java.
b. Também existe o exemplo de código do projeto testeOAEPRSA para ver como funciona o uso do RSA em Java. NÃO use o BaseRSAExample.java. USE APENAS o OAEPPaddedRSAExample.java como base para usar o RSA.
c. O projeto testeSignature (PKCS1SignatureExample.java) tem exemplo de assinatura digital usando o RSA com SHA256.
d. O projeto testeDH (BasicDHExample.java) tem exemplo de funcionamento do DH.
5. Se for preciso criar algum parâmetro, use a derivação de chaves (PBKDF2) para isso. Não é permitido ter parâmetros criptográficos (chaves, IVs) fixos no código.
6. Alice e Bob possuem chaves públicas e/ou certificados digitais. Alice e Bob devem conhecer a chave pública um do outro para verificar a assinatura, caso seja usada. Por exemplo, a chave pública pode ser lida de um arquivo em formato PEM (você pode criar a chave pública com o openssl, caso queira e seja possível). A chave pública e/ou certificados digitais podem ser transferidos de Alice para Bob e vice-versa, para que ambos conheçam a chave um do outro.
7. Se você quiser usar o keystore Java para armazenar as chaves usadas, olhe o código disponível nos exemplos. O keystore Java é um arquivo especial Java para armazenar chaves.
