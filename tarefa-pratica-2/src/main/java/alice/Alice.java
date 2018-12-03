package alice;

import utils.PrintUtils;
import utils.UtilsColors;
import utils.crypto.DH;
import utils.crypto.RSA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.security.SecureRandom;

public class Alice {

    private RSA rsa = new RSA();
    private DH dh = new DH();
    private DH.DHAlice dhAlice = dh.new DHAlice();

    private ServerSocket serverSocket;
    private Socket socketDeBob;

    private PrintUtils printUtils = new PrintUtils();

    private ObjectInputStream streamInObject;
    private ObjectOutputStream streamOutObject;

    private PublicKey chavePublicaBobRSA;

    public Alice() throws Exception {
        realizarAberturaEConexaoSocket();
        realizaTrocaDeChavesPublicasRSA();
        realizarAutenticacaoMutua();
        realizaTrocaDeChavesPublicasDH();
    }


    private void realizarAberturaEConexaoSocket() throws IOException {
        printUtils.printNormal("Socket");
        printUtils.printNormal("\tAbrindo Socket de Alice");
        serverSocket = new ServerSocket(3333);
        printUtils.printNormal("\tSocket de Alice Aberto");

        printUtils.printNormal("\tAguardando conexão de Bob");
        socketDeBob = serverSocket.accept();
        printUtils.printNormal("\tConexão com Bob efetuada");

        streamOutObject = new ObjectOutputStream(socketDeBob.getOutputStream());
        streamInObject = new ObjectInputStream(socketDeBob.getInputStream());
    }

    private void realizaTrocaDeChavesPublicasRSA() throws Exception {
        printUtils.printNormal("Chaves RSA");
        printUtils.printNormal("\tRealizando troca de chaves publicas");
        streamOutObject.writeObject(rsa.getParDeChaves().getPublic());

        chavePublicaBobRSA = (PublicKey) streamInObject.readObject();
        printUtils.printNormal("\t" + UtilsColors.ANSI_GREEN + "Chave Publica de Bob: " + chavePublicaBobRSA);
    }

    private void realizarAutenticacaoMutua() throws Exception {
        SecureRandom rand = new SecureRandom();

        String nonceAlice = String.valueOf(rand.nextInt());
        String idAlice = String.valueOf(rand.nextInt());

        String msg = nonceAlice + ";" + idAlice;

        byte[] msgEncriptada = rsa.encriptar(msg.getBytes(), chavePublicaBobRSA);
        streamOutObject.writeObject(msgEncriptada);

        byte[] mensagemBob = (byte[]) streamInObject.readObject();
        String mensagemBobStr = rsa.decriptar(mensagemBob, rsa.getParDeChaves().getPrivate());

        if (!mensagemBobStr.split(";")[0].equals(nonceAlice)) {
            printUtils.printComTempo(UtilsColors.ANSI_RED + "Autenticação mutua falhou!");
            System.exit(0);
        }

        msg = mensagemBobStr.split(";")[1];
        msgEncriptada = rsa.encriptar(msg.getBytes(), chavePublicaBobRSA);
        streamOutObject.writeObject(msgEncriptada);

        printUtils.printComTempo(UtilsColors.ANSI_GREEN + "Autenticação mutua realizada!");
    }

    private void realizaTrocaDeChavesPublicasDH() throws Exception {
        byte[] alicePubKeyEnc = dhAlice.obterChavePublicaEncoded();
        streamOutObject.writeObject(alicePubKeyEnc);

        byte[] bobPubKeyEnc = (byte[]) streamInObject.readObject();
        dhAlice.realizarAcordoDeChave(bobPubKeyEnc);

        int tamanhoChaveSecreta = dhAlice.gerarChaveSecreta();
        streamOutObject.writeInt(tamanhoChaveSecreta);

        dh.gerarChaveAes(dhAlice.getAliceSharedSecret());
    }

    public void enviarMensagemParaBob(String msg) throws Exception {
        streamOutObject.writeObject(dh.encriptar(msg));
        streamOutObject.writeObject(dh.getEncodedParams());
    }

    public void receberMensagemDeBob() throws Exception {
        boolean done = false;
        while (!done) {
            byte[] line = (byte[]) streamInObject.readObject();
            byte[] encodedParams = (byte[]) streamInObject.readObject();
            String lines = dh.decriptar(line, encodedParams);
            printUtils.printComTempo(UtilsColors.ANSI_YELLOW + "Mensagem de Bob: " + UtilsColors.ANSI_BLUE + lines);
            done = lines.equals(".bye");
        }
    }

    public void fechar() throws Exception {
        streamOutObject.close();
        serverSocket.close();
    }
}
