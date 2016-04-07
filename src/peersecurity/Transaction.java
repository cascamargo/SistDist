/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package peersecurity;

import java.io.Serializable;

/**
 *
 * @author Lucas
 */
class Transaction implements Serializable
{
    int CID;
    int VID;
    int coinQuant;
    boolean confirmed;
    long timestamp;

    public Transaction(int CID, int VID, int coinQuant, boolean confirmed, long timestamp) {
        this.CID = CID;
        this.VID = VID;
        this.coinQuant = coinQuant;
        this.confirmed = confirmed;
        this.timestamp=timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCID() {
        return CID;
    }

    public void setCID(int CID) {
        this.CID = CID;
    }

    public int getVID() {
        return VID;
    }

    public void setVID(int VID) {
        this.VID = VID;
    }

   

    public int getCoinQuant() {
        return coinQuant;
    }

    public void setCoinQuant(int coinQuant) {
        this.coinQuant = coinQuant;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
    
    
}
