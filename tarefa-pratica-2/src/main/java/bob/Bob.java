package bob;

import utils.PrintUtils;
import utils.UtilsColors;
import utils.crypto.DH;
import utils.crypto.RSA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.security.SecureRandom;


public class Bob {

    private RSA rsa = new RSA();
    private DH dh = new DH();
    private DH.DHBob dhBob = dh.new DHBob();

    private Socket socket;

    private PrintUtils printUtils = new PrintUtils();

    private ObjectInputStream streamInObject;
    private ObjectOutputStream streamOutObject;

    private PublicKey chavePublicaAliceRSA;

    public Bob() throws Exception {
        realizarAberturaEConexaoSocket();
        realizaTrocaDeChavesPublicasRSA();
        realizarAutenticacaoMutua();
        realizaTrocaDeChavesPublicasDH();
    }

    private void realizarAberturaEConexaoSocket() throws IOException {
        printUtils.printNormal("Socket");
        printUtils.printNormal("\tAbrindo Socket de Bob");
        socket = new Socket("localhost", 3333);
        printUtils.printNormal("\tSocket de Bob Aberto");
        printUtils.printNormal("\tConexão com Alice efetuada");

        streamOutObject = new ObjectOutputStream(socket.getOutputStream());
        streamInObject = new ObjectInputStream(socket.getInputStream());
    }

    private void realizaTrocaDeChavesPublicasRSA() throws Exception {
        printUtils.printNormal("Chaves RSA");
        printUtils.printNormal("\tRealizando troca de chaves publicas");
        streamOutObject.writeObject(rsa.getParDeChaves().getPublic());

        chavePublicaAliceRSA = (PublicKey) streamInObject.readObject();
        printUtils.printNormal("\t" + UtilsColors.ANSI_GREEN + "Chave Publica de Alice: " + chavePublicaAliceRSA);
    }

    private void realizarAutenticacaoMutua() throws Exception {
        SecureRandom rand = new SecureRandom();
        String nonceBob = String.valueOf(rand.nextInt());

        byte[] mensagemAlice = (byte[]) streamInObject.readObject();
        String mensagemAliceDescriptografada = rsa.decriptar(mensagemAlice, rsa.getParDeChaves().getPrivate());
        String nonceAlice = mensagemAliceDescriptografada.split(";")[0];

        byte[] msgEncriptada = rsa.encriptar(nonceAlice.concat(";").concat(nonceBob).getBytes(), chavePublicaAliceRSA);
        streamOutObject.writeObject(msgEncriptada);

        mensagemAlice = (byte[]) streamInObject.readObject();

        if(!rsa.decriptar(mensagemAlice, rsa.getParDeChaves().getPrivate()).equals(nonceBob)){
            printUtils.printComTempo(UtilsColors.ANSI_RED + "Autenticação mutua falhou!");
            System.exit(0);
        }
        printUtils.printComTempo(UtilsColors.ANSI_GREEN + "Autenticação mutua realizada!");

    }

    private void realizaTrocaDeChavesPublicasDH() throws Exception {
        byte[] alicePubKeyEnc = (byte[]) streamInObject.readObject();
        byte[] bobPubKeyEnc = dhBob.obterChavePublicaEncoded(alicePubKeyEnc);

        streamOutObject.writeObject(bobPubKeyEnc);
        dhBob.realizarAcordoDeChave();

        int tamanhoChaveSecretaAlice = streamInObject.readInt();
        dhBob.gerarChaveSecreta(tamanhoChaveSecretaAlice);

        dh.gerarChaveAes(dhBob.getBobSharedSecret());
    }

    public void enviarMensagemParaAlice(String msg) throws Exception {
        streamOutObject.writeObject(dh.encriptar(msg));
        streamOutObject.writeObject(dh.getEncodedParams());
    }

    public void receberMensagemDeAlice() throws Exception {
        boolean done = false;
        while (!done) {
            byte[] line = (byte[]) streamInObject.readObject();
            byte[] encodedParams = (byte[]) streamInObject.readObject();
            String lines = dh.decriptar(line, encodedParams);
            printUtils.printComTempo(UtilsColors.ANSI_YELLOW + "Mensagem de Alice: " + UtilsColors.ANSI_BLUE + lines);
            done = lines.equals(".bye");
        }
    }

    public void fechar() throws Exception {
        streamOutObject.close();
        socket.close();
    }

}
