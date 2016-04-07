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
<<<<<<< HEAD
 * Thread da comunicação unicast por UDP
 *
 * @author Lucas
=======
 * Thread da comunicação unicast por UDP, usada no envio de arquivos entre os
 * pares
 *
 * @author Cassiano
 * @author Henrique
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
 */
public class UnicastCommunication extends Thread {

    DatagramSocket socket;
    Process process;
<<<<<<< HEAD
    MulticastSocket s = null;
    InetAddress group = null;

    // Cria o socket UDP na porta do processo
    public UnicastCommunication(String ip, Process process) {
=======

    // Cria o socket UDP na porta do processo
    public UnicastCommunication(Process process) {
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
        this.process = process;
        try {
            socket = new DatagramSocket(process.port);
        } catch (IOException ex) {
            System.out.println("IO: " + ex);
        }
<<<<<<< HEAD
         try {
            group = InetAddress.getByName(ip);
            s = new MulticastSocket(6789);
            s.joinGroup(group);
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
    }

    /*
     * Função run() da thread
     */
    @Override
    public void run() {
        while (true) {
            byte[] buffer = new byte[1024];
            try {
<<<<<<< HEAD
                
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
                // Recebe a mensagem que foi enviada por UDP a este processo
                DatagramPacket messageIn = new DatagramPacket(buffer, buffer.length);
                socket.receive(messageIn);
                ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
                ObjectInputStream ois = new ObjectInputStream(bis);
                // Le o char identificador da mensagem e trata cada caso
                char type = ois.readChar();
<<<<<<< HEAD
                
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e

                switch (type) {
                    // Ao entrar e enviar a chave por multicast, receberá respostas com as chaves dos processos que já estavam no grupo
                    case 'K':
<<<<<<< HEAD
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
                                BitCoin.listTransaction.add(new Transaction(cid, process.ID, cquant, false,0));
                            process.setWait(true);
                            break;
                        }
                        else
                            break;
                    
                        
=======
                        int id = ois.readInt();
                        int rport = ois.readInt();
                        // Salva a chave na lista de processos
                        BitCoin.listProcess.add(new Process(id, rport, (PublicKey) ois.readObject(),
                                ois.readInt(), ois.readInt()));
                        System.out.println("Dados do processo "+id+" de porta " + rport + " recebida");
                        break;
                    // Trata requisição de compra
                    case 'C':/*
                        
                    //    
                    case 'W':
                        Scanner input;
                        String content = new String();
                        int wport = ois.readInt();

                        String filename = (String) ois.readObject();
                        System.out.println("Este processo foi escolhido para o arquivo <" + filename
                                + "> pelo processo de porta " + wport);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
                        ObjectOutputStream oos = new ObjectOutputStream(bos);
                        // Procura o arquivo na pasta do processo
                        if (process.dir.exists()) {
                            if (process.dir.isDirectory()) {

                                input = new Scanner(new File(process.dir.getAbsolutePath() + "\\" + filename));
                                // Pega o conteúdo do arquivo
                                while (input.hasNext()) {
                                    content += input.next();
                                }
                                // Criptografa o conteúdo do arquivo para um array de bytes usando a chave privada
                                byte[] cryptedContent = KeyManager.crypt(content, Peer2PeerFiles.privKey);
                                oos.writeChar('S');
                                oos.writeInt(cryptedContent.length); // Envia o tamanho do conteúdo criptografado
                                oos.write(cryptedContent);
                                oos.writeObject(filename);
                                oos.writeInt(process.port);
                                oos.flush();
                                // Envia o arquivo criptografado
                                byte[] out = bos.toByteArray();
                                DatagramPacket messageOut = new DatagramPacket(out, out.length, messageIn.getAddress(), wport);
                                socket.send(messageOut);
                                System.out.println("Arquivo criptografado e enviado");
                            }
                        }
                        break;
                    // Trata quando recebe o arquivo do processo escolhido
                    case 'S':
                        // Lê byte a byte o conteúdo criptografado
                        int readLength = ois.readInt();
                        byte[] fileContent = new byte[readLength];
                        for (int i = 0; i < readLength; i++) {
                            fileContent[i] = ois.readByte();
                        }

                        // Cria um arquivo no diretório para escrever o conteúdo recebido
                        String file = (String) ois.readObject();
                        String path = process.dir.getAbsolutePath() + "\\" + file;
                        File record = new File(path);
                        int fport = ois.readInt();

                        // Procura na lista de processos, pelo número de porta, a chave pública do processo que enviou o arquivo
                        PublicKey pub = null;
                        for (int i = 0; i < Peer2PeerFiles.listProcess.size(); i++) {
                            if (Peer2PeerFiles.listProcess.get(i).port == fport) {
                                pub = Peer2PeerFiles.listProcess.get(i).pub;
                                break;
                            }
                        }
                        // Descriptografa o arquivo usando a chave pública
                        String decryptedContent = KeyManager.decrypt(fileContent, pub);
                        if (!record.exists()) {
                            record.createNewFile();
                        }
                        // Escreve no arquivo criado o conteúdo descriptografado
                        FileWriter fw = new FileWriter(record.getAbsoluteFile());
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(decryptedContent);
                        bw.close();
                        System.out.println("Arquivo <" + file + "> recebido e descriptografado com sucesso");
                    default:*/
                        break;
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
                }

            } catch (IOException ex) {
                Logger.getLogger(UnicastCommunication.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(UnicastCommunication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}
