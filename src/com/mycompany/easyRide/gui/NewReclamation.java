/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.easyRide.gui;

import com.codename1.db.Database;
import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.messaging.Message;
import static com.codename1.processing.Result.JSON;
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.table.DefaultTableModel;
import com.codename1.ui.table.Table;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.table.TableModel;
import com.codename1.ui.util.Resources;
import com.mycompany.easyRide.entities.Reclamation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public class NewReclamation extends Form {
    private Database db;
    private Resources theme;

    public NewReclamation() {
                 theme = UIManager.initFirstTheme("/theme");

         TextField email = new TextField("","email");
        TextField objet= new TextField("", "objet");
        Button btn = new Button("Ajouter");
        
ComboBox combobox = new ComboBox();
        combobox.addItem("Location");
        combobox.addItem("Evenement");
        combobox.addItem("Maintenance");
        combobox.addItem("Commande");
       add(email).add(objet). add(combobox).add(btn);
       btn.addActionListener((evt) -> {
           
            ConnectionRequest con = new ConnectionRequest(
                    /*email.getText().toString()+"&type="+combobox.getSelectedItem().toString()+"&objet="+objet.getText().toString()*/);
            con.setUrl("http://localhost:8000/reclamation/add");
            con.setPost(false);
            con.addArgument("email", email.getText().toString());
            con.addArgument("type", combobox.getSelectedItem().toString());
            con.addArgument("objet", objet.getText().toString());
            //con.setDefaultCacheMode(ConnectionRequest.CachingMode.OFF);
           // con.isReadResponseForErrors();

             System.out.println(" ******************** "+con);
           con.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {
                       
            
             boolean resultOK = con.getResponseCode() == 200; //Code HTTP 200 OK
                con.removeResponseListener(this);
            if(resultOK){
                                        Dialog.show("Alert", "Reclamation ajoutée avec succes", new Command("OK"));

                }else{
                                    Dialog.show("Alert", "error", new Command("OK"));

                }
                         
              
                
            }
        });
           
    NetworkManager.getInstance().addToQueue(con);               
                //finvalider action
                
       });


addComponent(new Label("Easy Ride"));
getToolbar().addCommandToOverflowMenu("Retour", null, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
              ReclamationIndex f2 = new ReclamationIndex();
              f2.show();
            }
        });
show();
    }
    
    
}

