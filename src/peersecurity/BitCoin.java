package peersecurity;

import keys.KeyManager;
import java.net.*;
import java.io.*;
import java.security.*;
import java.util.ArrayList;
import java.util.Scanner;
<<<<<<< HEAD
import peersecurity.Transaction;
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e

/**
 * Classe principal do simulador de transações BitCoin para a disciplina de Sistemas Distribuídos
 * @author Lucas
 */

public class BitCoin {

    // Variáveis acessadas pelas outras classes do processo
    static ArrayList<Process> listProcess = new ArrayList<>();
<<<<<<< HEAD
    static ArrayList<Transaction> listTransaction = new ArrayList<>();
    protected static PrivateKey privKey;
    
    
=======
    protected static PrivateKey privKey;
    
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
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
<<<<<<< HEAD
            ru = new UnicastCommunication(args[0],process);
=======
            ru = new UnicastCommunication(process);
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
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
<<<<<<< HEAD
            System.out.println("\n[MULTICAST - SEND] Enviando dados do Processo Recém-criado: ID: "+id+" | Porta: "+port+" | Chave Pública: -Long Number- \n | Quant Moedas:"+coinQuant+" | Preço das Moedas: "+coinPrice+"");
            BitCoin.listProcess.add(new Process(id, port, pubKey, coinQuant, coinPrice ));
            
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
            
            // Compra e Venda
            
            String cmd;
            do {
<<<<<<< HEAD
                 //if(process.wait==false){
                    
                    System.out.println("\nDigite o comando (C para comprar,V para verificar a situação dos processos, T para verificar o histórico de transações ou S para sair):");
                    
                    cmd = in.nextLine().trim();
                    
                
=======
                System.out.println("Digite o comando (C para comprar, M para mineirar ou S para sair):");
                cmd = in.nextLine().trim();
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
                if (cmd.equalsIgnoreCase("S")) {break;}
                
                else if(cmd.equalsIgnoreCase("C"))
                {
<<<<<<< HEAD
                  //verifica qual a melhor oferta de venda
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
                  int lowprice = 1000000;
                  int cid=0,cport=0,quant=0,cquant=0;
                  PublicKey pubkey = process.pub;
                    for (Process p : listProcess) {
<<<<<<< HEAD
                        if(p.coinQuant>0 && p.ID != process.ID){
                            if(p.coinPrice < lowprice)
                            {
                                lowprice = p.coinPrice;
                                cid = p.ID;
                                cport = p.port;
                                quant = p.coinQuant;
                            }
                        }
                    }
                    if( listProcess.size() > 1 )
                    {
                    System.out.println("O menor preço ofertado entre os processos ativos é: "+lowprice+", Quant disponível: "+quant+" moedas, oferta do processo "+cid+"\nDigite a quantidade de moedas desejadas:");
                    cquant = Integer.parseInt(in.nextLine().trim());
                    
                    
                        
                    
                    if(cquant > quant+1)
                    {
                        //caso o valor inserido seja maior que o disp
                        do{
                       System.out.println("A Quantia digitada ultrapassa o disponível para a transação (quantia + taxa de mineração), por favor digite novamente: ");
                       cquant = Integer.parseInt(in.nextLine().trim()); 
                        }while(cquant>quant+1);
                    }
                    
                    ByteArrayOutputStream bos1 = new ByteArrayOutputStream(10);
                    ObjectOutputStream oos1 = new ObjectOutputStream(bos1);
                    oos1.writeChar('C');
                    oos1.writeInt(process.ID);
                    oos1.writeInt(process.port);
                    oos1.writeInt(cquant);
                    
                           
                    oos1.flush();
                    // Manda a mensagem de requisição de compra para o processo escolhido
                    byte[] out = bos1.toByteArray();
                    DatagramPacket messageOut1 = new DatagramPacket(out, out.length, InetAddress.getLocalHost(), cport);
                    socket.send(messageOut1);
                    System.out.println("\n[UNICAST - SEND] Enviado pedido de compra de moedas:\nComprador: Processo "+process.ID+" | Vendedor: Processo "+cid+" | Quant: "+cquant+" | Preço: "+lowprice+"");
                    process.setWait(true);
                    int pid = process.ID;
                    boolean status = false;
                    BitCoin.listTransaction.add(new Transaction(pid, cid, coinQuant, status,0));
            
                    while(true)
                    {
                        //processo aguarda até validação
                        System.out.println("Wait do processo: "+process.wait);
                        if(!process.isWait())
                        {
                             break;
                        }
                               
                    }
                    
                    
                }
                    //caso só exista um processo ativo
                    else{
                        System.out.println("\nSó existe um Processo ativo, impossibilitando transações"); 
                    }
                    
                }
                //imprime lista de Transações
                else if(cmd.equalsIgnoreCase("T")) {
                    System.out.println("Lista de Transações:");
                    for (Transaction t : listTransaction) {
                           System.out.println("\nComprador: "+t.CID+" | Vendedor: "+t.VID+" | Quant. Moedas: "+t.coinQuant+"| Status da Transação: "+t.confirmed+"| Timestamp: "+t.timestamp);
                        }
                    }
                else if(cmd.equalsIgnoreCase("V")) {
                    System.out.println("Lista de Processos:");
                    for (Process p : listProcess) {
                           System.out.println("\nProcesso: "+p.ID+" | Quantidade de Moedas: "+p.coinQuant);
                        }
                    }
                
            
            //}
=======
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
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
                
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

<<<<<<< HEAD
/*
* Classe que guarda as informações do processo para serem usadas pelas threads    
*/
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
class Process implements Serializable {

    int ID;
    int port;
    int coinQuant;
    int coinPrice;
    PublicKey pub;
<<<<<<< HEAD
    boolean wait;
    boolean mineflag;

   
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e

    
    

    public Process(int ID, int port, PublicKey pub, int coinQuant, int coinPrice) {
        this.ID = ID;
        this.port = port;
        this.pub = pub;
        this.coinQuant = coinQuant;
        this.coinPrice = coinPrice;
        
    }
<<<<<<< HEAD

    public boolean isMineflag() {
        return mineflag;
    }

    public void setMineflag(boolean mineflag) {
        this.mineflag = mineflag;
    }
    
    
    
     public boolean isWait() {
        return wait;
    }

    public void setWait(boolean wait) {
        this.wait = wait;
    }
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
    
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
<<<<<<< HEAD

=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
