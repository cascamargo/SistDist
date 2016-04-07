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
import java.sql.Timestamp;
import java.util.Random;
import keys.KeyManager;
import java.util.Date;
import static peersecurity.BitCoin.*;
import java.util.*; 


/**
 * Thread da comunicação multicast do processo, usada para envio de chave
 * pública e requisição de arquivo
 *
 * @author Lucas
 */
public class MulticastCommunication extends Thread {

    MulticastSocket s = null;
    InetAddress group = null;
    Process process = null;
    DatagramSocket socket;
    public static int mineflag;
    
    

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
                            byte[] cryptedContent = new byte[64];
                            int cid = ois.readInt();
                            int cport = ois.readInt();
                            int vid = ois.readInt();
                            int vport = ois.readInt();
                            int quant = ois.readInt();
                            cryptedContent[0] =  (byte) ois.read();
                            
                            // Procura na lista de processos, pelo número de porta, a chave pública do processo que enviou o arquivo
                            PublicKey pub = null;
                            for ( Process p : BitCoin.listProcess ) {
                                if (p.ID == vid) {
                                    pub = p.getPub();
                                    break;
                                }
                            }
                            /*
                            // Descriptografa o arquivo usando a chave pública
                            String decryptedContent = KeyManager.decrypt(cryptedContent, pub);
                            System.out.println("DECRIPT:"+decryptedContent );
                            */
                            if (process.ID != cid && process.ID != vid )
                            {
                                //BitCoin.listTransaction.add(new Transaction(cid, vid,process.ID, quant, false,0));
                            
                            
                            

                            System.out.println("\n[MULTICAST - RECEIVE] Transação pendente para validação");

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
                                    BitCoin.listTransaction.add(new Transaction(cid, vid,mid, quant,false,timestamp1));
                                    
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
                                   int cont=0;
                                   
                                   /*for (Iterator <Transaction> iterator = listTransaction.iterator(); iterator.hasNext();) {
                                        Transaction t1 = iterator.next();
                                        if(t1.VID == vid && t1.CID == cid && t1.coinQuant == quant )
                                        {
                                            
                                            if (sTimestamp == t1.timestamp && sTimestamp != 0)
                                            {
                                                //cont++;
                                                t1.setConfirmed(true);    
                                            }
                                            else
                                            {
                                                iterator.remove();
                                            }
                                            // Remove the current element from the iterator and the list.
                                        }
                                            
                                        
                                   }*/
                                   
                                    for (Transaction t1 : BitCoin.listTransaction)
                                    {
                                        if(t1.VID == vid && t1.CID == cid && t1.coinQuant == quant )
                                        {
                                            
                                            if (sTimestamp == t1.timestamp && sTimestamp != 0)
                                            {
                                                cont++;
                                                if(cont>=1)
                                                t1.setConfirmed(true);    
                                            }
                                            else
                                            {
                                                
                                            }
                                        }
                                    }
                                    //System.out.println("BitCoin.listTransaction.size()"+BitCoin.listTransaction.size());
                                    
                            Transaction t2 = BitCoin.listTransaction.get(BitCoin.listTransaction.size()-1);
                            //if(BitCoin.listTransaction.size()>=2)
                             //   BitCoin.listTransaction.remove(BitCoin.listTransaction.size()-1);
                            
                            ByteArrayOutputStream bos1 = new ByteArrayOutputStream(10);
                            ObjectOutputStream oos1;
                            oos1 = new ObjectOutputStream(bos1);
                            oos1.writeChar('B'); // char identificando que a mensagem está passando um broadcast de validação  
                            oos1.writeInt(t2.CID);
                            oos1.writeInt(t2.MID);
                            oos1.writeInt(t2.VID);
                            oos1.writeInt(t2.coinQuant);
                            oos1.writeBoolean(t2.isConfirmed());
                            oos1.writeLong(t2.timestamp);

                            oos1.flush();

                            // Converte o objeto para uma array de bytes e envia por datagrama
                            byte[] m2 = bos1.toByteArray();
                            DatagramPacket messageOut = new DatagramPacket(m2, m2.length,group, 6789);
                            s.send(messageOut);

                            if(t2.isConfirmed())
                            {
                                process.coinQuant-=(t2.coinQuant+1);
                                
                                System.out.println("\n[MULTICAST - RECEIVE] Transação validada com sucesso pelo minerador: "+mid+" | Comprador: "+t2.CID+" | Vendedor:"+t2.VID+" | quantidade de moedas:"+t2.coinQuant);
                                                 

                                //atualiza valores do banco

                                for (Process p : listProcess) {
                                    if(p.ID == t2.MID)
                                        p.coinQuant+=1;
                                    if(p.ID==t2.CID){
                                        p.coinQuant += t2.coinQuant;
                                    }
                                    else if(p.ID == t2.VID){
                                        p.coinQuant -=(t2.coinQuant+1);
                                    }
                                }
                            }
                        }
                    }
                        
                    case 'B':
                        
                    int tcid = ois.readInt();
                    int tmid = ois.readInt();
                    int tvid = ois.readInt();
                    int tquant= ois.readInt();
                    boolean confirm= ois.readBoolean();
                    long timestamp2= ois.readLong();
                        
                        if(process.ID != tvid && tmid >= 0 && tmid <= 5000)
                        {
                           if(confirm)
                           {
                                BitCoin.listTransaction.add(new Transaction(tcid, tvid,tmid, tquant,confirm,timestamp2));
                                if(process.ID == tmid)
                                    process.coinQuant++;
                                if(process.ID == tcid)
                                    process.coinQuant+=tquant;
                                
                                System.out.println("\n[MULTICAST - RECEIVE] Transação validada com sucesso pelo minerador: "+tmid+" | Comprador: "+tcid+" | Vendedor:"+tvid+" | quantidade de moedas:"+tquant);
                                for (Process p : listProcess) {
                                    if(p.ID == tmid)
                                        p.coinQuant+=1;
                                    if(p.ID==tcid){
                                        p.coinQuant += tquant;
                                       }
                                    else if(p.ID == tvid){
                                         p.coinQuant -= (tquant+1);
                                    }
                                }               
                            }
                           
                            //System.out.println("\n[MULTICAST - RECEIVE] Transação validada com sucesso pelo minerador: "+tmid+" | Comprador: "+tcid+" | Vendedor:"+tvid+" | quantidade de moedas:"+tquant);
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
            }
        }
    }
}
