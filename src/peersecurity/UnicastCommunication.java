package peersecurity;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import keys.KeyManager;

/**
 * Thread da comunicação unicast por UDP
 *
 * @author Lucas
 */
public class UnicastCommunication extends Thread {

    DatagramSocket socket;
    Process process;
    MulticastSocket s = null;
    InetAddress group = null;

    // Cria o socket UDP na porta do processo
    public UnicastCommunication(String ip, Process process) {
        this.process = process;
        try {
            socket = new DatagramSocket(process.port);
        } catch (IOException ex) {
            System.out.println("IO: " + ex);
        }
         try {
            group = InetAddress.getByName(ip);
            s = new MulticastSocket(6789);
            s.joinGroup(group);
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    /*
     * Função run() da thread
     */
    @Override
    public void run() {
        while (true) {
            byte[] buffer = new byte[1024];
            try {
                
                // Recebe a mensagem que foi enviada por UDP a este processo
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                socket.receive(messageIn);
                ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bis);
                // Le o char identificador da mensagem e trata cada caso
                char type = ois.readChar();
                

                switch (type) {
                    // Ao entrar e enviar a chave por multicast, receberá respostas com as chaves dos processos que já estavam no grupo
                    case 'K':
                        if(process.wait == false)
                        {
                            int id = ois.readInt();
                            int rport = ois.readInt();
                            PublicKey pubKey = (PublicKey) ois.readObject();
                            int coinQuant = ois.readInt();
                            int coinPrice = ois.readInt();
                            // Salva a chave na lista de processos
                            BitCoin.listProcess.add(new Process(id, rport, pubKey ,
                                    coinQuant, coinPrice));
                            System.out.println("\n[UNICAST - RECEIVE] Dados do processo de ID:"+id+" recebidos: Porta: "+rport+" | Chave Pública: -Long Number- \n | Quant Moedas:"+coinQuant+" | Preço das Moedas: "+coinPrice+"");
                            break;
                        }
                        else
                            break;
                            
                    // Trata requisição de compra
                    case 'C':
                        String content="";
                        if(!process.isWait())
                        {
                            int cid = ois.readInt();
                            int cport = ois.readInt();
                            int cquant = ois.readInt();

                            //process.coinQuant = process.coinQuant - cquant;
                            // Criptografa o conteúdo do arquivo para um array de bytes usando a chave privada
                            content+=cid;
                            
                            
                            byte[] cryptedContent = KeyManager.crypt(content, BitCoin.privKey);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
                            ObjectOutputStream oos;
                            oos = new ObjectOutputStream(bos);
                            oos.writeChar('M'); // char identificando que a mensagem está passando um pedido de validação de transação  
                            oos.writeInt(cid);
                            oos.writeInt(cport);
                            oos.writeInt(process.ID);
                            oos.writeInt(process.port);
                            oos.writeInt(cquant);
                            oos.write(cryptedContent);

                            oos.flush();
                            // Converte o objeto para uma array de bytes e envia por datagrama
                            byte[] m = bos.toByteArray();
                            DatagramPacket messageOut = new DatagramPacket(m, m.length,group, 6789);
                            s.send(messageOut);
                            System.out.println("\n[UNICAST - RECEIVE] Pedido de compra recebido");
                            System.out.println("[MULTICAST - SEND] Esperando validação: Comprador: Processo "+cid+" | Vendedor : Processo "+process.ID+" | Quant Moedas:"+cquant+"");
                                //BitCoin.listTransaction.add(new Transaction(cid, process.ID,0, cquant, false,0));
                            process.setWait(true);
                            break;
                        }
                        else
                            break;
                    
                        
                }

            } catch (IOException ex) {
                Logger.getLogger(UnicastCommunication.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(UnicastCommunication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
