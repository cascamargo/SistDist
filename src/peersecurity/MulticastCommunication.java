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
<<<<<<< HEAD
import java.sql.Timestamp;
import java.util.Random;
import keys.KeyManager;
import java.util.Date;
import static peersecurity.BitCoin.*;
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e

/**
 * Thread da comunicação multicast do processo, usada para envio de chave
 * pública e requisição de arquivo
 *
<<<<<<< HEAD
 * @author Lucas
=======
 * @author Cassiano
 * @author Henrique
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
 */
public class MulticastCommunication extends Thread {

    MulticastSocket s = null;
    InetAddress group = null;
    Process process = null;
    DatagramSocket socket;
<<<<<<< HEAD
    public static int mineflag;
    
    
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e

    public MulticastCommunication(String ip, Process p) throws SocketException {
        this.socket = new DatagramSocket();
        this.process = p;
<<<<<<< HEAD
        
        
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
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
<<<<<<< HEAD
        
=======
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
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
<<<<<<< HEAD
                             System.out.println("\n[MULTICAST - RECEIVE] Dados do processo de ID:"+pid+" recebidos: Porta: "+pport+" | Chave Pública: -Long Number- \n | Quant Moedas:"+pquant+" | Preço das Moedas: "+pprice+"");
                             System.out.println("\n[UNICAST - SEND] Enviando dados do Processo: ID: "+process.ID+" recebidos: Porta: "+process.port+" | Chave Pública: -Long Number- \n | Quant Moedas:"+process.coinQuant+" | Preço das Moedas: "+process.coinPrice+"");
                        
                            
                            break;
                        }
                    // Trata quandoa mensagem é um pedido de validacao
                    case 'M':
                         //gera delay aleatorio
                        Random random = new Random();
                        long fraction = (long)(4000 * random.nextDouble());
                        int randomnumbr = (int)(fraction + 1000);
                         try{
                            Thread.sleep(randomnumbr);
                         }catch(InterruptedException ex)
                         {
                            Thread.currentThread().interrupt();
                         }
                         
                         Timestamp date = new Timestamp(System.currentTimeMillis());
                         long timestamp = date.getTime();
                         //System.out.println("TIMESTAMP:"+timestamp);
                        
                        if(!process.isWait())
                        {
                            this.mineflag++;
                            byte[] cryptedContent = new byte[10];
                            int cid = ois.readInt();
                            int cport = ois.readInt();
                            int vid = ois.readInt();
                            int vport = ois.readInt();
                            int quant = ois.readInt();
                            
                            if (process.ID != cid && process.ID != vid )
                            {
                                BitCoin.listTransaction.add(new Transaction(cid, vid, quant, false,0));
                            
                            
                            cryptedContent[0] = ois.readByte();

                            System.out.println("\n[MULTICAST - RECEIVE] Transação pendente para validação");

                             // Procura na lista de processos, pelo número de porta, a chave pública do processo que enviou o arquivo
                            PublicKey pub = null;
                            for (Process p1 : listProcess) {
                                if (p1.ID == vid) {
                                    pub = p1.pub;
                                    break;
                            }
                        }
                        // Descriptografa o arquivo usando a chave pública
                        //String decryptedContent = KeyManager.decrypt(cryptedContent, pub);
                        
                        //    System.out.println("DECRYPTED:"+decryptedContent);
                        
                            
                        
                         
                             //atualiza valores do processo
                            
                                    
                                    // long timestamp = getTime();
                            ByteArrayOutputStream bos1 = new ByteArrayOutputStream(10);
                            ObjectOutputStream oos1;
                            oos1 = new ObjectOutputStream(bos1);
                            oos1.writeChar('V'); // char identificando que a mensagem está passando um pedido de validação de transação  
                            oos1.writeInt(cid);
                            oos1.writeInt(cport);
                            oos1.writeInt(vid);
                            oos1.writeInt(vport);
                            oos1.writeInt(quant);
                            oos1.writeInt(process.ID);
                            oos1.writeLong(timestamp);

                            oos1.flush();
                            // Converte o objeto para uma array de bytes e envia por datagrama
                            byte[] m1 = bos1.toByteArray();
                            DatagramPacket messageOut = new DatagramPacket(m1, m1.length,group, 6789);
                            s.send(messageOut);
                            System.out.println("\n[MULTICAST - SEND] Enviada validação de transação");

                                  
                                   
                                
                                
                        }
                   }
                        
                    case 'V':
                      
                        int cid = ois.readInt();
                        int cport = ois.readInt();
                        int vid = ois.readInt();
                        int vport = ois.readInt();
                        int quant= ois.readInt();
                        int mid= ois.readInt();
                        long timestamp1 = ois.readLong();
                        
                        long sTimestamp = timestamp1;
                        
                        if(mid <=5000 && mid >=0)
                        {
                        if(process.ID == vid || process.ID == cid)
                            {   
                                
                                process.setWait(false);
                                //System.out.println("LIBEROU PROCESSOS:"+process.ID+"Wait: "+process.wait);
                                if(process.ID == vid)
                                {
                                    if(timestamp1 != 0)
                                    BitCoin.listTransaction.add(new Transaction(cid, vid, quant,false,timestamp1));
                                    
                                    for (Transaction t : listTransaction)
                                     {
                                         if(t.VID == vid && t.CID == cid && t.coinQuant == quant)
                                         {
                                             
                                             if (sTimestamp > t.timestamp && t.timestamp != 0)
                                             {
                                                 sTimestamp = t.timestamp;
                                                 //System.out.println("TIMESTAMP: "+sTimestamp);
                                                 
                                             }
                                         }
                                         
                                     }
                            
                                }
                              
                            }
                        
                        
                        if(process.ID == vid)
                                {
                                    /*random = new Random();
                                    fraction = (long)(20000 * random.nextDouble());
                                    randomnumbr = (int)(fraction + 1000);
                                     try{
                                        Thread.sleep(randomnumbr);
                                     }catch(InterruptedException ex)
                                     {
                                        Thread.currentThread().interrupt();
                                     }*/
                                     int cont=0;
                                     for (Transaction t1 : listTransaction)
                                     {
                                         if(t1.VID == vid && t1.CID == cid && t1.coinQuant == quant)
                                         {
                                             if (sTimestamp != t1.timestamp)
                                             {
                                                //BitCoin.listTransaction.remove(t1);
                                             }
                                             if (sTimestamp == t1.timestamp && sTimestamp != 0)
                                             {
                                                 cont++;
                                                 t1.setConfirmed(true); 
                                                 if(cont>=1 && t1.isConfirmed())
                                                 System.out.println("\n[MULTICAST - RECEIVE] Transação validada com sucesso pelo minerador: "+mid+" | Comprador: "+t1.CID+" | Vendedor:"+t1.VID+" | quantidade de moedas:"+t1.coinQuant);
                                                 
                                             }
                                         }
                                         
                                     }
                                     
                                     //mandar um multicast passando as informações
                                     
                                     //minerador recebe a recompensa
                                    if(mid == process.ID)
                                      process.coinQuant+=1; 
                                    if(cid == process.ID)
                                        process.coinQuant+=quant;
                                    if(vid == process.ID)
                                        process.coinQuant-=(quant+1);
                            
                                }
                        
                        //atualiza valores do banco
                        if(vid == process.ID)
                        {
                             for (Process p : listProcess) {
                                 if(p.ID == mid)
                                     p.coinQuant+=1;
                                 if(p.ID==cid){
                                       p.coinQuant += quant;
                                    }
                                    else if(p.ID == vid){
                                        p.coinQuant -= (quant+1);
                                    }
                             }
                        }
                }
            }
            } catch (IOException | ClassNotFoundException e) {
                
=======
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
>>>>>>> 4d5f4b6564779f935d8eb516f79ea9cc2b883c3e
            }
        }
    }
}
