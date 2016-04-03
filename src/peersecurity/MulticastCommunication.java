package peersecurity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.security.PublicKey;

/**
 * Thread da comunicação multicast do processo, usada para envio de chave
 * pública e requisição de arquivo
 *
 * @author Cassiano
 * @author Henrique
 */
public class MulticastCommunication extends Thread {

    MulticastSocket s = null;
    InetAddress group = null;
    Process process = null;
    DatagramSocket socket;

    public MulticastCommunication(String ip, Process p) throws SocketException {
        this.socket = new DatagramSocket();
        this.process = p;
        // Insere a thread no grupo
        try {
            group = InetAddress.getByName(ip);
            s = new MulticastSocket(6789);
            s.joinGroup(group);
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    // Função run() da thread
    @Override
    public void run() {
        while (true) {
            try {
                // Recebe a mensagem multicast, verifica o char identificador e trata
                byte[] buffer = new byte[1024];
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                s.receive(messageIn);
                byte[] m = messageIn.getData();
                ByteArrayInputStream bis = new ByteArrayInputStream(m);
                ObjectInputStream ois = new ObjectInputStream(bis);
                char type = ois.readChar();

                switch (type) {
                    // Trata quando recebe uma chave pública de um processo que acabou de entrar
                    case 'K':
                        int pid;
                        if ((pid = ois.readInt()) == process.ID) {
                            break;
                        } else {

                            // Recebe as informacoes do processo que entrou e guarda em uma ArrayList
                            int pport = ois.readInt();
                            PublicKey ppub = (PublicKey) ois.readObject();
                            int pquant = ois.readInt();
                            int pprice = ois.readInt();
                            
                            BitCoin.listProcess.add(new Process(pid, pport, ppub, pquant, pprice ));
                            ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
                            ObjectOutputStream oos = new ObjectOutputStream(bos);
                            oos.writeChar('K');
                            oos.writeInt(process.ID);
                            oos.writeInt(process.port);
                            oos.writeObject(process.pub);
                            oos.writeInt(process.coinQuant);
                            oos.writeInt(process.coinPrice);
                           
                            oos.flush();
                            // Manda de volta a própria chave pública ao processo que entrou
                            byte[] out = bos.toByteArray();
                            DatagramPacket messageOut = new DatagramPacket(out, out.length, messageIn.getAddress(), pport);
                            socket.send(messageOut);
                            System.out.println("Dados do processo "+pid+" de porta " + pport + " recebida");
                            break;
                        }
                    // Trata quandoa mensagem é uma validação de compra (mineirada)
                    case 'M':
                        int pport = ois.readInt();
                        if (pport == process.port) {
                            break;
                        }
                       
                        
                }

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("ERRO:" + e.getMessage());
            }
        }
    }
}
