/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.easyRide.gui;

import com.codename1.components.FloatingHint;
import com.codename1.components.ImageViewer;
import com.codename1.components.ToastBar;
import com.codename1.db.Database;
import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.messaging.Message;
import static com.codename1.processing.Result.JSON;
import com.codename1.share.EmailShare;
import com.codename1.ui.AutoCompleteTextField;
import com.codename1.ui.Button;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Command;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Dialog;
import com.codename1.ui.Display;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextComponent;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.URLImage;
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
import com.codename1.ui.validation.LengthConstraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.ui.validation.Validator;
import com.codename1.util.regex.RE;
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
    TextField e = new TextField("", "");
    TextField email = new TextField("", "email", 30, TextArea.EMAILADDR);
    TextField objet = new TextField("", "objet");
    //   TextComponent email = new TextComponent().label("Email                                   ");
    //         TextComponent objet = new TextComponent().label("Objet                                     ").multiline(true);

    public NewReclamation() {
        theme = UIManager.initFirstTheme("/theme");
        this.setTitle("Add reclamation");
        //  AutoCompleteTextField ac = new AutoCompleteTextField("Short", "Shock", "Sholder", "Shrek");
        //    ac.setMinimumElementsShownInPopup(5);
        Button btn = new Button("Ajouter");
        e.setHidden(true);
        Image img = theme.getImage("rec.png");
        img.animate();
        img.scaledHeight(1500);
        img.fill(1500, 1500);
        ComboBox combobox = new ComboBox();
        combobox.addItem("Location");
        combobox.addItem("Evenement");
        combobox.addItem("Maintenance");
        combobox.addItem("Commande");
   
        add(e).add(email).add(objet).add(combobox).add(img).add(btn);

        btn.addActionListener((evt) -> {
            if (checkValid()) {
                ConnectionRequest con = new ConnectionRequest( /*email.getText().toString()+"&type="+combobox.getSelectedItem().toString()+"&objet="+objet.getText().toString()*/);
                con.setUrl("http://localhost:8000/reclamation/add");
                con.setPost(false);
                con.addArgument("email", email.getText().toString());
                con.addArgument("type", combobox.getSelectedItem().toString());
                con.addArgument("objet", objet.getText().toString());
                //con.setDefaultCacheMode(ConnectionRequest.CachingMode.OFF);
                // con.isReadResponseForErrors();

                System.out.println(" ******************** " + con);
                con.addResponseListener(new ActionListener<NetworkEvent>() {
                    @Override
                    public void actionPerformed(NetworkEvent evt) {

                        boolean resultOK = con.getResponseCode() == 200; //Code HTTP 200 OK
                        con.removeResponseListener(this);
                        if (resultOK) {
                            Dialog.show("Alert", "Reclamation ajoutée avec succes", new Command("OK"));

                        } else {
                            Dialog.show("Alert", "error", new Command("OK"));

                        }

                    }
                });

                NetworkManager.getInstance().addToQueue(con);
                //finvalider action
            } //fin validator
            else {
                Dialog.show("Alert", "Verifier remplissage du formulaire", new Command("OK"));
            }
        });

        getToolbar().addCommandToOverflowMenu("Retour", null, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
                ReclamationIndex f2 = new ReclamationIndex();
                f2.show();
            }
        });
        show();
    }

    public boolean checkValid() {
        String mail = email.getText();
        String obj = objet.getText();
        Validator validator = new Validator();
        validator.setShowErrorMessageForFocusedComponent(true);
        validator.addConstraint(email, RegexConstraint.validEmail("Invalid Email !!!"));
        String c = validator.getErrorMessage(email);

        validator.addConstraint(objet, new LengthConstraint(5, "Objet length doit etre > 5"));
        // RE pattern =new RE("^([a-zA-Z0-9.!#$%&'*+/=?^`{|}~]|-|_)+@((\\\\[[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}\\\\.[0-9]{1,3}\\\\])|(([a-zA-Z\\\\-0-9]+\\\\.)+[a-zA-Z]{2,}))$");
        //  if((pattern.match(mail)) && (obj.length()>5)){ return true;}
        if (!validator.isValid()) {
            //  Dialog.show("Alert", "Email Invalid", new Command("OK"));
            ToastBar.showErrorMessage("invalid Email format !!!");
            return false;
        }
        if (!(obj.length() > 5)) {
            Dialog.show("Alert", "Objet length doit etre > 5 ", new Command("OK"));
            return false;
        }
        return true;
    }

}
