/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RequestResponseDISMAP;


import Authenticate.Authentication;
import Crypto.*;
import GestionSocket.GestionSocket;
import GestionSocket.ISocket;
import RequestResponse.ConsoleServeur;
import RequestResponse.IRequest;
import static RequestResponseDISMAP.IDISMAP.BUY_GOODS_REQUEST;
import static RequestResponseDISMAP.IDISMAP.LOGIN_REQUEST;
import static RequestResponseDISMAP.IDISMAP.LOGOUT;
import static RequestResponseDISMAP.IDISMAP.NO;
import static RequestResponseDISMAP.IDISMAP.USER_INSERT_REQUEST;
import static RequestResponseDISMAP.IDISMAP.YES;
import UtilsCrypto.ByteArrayList;
import UtilsCrypto.ManipFichierCrypto;
import UtilsDISMAP.FichierConfig;
import beans.BeanBDAccessMySQL;
import beans.ConnectionOptions;
import beans.DataBaseAccessFactory;
import beans.IDataBaseAccess;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.bouncycastle.jce.provider.BouncyCastleProvider;



/**
 *
 * @author Bob
 */
public class RequestDISMAP implements IRequest, IDISMAP, Serializable{
    
    private int         type; // LOGIN,...
    private Object      chargeUtile;
    private int         codeRetour;
    private GestionSocket GSocketCo;
    // SI je veux extraire certaines variables membres du processus serializable, on peut utiliser le qualificaeur "transient"

    public int getCodeRetour() {
        return codeRetour;
    }

    public void setCodeRetour(int codeRetour) {
        this.codeRetour = codeRetour;
    }


           
    
    public RequestDISMAP(int type, Object chargeUtile) {
        this.type = type;
        this.chargeUtile = chargeUtile; // set du vector
    }


    @Override
    public Object getChargeUtile() {
        return chargeUtile;
    }

    @Override
    public void setChargeUtile(Object chargeUtile) {
        this.chargeUtile = chargeUtile;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }



    @Override
    public boolean executeRequest(ISocket Socket, ConsoleServeur guiApplication) {
        
        if(getType() == LOGIN_REQUEST)
        {
            guiApplication.TraceEvenements("Réception de LOGIN_REQUEST");
            try {
                traiteRequeteLogin(Socket, guiApplication);
            } catch (Exception ex) {
                Logger.getLogger(RequestDISMAP.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        if(getType() == USER_INSERT_REQUEST)
        {
            guiApplication.TraceEvenements("Réception de USER_INSERT_REQUEST");
            try {
                traiteRequeteInsertLogin(Socket, guiApplication);
            } catch (Exception ex) {
                Logger.getLogger(RequestDISMAP.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        if(getType() == SEARCH_GOODS_REQUEST)
        {
            guiApplication.TraceEvenements("Réception de SEARCH_GOODS_REQUEST");
            try {
                traiteRequeteSGR(Socket, guiApplication);
            } catch (Exception ex) {
                Logger.getLogger(RequestDISMAP.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        if(getType() == TAKE_GOODS_REQUEST)
        {
            guiApplication.TraceEvenements("Réception de TAKE_GOODS_REQUEST");
            try {
                traiteRequeteTGR(Socket, guiApplication);
            } catch (Exception ex) {
                Logger.getLogger(RequestDISMAP.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        if(getType() == BUY_GOODS_REQUEST)
        {
            guiApplication.TraceEvenements("Réception de BUY_GOODS_REQUEST");
            try {
                traiteRequeteBGR(Socket, guiApplication);
            } catch (Exception ex) {
                Logger.getLogger(RequestDISMAP.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        if(getType() == DELIVERY_GOODS_REQUEST)
        {
            guiApplication.TraceEvenements("Réception de DELIVERY_GOODS_REQUEST");
            try {
                traiteRequeteDGR(Socket, guiApplication);
            } catch (Exception ex) {
                Logger.getLogger(RequestDISMAP.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        if(getType() == LIST_SALES_REQUEST)
        {
            guiApplication.TraceEvenements("Réception de LIST_SALES_REQUEST");
            try {
                traiteRequeteLIST_SALES(Socket, guiApplication);
            } catch (Exception ex) {
                Logger.getLogger(RequestDISMAP.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }
        
        if(getType() == BUY_GOODS_REQUEST_CO)
        {
            guiApplication.TraceEvenements("Réception de BUY_GOODS_REQUEST_CO");
            try {
                traiteRequeteBUY_GOODS_REQUEST_CO(Socket, guiApplication);
            } catch (Exception ex) {
                Logger.getLogger(RequestDISMAP.class.getName()).log(Level.SEVERE, null, ex);
            }
            return true;
        }

        else if (getType() == LOGOUT)
        {
            guiApplication.TraceEvenements("Réception d'une requête de fermeture de connexion");
            traiteRequeteLogout(Socket, guiApplication);
            return true;
        }
   
 
        return false;
    }
    
    
    public void traiteRequeteLogin(ISocket Socket, ConsoleServeur guiApplication) throws Exception
    {   
    
        BeanBDAccessMySQL   beanSql;
        //Récupération des informations
        Vector vInfos = (Vector) getChargeUtile();
        
        beanSql = ConnectToBd();
        String login = vInfos.get(0).toString();
        String password_DB = beanSql.findPasswpordByLogin(login);
        System.out.println("pass DB = " + password_DB);
       
            
        
        byte[] pwdUser= (byte[])vInfos.get(1);
        
        ResponseDISMAP rep = new ResponseDISMAP();
        if(password_DB != null)
        {
             // hash du password pour comparer avec le digest recu
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-512");
            byte[] hashedStringDB = messageDigest.digest(password_DB.getBytes());
            System.out.println("pass DB hash = " + hashedStringDB);
            System.out.println("pass user hash = " + pwdUser);
            if (MessageDigest.isEqual(pwdUser,hashedStringDB))
            {
                
                guiApplication.TraceEvenements("Login '" + vInfos.get(0) + "' accepté !");
                rep.setCodeRetour(YES);
            }
            else
            {
                guiApplication.TraceEvenements("Login '" + vInfos.get(0) + "'refusé !");
                rep.setCodeRetour(NO);
            }
        }
        else
        {
            guiApplication.TraceEvenements("Login '" + vInfos.get(0) + "'inconnu !");
            rep.setCodeRetour(NO);
        }
        //Déconnexion de la BD
        //...
        
        Socket.Send(rep);
    }
    public void traiteRequeteInsertLogin(ISocket Socket, ConsoleServeur guiApplication) throws Exception
    {   
        // Le serveur vérifie dans la bd, le digest.
        DataBaseAccessFactory dbaf;
        IDataBaseAccess db;
        ConnectionOptions options;
        BeanBDAccessMySQL   beanSql;
        //Récupération des informations
        Vector vInfos = (Vector) getChargeUtile();
        
        System.out.println("Connexion à la bd " );
        beanSql = ConnectToBd();
        
        int test = beanSql.traiteRequeteInsertLogin(vInfos.get(0).toString(),vInfos.get(1).toString());
   
        ResponseDISMAP rep = new ResponseDISMAP();
        if(test == 1)
        {
                guiApplication.TraceEvenements("Login '" + vInfos.get(0) + "' créé !");
                rep.setCodeRetour(YES);
        }
        else
        {
            guiApplication.TraceEvenements("Login '" + vInfos.get(0) + "'refusé !");
            rep.setCodeRetour(NO);
        }

        
        Socket.Send(rep);
    }

    public void traiteRequeteSGR(ISocket Socket, ConsoleServeur guiApplication) throws Exception
    {   
        
        BeanBDAccessMySQL   beanSql;
        ResponseDISMAP rep = new ResponseDISMAP();
        //Récupération des informations
        Vector vInfos = (Vector) getChargeUtile();
beanSql = ConnectToBd();
        Vector  b = new Vector ();
 
        List<Object> res = new ArrayList<Object>();
        String typePrecis = vInfos.get(0).toString();
        res=beanSql.getListSearchGoods(typePrecis);
        
            
        
        if(res.size()>0)
        {
            System.out.println("liste = " + res );
            rep.setCodeRetour(YES);
            rep.setChargeUtile(res);
            System.out.println("Quelque chose à envoyer");
        }
        else
        {
            rep.setCodeRetour(NO);
            System.out.println("Vide");
        }
        
        guiApplication.TraceEvenements("Envoie du listring réussie dans la BD");
        

        
        Socket.Send(rep);
    
    }
    public void traiteRequeteTGR(ISocket Socket, ConsoleServeur guiApplication) throws Exception
    {   
        // Le serveur vérifie dans la bd, le digest.
        DataBaseAccessFactory dbaf;
        IDataBaseAccess db;
        ConnectionOptions options;
        BeanBDAccessMySQL   beanSql;
        //Récupération des informations
        Vector vInfos = (Vector) getChargeUtile();
        
        System.out.println("Connexion à la bd " );
        beanSql = ConnectToBd();

        int numSerie=Integer.valueOf(vInfos.get(0).toString());
        boolean test = beanSql.UpdateAppareilEtat(1, numSerie); // 1 pour etatPaiement réservé
        ResponseDISMAP rep = new ResponseDISMAP();
        if(test)
        {
           
                guiApplication.TraceEvenements("Update de  '" + vInfos.get(0) + "' done !");
                rep.setCodeRetour(YES);
                Vector vRep = new Vector();
                vRep.add(numSerie);
                float price = beanSql.findFinalPriceBySerialNum(numSerie);
                vRep.add(price);
                rep.setChargeUtile(vRep);
        }
        else
        {
            guiApplication.TraceEvenements("Login '" + vInfos.get(0) + "'refusé !");
            rep.setCodeRetour(NO);
        }
        
        
        //Déconnexion de la BD
        //...
        
        Socket.Send(rep);
    }
public void traiteRequeteBGR(ISocket Socket, ConsoleServeur guiApplication) throws Exception
{   

        Vector vInfos = (Vector) getChargeUtile();
        Vector vNumSeries = (Vector)vInfos.get(0);
                

        /*************************************CHIFFRAGE DE LA REQUETE + LECTURE DE LA CLE DANS FICHIER*****************************/
        Security.addProvider(new BouncyCastleProvider());
        Provider prov[] = Security.getProviders();

        CryptoManager cm = new CryptoManager();
        Service s = cm.newInstance("DES");
        Chiffrement c = (Chiffrement)s ;

        ManipFichierCrypto mp = new ManipFichierCrypto();

        Cle cleFichier= mp.ReadFileKey("CleDES");
        System.out.println("cle" + cleFichier);
        c.init(cleFichier);
        String cipher = c.crypte(vInfos.get(1).toString()); //on crype le mode de paiement (CARTE,CASH...)
        
        /*****************************************HMAC DU MOYEN DE PAIEMENT **********************************************************/
        s = cm.newInstance("BC"); // on obtient en fonction de TriumVirat, le chemin de la class (Crypto.Triumvirat)
//        
        //cle = cm.genereCle("BC"); // on obtient une clé classe CleCaesar
//      
        System.out.println("service = " + s);
        //cle = service.generateKey(128);
        Authentication authen;
        authen = (Authentication) s ; 

        Security.addProvider(new BouncyCastleProvider());

        cleFichier= mp.ReadFileKey("CleHMAC");
        System.out.println("CLE = " + cleFichier);
        authen.init(cleFichier);
        byte [] hmac = authen.AddAuth(cipher);
 
  
        
        /*******************************AJOUT DONNEES DANS LE VECTEUR POUR ENVOIE VERS SERCO******************************************/
        Vector BuyGoods = new Vector();
        
        //BuyGoods.add(vInfos.get(0).toString()) ; // //numSerie
        BuyGoods.add(cipher) ; // //mode de paiement crypté
        BuyGoods.add(hmac) ; //mode de paiement crypté + HMAC
        
        RequestDISMAP req = new RequestDISMAP(BUY_GOODS_REQUEST_CO, BuyGoods);
        GSocketCo = new GestionSocket();
        guiApplication.TraceEvenements("serCo = " +FichierConfig.getProperty("serCo") );
        guiApplication.TraceEvenements("portCo = " +FichierConfig.getProperty("portCo") );
        
        
        /*******************************CONNEXION VERS SERCO*************************************************************************/
        GSocketCo.ConnectServeur(FichierConfig.getProperty("serCo"), Integer.parseInt(FichierConfig.getProperty("portCo")));
        guiApplication.TraceEvenements("CONNEXION AU SERVEUR CO");
        GSocketCo.Send(req);
        System.out.println("Apres la requete");
        //Attente de reponse du serveur
        
        
        /*******************************REPONSE DE SERCO*****************************************************************************/
        ResponseDISMAP repCo = (ResponseDISMAP) GSocketCo.Receive();
        ResponseDISMAP rep = new ResponseDISMAP();
        BeanBDAccessMySQL   beanSql;
        beanSql = ConnectToBd();
        if(repCo.getCodeRetour() == YES)
        {
         for(int k=0 ; k< vNumSeries.size(); k++)
         {
             boolean test = beanSql.UpdateAppareilEtatBG(2, (int)vNumSeries.get(k)); // 1 pour etatPaiement réservé
            if(test)
            {
                guiApplication.TraceEvenements("Update de  '" + vNumSeries.get(k) + "' failed !");
                rep.setCodeRetour(YES);
                
            }
            else 
            {
                guiApplication.TraceEvenements("Update de  '" + vNumSeries.get(k) + "' failed !");
                rep.setCodeRetour(ALREADY_PAIED);
                Socket.Send(rep);
            }
         }
            
            rep.setCodeRetour(YES);
        
        }
        
         else if(repCo.getCodeRetour() == BAD_PAIEMENT)
        {
            guiApplication.TraceEvenements("Mauvaise méthode de paiement !");
            rep.setCodeRetour(BAD_PAIEMENT);
        
        }


        Socket.Send(rep);
 }
    public void traiteRequeteDGR(ISocket Socket, ConsoleServeur guiApplication) throws Exception
    {   

        BeanBDAccessMySQL   beanSql;
        //Récupération des informations
        Vector vInfos = (Vector) getChargeUtile();
        
        System.out.println("Connexion à la bd " );
        beanSql = ConnectToBd();
        Vector numSeries = (Vector)vInfos.get(1);
        String adresse = vInfos.get(2).toString();
        String nomClient = vInfos.get(3).toString();
        float prix =0;
        
        
        int idClient = beanSql.FindIdClientByName(nomClient);
        
        // SI LE CLIENT N'EXISTE PAS, ON LE CREE
        if(idClient ==0) 
        {
            beanSql.traiteRequeteInsertClient(nomClient, adresse);
        }
        idClient = beanSql.FindIdClientByName(nomClient);
        String ModeDePaiement = vInfos.get(4).toString();
        
        
        // CALCULER LE PRIX TOTAL
        boolean bool=false;
        ResponseDISMAP rep = new ResponseDISMAP();
        // INSERTION "DES" FACTURES (Mais le client n'en recoit qu'une groupée
        for(int i =0 ; i<numSeries.size() ;i++)
        {
            prix = prix + beanSql.findFinalPriceBySerialNum((int)numSeries.get(i));
            bool=beanSql.traiteRequeteInsertFacture((int)numSeries.get(i), prix, idClient, 1,ModeDePaiement);
            if(!bool)
            {
                 guiApplication.TraceEvenements("Impossible insertion facture !");
                 rep.setCodeRetour(NO);
                 Socket.Send(rep);
            }
            else
                 guiApplication.TraceEvenements("insert de Facture réussie !");
            
        }
   
        
        String Facture ="FACTURE " +"\n" + "Nom du client =" + nomClient +"\n" + "Prix effectif=" + prix +"\n" +"Numéro de série appareil=" + numSeries +"\n" + "Adresse de facturation=" + adresse ;
 
        guiApplication.TraceEvenements("Facture client générée!");
        rep.setCodeRetour(YES);
        Vector vRep = new Vector();
        vRep.add(Facture);
        rep.setChargeUtile(vRep);
      
        
        
        //Déconnexion de la BD
        //...
        
        Socket.Send(rep);
    }
    public void traiteRequeteLIST_SALES(ISocket Socket, ConsoleServeur guiApplication) throws Exception
    {   
        
        BeanBDAccessMySQL   beanSql;
        ResponseDISMAP rep = new ResponseDISMAP();
        //Récupération des informations
        Vector vInfos = (Vector) getChargeUtile();
        beanSql = ConnectToBd();
        Vector  b = new Vector ();
        System.out.println("TAILLE LISTE VINFOS ="+ vInfos.size());
        List<Object> res ;
        res=beanSql.getFacturesByIds(vInfos);
        System.out.println("TAILLE LISTE ==========" + res.size());
            
        
        if(res.size()>0)
        {
            System.out.println("liste = " + res );
            rep.setCodeRetour(YES);
            rep.setChargeUtile(res);
            System.out.println("Quelque chose à envoyer");
        }
        else
        {
            rep.setCodeRetour(NO);
            System.out.println("Vide");
        }
        
        guiApplication.TraceEvenements("Envoie du listring réussie dans la BD");
        

        
        Socket.Send(rep);
    
    }
    private void traiteRequeteLogout(ISocket Socket, ConsoleServeur guiApplicaiton) {
        Socket.Close();
    }
    public void  traiteRequeteBUY_GOODS_REQUEST_CO(ISocket Socket, ConsoleServeur guiApplication) throws Exception
    {   

        
        //Récupération des informations
        Vector vInfos = (Vector) getChargeUtile();
        String moyenDePaiement = null;
        System.out.println("Connexion à la bd " );
        BeanBDAccessMySQL   beanSql = ConnectToBd();
        
        /***********************************     RECUPERATION CIPHER + HMAC      **********************************************/
     
        String CiphermoyenDePaiement = vInfos.get(0).toString() ; // sous forme de cipher
        byte[] hmac =(byte[]) vInfos.get(1);
        
        /***********************************     VERIFICATION DU HMAC     **********************************************/
        
        CryptoManager cm = new CryptoManager();
        Service s = cm.newInstance("BC");
 
        System.out.println("service = " + s);
        //cle = service.generateKey(128);
        Authentication authen;
        authen = (Authentication) s ; 

        Security.addProvider(new BouncyCastleProvider());
        ManipFichierCrypto mp = new ManipFichierCrypto();
        Cle cleFichier= mp.ReadFileKey("CleHMAC");
        System.out.println("CLE = " + cleFichier);
        authen.init(cleFichier);
        boolean ok = authen.verifyAuth(CiphermoyenDePaiement, hmac);
        
        
        /*************************************CHIFFRAGE DE LA REQUETE + LECTURE DE LA CLE DANS FICHIER*****************************/
        ResponseDISMAP rep = new ResponseDISMAP();
        if (ok)
        {
             guiApplication.TraceEvenements("Intégrité vérifié et authentification réussie !!!! '" );
        

                cm = new CryptoManager();
                s = cm.newInstance("DES");
                Chiffrement c = (Chiffrement)s ;
                cleFichier= mp.ReadFileKey("CleDES");
                System.out.println("cle" + cleFichier);
                c.init(cleFichier);
                moyenDePaiement = c.decrypt(CiphermoyenDePaiement); //on crype le mode de paiement (CARTE,CASH...)
                guiApplication.TraceEvenements("Moyen de paiement décrypté  = '" + moyenDePaiement);
                /*****************************************HMAC DU MOYEN DE PAIEMENT **********************************************************/


                


                if ("CARTE".equals(moyenDePaiement) || "CASH".equals(moyenDePaiement))
                {


                        guiApplication.TraceEvenements("Moyen de paiement '" + moyenDePaiement);
                        rep.setCodeRetour(YES);
                        String message = "Paiement par " + moyenDePaiement +"accepté";
                        rep.setChargeUtile(message);
                    
                   


                }
                else
                {
                    guiApplication.TraceEvenements("Moyen de paiement '" + moyenDePaiement);
                    rep.setCodeRetour(BAD_PAIEMENT);
                    String message = "Paiement par " + moyenDePaiement +"refusé ";
                    rep.setChargeUtile(message);
                }
        }
        else
        {
             rep.setCodeRetour(FAIL_AUTHENTICATION);
        }

                Socket.Send(rep);
                guiApplication.TraceEvenements("Send Reponse to SerMv");
                
    }

  
    public BeanBDAccessMySQL ConnectToBd()
     {
            DataBaseAccessFactory dbaf;
            IDataBaseAccess db;
            ConnectionOptions options;
            dbaf = DataBaseAccessFactory.getInstance();
            db = dbaf.getDataBaseAcces("MySQL");
            

            options = new ConnectionOptions();
            options.addOption("host", FichierConfig.getProperty("host"));
            options.addOption("port", FichierConfig.getProperty("portMySQL"));
            System.out.println("PORT MYSQL = " + FichierConfig.getProperty("portMySQL") );
            options.addOption("database", FichierConfig.getProperty("DB_name_MySQL"));
            options.addOption("user", FichierConfig.getProperty("userMySQL"));
            options.addOption("passwd", FichierConfig.getProperty("pwdMySQL"));
            int test = db.Connect(options);
            
            return (BeanBDAccessMySQL)db;
     }

    

    
 
}
