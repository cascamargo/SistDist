package peersecurity;

import keys.KeyManager;
import java.net.*;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Classe principal do simulador de transações BitCoin para a disciplina de Sistemas Distribuídos
 * @author Lucas
 */

public class BitCoin {

    // Variáveis acessadas pelas outras classes do processo
    static ArrayList<Process> listProcess = new ArrayList<>();
    protected static PrivateKey privKey;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // args give message contents and destination multicast group (e.g. "228.5.6.7")       
        
        PublicKey pubKey;
        int id;
        int port;
        int coinQuant = 100;
        int coinPrice;
        Scanner in = new Scanner(System.in);
        // ID usado apenas para nomear a pasta de cada processo
        System.out.println("Digite o ID deste processo:");
        id = Integer.parseInt(in.nextLine().trim());
        // Porta usada para comunicação UDP
        System.out.println("Digite a porta deste processo:");
        port = Integer.parseInt(in.nextLine().trim());
        MulticastSocket s = null; 
        DatagramSocket socket = null;
        // Preço das moedas para o processo
        System.out.println("Digite o preço das moedas deste processo:");
        coinPrice = Integer.parseInt(in.nextLine().trim());
       
        
        //cria diretorio
        /*String nome = "Processo_" + String.valueOf(id);
        File dir = new File(nome);
        dir.mkdirs();*/

        // Gera o par de chaves
        KeyManager gensig = new KeyManager();
        privKey = gensig.getPriv();
        pubKey = gensig.getPub();
        
        Process process = new Process(id, port, pubKey, coinQuant, coinPrice);
        

        try {
            socket = new DatagramSocket();
            // Entra no grupo multicast
            InetAddress group = InetAddress.getByName(args[0]);
            s = new MulticastSocket(6789);
            s.joinGroup(group);
            MulticastCommunication c;
            UnicastCommunication ru;
            // Inicia as threads de comunicação multicast e unicast
            ru = new UnicastCommunication(process);
            ru.start();
            c = new MulticastCommunication(args[0], process);
            c.start();
            // Manda o ID, a porta e a chave pública em multicast através do ObjectOutputStream
            ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
            ObjectOutputStream oos;
            oos = new ObjectOutputStream(bos);
            oos.writeChar('K'); // char identificando que a mensagem está passando a chave pública
            oos.writeInt(id);
            oos.writeInt(port);
            oos.writeObject(pubKey);
            oos.writeInt(coinQuant);
            oos.writeInt(coinPrice);
          
            oos.flush();
            // Converte o objeto para uma array de bytes e envia por datagrama
            byte[] m = bos.toByteArray();
            DatagramPacket messageOut = new DatagramPacket(m, m.length, group, 6789);
            s.send(messageOut);
            
            // Compra e Venda
            
            String cmd;
            do {
                System.out.println("Digite o comando (C para comprar, M para mineirar ou S para sair):");
                cmd = in.nextLine().trim();
                if (cmd.equalsIgnoreCase("S")) {break;}
                
                else if(cmd.equalsIgnoreCase("C"))
                {
                  int lowprice = 1000000;
                  int cid=0,cport=0,quant=0,cquant=0;
                  PublicKey pubkey = process.pub;
                    for (Process p : listProcess) {
                        if(p.coinPrice < lowprice)
                        {
                            lowprice = p.coinPrice;
                            cid = p.ID;
                            cport = p.port;
                            quant = p.coinQuant;
                        }
                    }
                    System.out.println("O menor preço ofertado entre os processos ativos é:"+lowprice+" Quant disp"+quant+"\nDigite a quantidade de moedas desejadas:");
                    cquant = Integer.parseInt(in.nextLine().trim());
                    
                    
                    
                    oos.writeChar('C');
                    oos.writeInt(cid);
                    oos.writeInt(cport);
                    oos.writeObject(pubkey);
                    oos.writeInt(cquant);
                    
                           
                    oos.flush();
                    // Manda a mensagem de requisição de compra para o processo escolhido
                    byte[] out = bos.toByteArray();
                    DatagramPacket messageOut1 = new DatagramPacket(out, out.length, InetAddress.getLocalHost(), cport);
                    socket.send(messageOut1);
                    
                    
                }
                
                //código para mineirar
                else{
                    
                }
                
                
                
                
                
                bos = new ByteArrayOutputStream(10);
                oos = new ObjectOutputStream(bos);
                oos.writeChar(cmd.charAt(0));
                oos.writeInt(port);
                oos.writeObject(cmd.substring(2));
                oos.flush();
                m = bos.toByteArray();
                // Envia a requisição de arquivo ao grupo
                messageOut = new DatagramPacket(m, m.length, group, 6789);
                s.send(messageOut);
                
            }while(true);
            
            s.leaveGroup(group);
            s.close();
            System.exit(0);
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (s != null) {
                s.close();
            }
        }
    }
}

class Process implements Serializable {

    int ID;
    int port;
    int coinQuant;
    int coinPrice;
    PublicKey pub;

    
    

    public Process(int ID, int port, PublicKey pub, int coinQuant, int coinPrice) {
        this.ID = ID;
        this.port = port;
        this.pub = pub;
        this.coinQuant = coinQuant;
        this.coinPrice = coinPrice;
        
    }
    
    public int getCoinQuant() {
        return coinQuant;
    }

    public void setCoinQuant(int coinQuant) {
        this.coinQuant = coinQuant;
    }

    public int getCoinPrice() {
        return coinPrice;
    }

    public void setCoinPrice(int coinPrice) {
        this.coinPrice = coinPrice;
    }

    public int getID() {
        return ID;
    }

    public int getPort() {
        return port;
    }
    
  

    public PublicKey getPub() {
        return pub;
    }

    
    @Override
    public String toString() {
        return "Process{" + "ID=" + ID + ", port=" + port + ", pub=" + pub + '}';
    }
}
