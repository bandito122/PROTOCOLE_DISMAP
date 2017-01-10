/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RequestResponseDISMAP;


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
import UtilsDISMAP.FichierConfig;
import beans.BeanBDAccessMySQL;
import beans.ConnectionOptions;
import beans.DataBaseAccessFactory;
import beans.IDataBaseAccess;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;



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
        // Le serveur vérifie dans la bd, le digest.
        DataBaseAccessFactory dbaf;
        IDataBaseAccess db;
        ConnectionOptions options;
        BeanBDAccessMySQL   beanSql;
        //Récupération des informations
        Vector vInfos = (Vector) getChargeUtile();
        
        System.out.println("Connexion à la bd " );
        beanSql = ConnectToBd();
        System.out.println("Recherche du password dont le login : " + vInfos.get(0).toString());
        //String passwordHASHBD = beanSql.findPasswpordByLoginHashCode(vInfos.get(0).toString());
        String pwdHashCSV= FichierConfig.getProperty("passHash").toString();
        String pwdUser=vInfos.get(1).toString();
        ResponseDISMAP rep = new ResponseDISMAP();
        if(pwdHashCSV != null)
        {
            System.out.println("pass CSV = " + pwdHashCSV);
            System.out.println("pass user = " + pwdUser);
            if (pwdHashCSV.equals(pwdUser))
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
        Integer hashpass = vInfos.get(1).toString().hashCode() ; 
        
        int test = beanSql.traiteRequeteInsertLogin(vInfos.get(0).toString(),hashpass.toString());
   
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
     
        Vector BuyGoods = new Vector();
        BuyGoods.add(vInfos.get(0).toString()) ; // //numSerie
        BuyGoods.add(vInfos.get(1).toString()) ; // //mode de paiement
        
        // envoie au serveur compta
        
        RequestDISMAP req = new RequestDISMAP(BUY_GOODS_REQUEST_CO, BuyGoods);
        GSocketCo = new GestionSocket();
        guiApplication.TraceEvenements("serCo = " +FichierConfig.getProperty("serCo") );
        guiApplication.TraceEvenements("portCo = " +FichierConfig.getProperty("portCo") );
        GSocketCo.ConnectServeur(FichierConfig.getProperty("serCo"), Integer.parseInt(FichierConfig.getProperty("portCo")));
        guiApplication.TraceEvenements("CONNEXION AU SERVEUR CO");
        GSocketCo.Send(req);
        System.out.println("Apres la requete");
        //Attente de reponse du serveur
        ResponseDISMAP repCo = (ResponseDISMAP) GSocketCo.Receive();
        ResponseDISMAP rep = new ResponseDISMAP();
        if(repCo.getCodeRetour() == YES)
        {
            guiApplication.TraceEvenements("BUYGOODS avec  '" + vInfos.get(0) + "par " + vInfos.get(1).toString() + " done !");
            rep.setCodeRetour(YES);
        
        }
        else if(repCo.getCodeRetour() == ALREADY_PAIED)
        {
            guiApplication.TraceEvenements("Vous avez déjà payé !");
            rep.setCodeRetour(ALREADY_PAIED);
        
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
        
        int numSerie=(int)vInfos.get(1);
        String adresse = vInfos.get(2).toString();
        String nomClient = vInfos.get(3).toString();
        float prix = beanSql.findFinalPriceBySerialNum(numSerie);
        
        int idClient = beanSql.FindIdClientByName(nomClient);
        if(idClient ==0) // si client n'existe pas alors on le cree
        {
            beanSql.traiteRequeteInsertClient(nomClient, adresse);
        }
        String ModeDePaiement = vInfos.get(3).toString();
        boolean bool = beanSql.traiteRequeteInsertFacture(numSerie, prix, idClient, 1,ModeDePaiement);
        String Facture ="FACTURE " +"\n" + "Nom du client =" + nomClient +"\n" + "Prix effectif=" + prix +"\n" +"Numéro de série appareil=" + numSerie +"\n" + "Adresse de facturation=" + adresse ;
        ResponseDISMAP rep = new ResponseDISMAP();
        if(bool)
        {
           
                guiApplication.TraceEvenements("insert de Facture réussie !");
                rep.setCodeRetour(YES);
                Vector vRep = new Vector();
                vRep.add(Facture);
                rep.setChargeUtile(vRep);
        }
        else
        {
            guiApplication.TraceEvenements("Impossible insertion facture !");
            rep.setCodeRetour(NO);
        }
        
        
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
        
        System.out.println("Connexion à la bd " );
        BeanBDAccessMySQL   beanSql = ConnectToBd();
        String moyenDePaiement = vInfos.get(1).toString() ;
        int numSerie=Integer.valueOf(vInfos.get(0).toString());
        
        ResponseDISMAP rep = new ResponseDISMAP();

                
        if ("CARTE".equals(moyenDePaiement) || "CASH".equals(moyenDePaiement))
        {
            boolean test = beanSql.UpdateAppareilEtatBG(2, numSerie); // 1 pour etatPaiement réservé
            if(test)
            {
                guiApplication.TraceEvenements("Moyen de paiement '" + moyenDePaiement);
                guiApplication.TraceEvenements("Update de  '" + vInfos.get(0) + "' done !");
                rep.setCodeRetour(YES);
                String message = "Paiement par " + moyenDePaiement +"accepté";
                rep.setChargeUtile(message);
            }
            else
                guiApplication.TraceEvenements("Update de  '" + vInfos.get(0) + "' failed !");
            
                rep.setCodeRetour(ALREADY_PAIED);


        }
        else
        {
            guiApplication.TraceEvenements("Moyen de paiement '" + moyenDePaiement);
            rep.setCodeRetour(BAD_PAIEMENT);
            String message = "Paiement par " + moyenDePaiement +"refusé ";
            rep.setChargeUtile(message);
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
