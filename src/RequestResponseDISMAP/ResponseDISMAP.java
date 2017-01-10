/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RequestResponseDISMAP;

import java.io.Serializable;

/**
 *
 * @author Bob
 */
public class ResponseDISMAP implements IDISMAP, Serializable{
    
    private int     codeRetour;
    private Object  chargeUtile;

    public ResponseDISMAP() {
    }

    public ResponseDISMAP(int codeRetour, Object chargeUtile) {
        this.codeRetour = codeRetour;
        this.chargeUtile = chargeUtile;
    }

    public Object getChargeUtile() {
        return chargeUtile;
    }

    public void setChargeUtile(Object chargeUtile) {
        this.chargeUtile = chargeUtile;
    }

    public int getCodeRetour() {
        return codeRetour;
    }

    public void setCodeRetour(int codeRetour) {
        this.codeRetour = codeRetour;
    }
}
