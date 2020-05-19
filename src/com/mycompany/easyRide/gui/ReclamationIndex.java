/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.easyRide.gui;

import com.codename1.capture.Capture;
import com.codename1.components.ImageViewer;
import com.codename1.components.MediaPlayer;
import com.codename1.components.MultiButton;
import com.codename1.components.ToastBar;
import com.codename1.db.Database;
import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.FileSystemStorage;
import com.codename1.io.JSONParser;
import com.codename1.io.Log;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.location.Location;
import com.codename1.location.LocationManager;
import com.codename1.media.Media;
import com.codename1.media.MediaManager;
import com.codename1.messaging.Message;
import com.codename1.notifications.LocalNotification;
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
import com.codename1.ui.Graphics;
import com.codename1.ui.Image;
import com.codename1.ui.Label;
import com.codename1.ui.TextArea;
import com.codename1.ui.TextField;
import com.codename1.ui.Toolbar;
import com.codename1.ui.URLImage;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Style;
import com.codename1.ui.plaf.UIManager;
import com.codename1.ui.spinner.Picker;
import com.codename1.ui.table.DefaultTableModel;
import com.codename1.ui.table.Table;
import com.codename1.ui.table.TableLayout;
import com.codename1.ui.table.TableModel;
import com.codename1.ui.util.Resources;
import com.mycompany.easyRide.entities.Reclamation;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author ASUS
 */
public class ReclamationIndex extends Form {

    private Database db;
    private Resources theme;

    public ReclamationIndex() {
        theme = UIManager.initFirstTheme("/theme");
        Label l = new Label("Bienvenue sur l'espace des reclamations");
        l.setWidth(50);
        Image img = theme.getImage("reclam.jpg");
        img.animate();
        img.scaledHeight(150);
        img.fill(150, 150);
        add(l).add(img);
        // Location position = LocationManager.getLocationManager().getCurrentLocationSync();
        // System.out.println("***"+position);
        Label picture = new Label("Inserer un image ici", "Container");
        add(picture);
        Style s = UIManager.getInstance().getComponentStyle("TitleCommand");

        Image camera = FontImage.createMaterial(FontImage.MATERIAL_CAMERA, s);
        getToolbar().addCommandToRightBar("", camera, (ev) -> {
            try {
                int width = Display.getInstance().getDisplayWidth();
                Image capturedImage = Image.createImage(Capture.capturePhoto(width, -1));
                Image roundMask = Image.createImage(width, capturedImage.getHeight(), 0xff000000);
                Graphics gr = roundMask.getGraphics();
                gr.setColor(0xffffff);
                gr.fillArc(0, 0, width, width, 0, 360);
                Object mask = roundMask.createMask();
                capturedImage = capturedImage.applyMask(mask);
                picture.setIcon(capturedImage);
                System.out.println("img ================>" + capturedImage.toString() + " " + picture.getName());
                revalidate();
            } catch (IOException err) {
                Log.e(err);
            }
        });
        //    Style s = UIManager.getInstance().getComponentStyle("Title");
        FontImage iconn = FontImage.createMaterial(FontImage.MATERIAL_VIDEO_LIBRARY, s);

        getToolbar().addCommandToRightBar(new Command("", iconn) {
            @Override
            public void actionPerformed(ActionEvent evt) {
                /*                Display.getInstance().openGallery((e) -> {
                    if (e != null && e.getSource() != null) {
                        String file = (String) e.getSource();
                        try {
                            Media video = MediaManager.createMedia(file, true);
                            removeAll();
                            add(BorderLayout.CENTER, new MediaPlayer(video));
                            revalidate();
                        } catch (IOException err) {
                            Log.e(err);
                        }
                    }
                }, Display.GALLERY_VIDEO);*/

                try {
                    final MediaPlayer mpPlayer = new MediaPlayer();
                    String value = Capture.captureVideo();
                    System.out.println("Captured Video " + value);
                    if (value != null) {
                        System.out.println("Playing Video");
                        InputStream is = FileSystemStorage.getInstance().openInputStream(value);
                        String strMime = "video/mp4";
                        System.out.println("Input Stream" + is.available());
                        mpPlayer.setName("bla");
                        mpPlayer.setDataSource(is, strMime, new Runnable() {
                            public void run() {
                                System.out.println("reset the clip for playback");
                            }
                        });
                        addComponent(BorderLayout.CENTER, mpPlayer);
                        revalidate();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        Toolbar tb = this.getToolbar();
        Image icon = theme.getImage("icon.png");
        Container topBar = BorderLayout.east(new Label(icon));
        topBar.add(BorderLayout.SOUTH, new Label("Cool App Tagline...", "SidemenuTagline"));
        topBar.setUIID("SideCommand");
        tb.addComponentToSideMenu(topBar);

        tb.addMaterialCommandToSideMenu("Index", FontImage.MATERIAL_HOME, e -> {
            Dialog.show("Alert", "Click sur la ligne du column objet pour modifier \n Click column type to delete \n Click email rows to send email", new Command("OK"));

            ArrayList<Float> listR = new ArrayList<Float>();
            ArrayList<Reclamation> rec = new ArrayList<Reclamation>();

            ConnectionRequest con = new ConnectionRequest("http://localhost:8000/reclamation/indexcn1");
            con.setPost(false);
            System.out.println(" ******************** " + con);

            con.addResponseListener(new ActionListener<NetworkEvent>() {

                @Override
                public void actionPerformed(NetworkEvent evt) {
                    String response = new String(con.getResponseData());
                    try {
                        JSONParser j = new JSONParser();
                        Map<String, Object> tasksListJson = j.parseJSON(new CharArrayReader(response.toCharArray()));

                        List<Map<String, Object>> list = (List<Map<String, Object>>) tasksListJson.get("root");
                        for (Map<String, Object> obj : list) {

                            Float id = Float.parseFloat(obj.get("id").toString());
                            int i = (int) Math.round(id);
                            String tr = obj.get("typereclamation").toString();
                            String email = obj.get("email").toString();
                            String o = obj.get("objet").toString();
                            String s = obj.get("status").toString();
                            String des = obj.get("description").toString();
                            String img = obj.get("image").toString();
                            Reclamation r = new Reclamation();
                            r.setTypeReclamation(tr);
                            r.setEmail(email);
                            r.setId(i);
                            r.setObjet(o);
                            r.setStatus(s);
                            r.setImage(img);
                            r.setDescription(des);
                            rec.add(r);
                            listR.add(id);

                        }
                        Form hi = new Form("Index", new TableLayout(2, 3));
                        Object op[][] = new Object[100][100];//listR.toArray();
                        int i = 1;
                        for (Reclamation r : rec) {

                            //
                            op[i][0] = r.getObjet();
                            op[i][1] = r.getTypeReclamation();
                            op[i][2] = r.getEmail();
                            op[i][3] = r.getId();
                            // 
                            // op[i][3]=im;
                            i++;
                        }
                        /*  hi.add(new Label("Type")).
                                add(new Label("Email"))
                               // add(new Label("Third")).
                                //add(new Label("Fourth")).
                                ;
                             for (Reclamation f : rec){
                                 //hi.add(String.valueOf(f.getId()));
                                 hi.add(f.getTypeReclamation());
                                 hi.add(f.getEmail());
                             }*/
                        TableModel model = new DefaultTableModel(new String[]{"Objet", "Type", "Email"}, op/*new Object[][] {
                {listR, "Row A", "Row X"},
                
                }*/) {
                            public boolean isCellEditable(int row, int col) {
                                return col != 0;
                            }
                        };
                        Table table = new Table(model) {
                            @Override
                            protected Component createCell(Object value, int row, int column, boolean editable) { // (1)
                                Component cell = null;
                                for (int i = 0; i < rec.size(); i++) {
                                    if (column == 2) { // (2)
                                        
                                        Picker p = new Picker();
                                        p.setType(Display.PICKER_TYPE_STRINGS);
                                        p.setStrings("Votre Reclamation a été traité", "Votre Reclamation est en cours de traitement", "Reclamation en attente", "Reclamation refusée");
                                        p.setSelectedString((String) value); // (3)
                                        p.setUIID("" + l);
                                        p.addActionListener((e) -> {
                                           getModel().setValueAt(row, column, p.getSelectedString());
                                            Message m = new Message(p.getSelectedString());
                                           Display.getInstance().sendMessage(new String[]{getModel().getValueAt(row, 2).toString()}, "EasyRide Reclamation ", m);
                                          System.out.println("$$$$$$$$$ "+(row - 1)+" ========>"+getModel().getValueAt(row, 2).toString());
                                            if( p.getSelectedString().equals("Reclamation refusée") ){
                                            ConnectionRequest con = new ConnectionRequest();
                                            con.setUrl("http://localhost:8000/reclamation/" + rec.get(row - 1).getId() + "/deleteCodeName?s=1");
                                            con.setPost(false);
                                            //  con.addArgument("description", desc.getText().toString());

                                            con.addResponseListener(new ActionListener<NetworkEvent>() {
                                                @Override
                                                public void actionPerformed(NetworkEvent evt) {

                                                    boolean resultOK = con.getResponseCode() == 200; //Code HTTP 200 OK
                                                    con.removeResponseListener(this);
                                                    if (resultOK) {
                                                        Dialog.show("Alert", "Reclamation supprimée avec succes", new Command("OK"));
                                                        ReclamationIndex ri = new ReclamationIndex();
                                                        ri.show();

                                                    } else {
                                                        Dialog.show("Alert", "error", new Command("OK"));
                                                        System.out.println("*****" + resultOK);

                                                    }

                                                }
                                            });

                                            NetworkManager.getInstance().addToQueue(con);
                                            //finvalider action
                                            }
                                            if(p.getSelectedString().equals("Votre Reclamation a été traité") ){//){
                                                                                            ConnectionRequest con = new ConnectionRequest();
                                            con.setUrl("http://localhost:8000/reclamation/" + rec.get(row - 1).getId() + "/deleteCodeName?s=0");
                                            con.setPost(false);
                                            //  con.addArgument("description", desc.getText().toString());

                                            con.addResponseListener(new ActionListener<NetworkEvent>() {
                                                @Override
                                                public void actionPerformed(NetworkEvent evt) {

                                                    boolean resultOK = con.getResponseCode() == 200; //Code HTTP 200 OK
                                                    con.removeResponseListener(this);
                                                    if (resultOK) {
                                                        Dialog.show("Alert", "Reclamation traiter avec succes", new Command("OK"));
                                                        ReclamationIndex ri = new ReclamationIndex();
                                                        ri.show();

                                                    } else {
                                                        Dialog.show("Alert", "error", new Command("OK"));
                                                        System.out.println("*****" + resultOK);

                                                    }

                                                }
                                            });

                                            NetworkManager.getInstance().addToQueue(con);
                                            //finvalider action
                                            } 
                                        }); // (4) 

                                        cell = p;

                                    } else {
                                        cell = super.createCell(value, row, column, editable);
                                    }

                                    if (row > -1 && row % 2 == 0) { // (5)
                                        // pinstripe effect 
                                        cell.getAllStyles().setBgColor(0xeeeeee);
                                        cell.getAllStyles().setBgTransparency(255);
                                    }

//******************************************update***************************************************
                                    if (column == 0) { // (2)
                                        /* Picker p = new Picker();
            p.setType(Display.PICKER_TYPE_STRINGS);
            p.setStrings("Votre Reclamation a été traité", "Votre Reclamation est en cours de traitement", "Reclamation en attente", "Reclamation refusée");
            p.setSelectedString((String)value); // (3)
            p.setUIID(""+i);
            p.addActionListener((e) -> {getModel().setValueAt(row, column, p.getSelectedString());
            Message m = new Message(p.getSelectedString());
            int a = Integer.parseInt(p.getUIID().trim());
            Display.getInstance().sendMessage(new String[] {getModel().getValueAt(a, 2).toString()}, "Reclamation Traité", m);
    
            } ); // (4) */
                                        cell = super.createCell(value, row, column, editable);
                                        cell.addDragOverListener((e) -> {
                                            ToastBar.showMessage("Click to update", FontImage.MATERIAL_INFO);
                                        });
                                        cell.addPointerPressedListener((evt) -> {
                                            boolean b = rec.get(row - 1).getObjet().equals(getModel().getValueAt(row, 0).toString());
                                            boolean c = ((String) value).equals(getModel().getValueAt(row, 0).toString());
                                            System.out.println("******" + b + " ------------" + c + "   row " + row);
                                            Form hi = new Form("Update Reclamation");
                                            hi.addComponent(new Label("Email"));
                                            TextField email = new TextField("", "email");
                                            email.setText(rec.get(row - 1).getEmail());
                                            hi.add(email);
                                            hi.addComponent(new Label("Objet"));
                                            TextField objet = new TextField("", "objet");
                                            objet.setText(rec.get(row - 1).getObjet());
                                            hi.add(objet);
                                            hi.addComponent(new Label("Description"));
                                            TextField desc = new TextField("", "Description");
                                            desc.setText(rec.get(row - 1).getDescription());
                                            hi.add(desc);
                                            hi.addComponent(new Label("Status"));
                                            TextField status = new TextField("", "status");
                                            status.setText(rec.get(row - 1).getStatus());
                                            hi.add(status);
                                            Button btn = new Button("Ajouter");

                                            ComboBox combobox = new ComboBox();
                                            combobox.addItem("Location");
                                            combobox.addItem("Evenement");
                                            combobox.addItem("Maintenance");
                                            combobox.addItem("Commande");
                                            combobox.setSelectedItem(rec.get(row - 1).getTypeReclamation());
                                            hi.add(combobox).add(btn);
                                            btn.addActionListener((e) -> {
                                                ConnectionRequest con = new ConnectionRequest();
                                                con.setUrl("http://localhost:8000/reclamation/" + rec.get(row - 1).getId() + "/editCodeName");
                                                con.setPost(false);
                                                con.addArgument("email", email.getText().toString());
                                                con.addArgument("type", combobox.getSelectedItem().toString());
                                                con.addArgument("objet", objet.getText().toString());
                                                con.addArgument("status", status.getText().toString());
                                                con.addArgument("description", desc.getText().toString());

                                                con.addResponseListener(new ActionListener<NetworkEvent>() {
                                                    @Override
                                                    public void actionPerformed(NetworkEvent evt) {

                                                        boolean resultOK = con.getResponseCode() == 200; //Code HTTP 200 OK
                                                        con.removeResponseListener(this);
                                                        if (resultOK) {
                                                            Dialog.show("Alert", "Reclamation modifier avec succes", new Command("OK"));

                                                        } else {
                                                            Dialog.show("Alert", "error", new Command("OK"));
                                                            System.out.println("*****" + resultOK);

                                                        }

                                                    }
                                                });

                                                NetworkManager.getInstance().addToQueue(con);
                                                //finvalider action
                                            });
                                            Toolbar tb = hi.getToolbar();
                                            Image icon = theme.getImage("icon.png");
                                            Container topBar = BorderLayout.east(new Label(icon));
                                            topBar.add(BorderLayout.SOUTH, new Label("Cool App Tagline...", "SidemenuTagline"));
                                            topBar.setUIID("SideCommand");
                                            tb.addComponentToSideMenu(topBar);

                                            tb.addMaterialCommandToSideMenu("Home", FontImage.MATERIAL_HOME, e -> {
                                            });
                                            tb.addMaterialCommandToSideMenu("Reclamation", FontImage.MATERIAL_WEB, e -> {
                                                try {
                                                    ReclamationIndex ri = new ReclamationIndex();
                                                    ri.show();
                                                } catch (Exception ee) {
                                                    System.out.println("****" + ee.getMessage());

                                                }

                                            });
                                            tb.addMaterialCommandToSideMenu("Settings", FontImage.MATERIAL_SETTINGS, e -> {
                                            });
                                            tb.addMaterialCommandToSideMenu("About", FontImage.MATERIAL_INFO, e -> {
                                            });
                                            hi.getToolbar().addCommandToOverflowMenu("Retour", null, new ActionListener() {

                                                @Override
                                                public void actionPerformed(ActionEvent evt) {
                                                    ReclamationIndex f2 = new ReclamationIndex();
                                                    f2.show();
                                                }
                                            });

                                            hi.show();

                                        });

                                    }

                                    //*************************************************delete*********************************************
                                    if (column == 1) { // (2)
                                        cell = super.createCell(value, row, column, editable);
                                        cell.addDragOverListener((e) -> {
                                            ToastBar.showMessage("Click to delete!", FontImage.MATERIAL_WARNING);
                                            System.out.println("****ttt");
                                        });
                                        cell.addPointerPressedListener((evt) -> {

                                            ConnectionRequest con = new ConnectionRequest();
                                            con.setUrl("http://localhost:8000/reclamation/" + rec.get(row - 1).getId() + "/deleteCodeName?s=1");
                                            con.setPost(false);
                                            //  con.addArgument("description", desc.getText().toString());

                                            con.addResponseListener(new ActionListener<NetworkEvent>() {
                                                @Override
                                                public void actionPerformed(NetworkEvent evt) {

                                                    boolean resultOK = con.getResponseCode() == 200; //Code HTTP 200 OK
                                                    con.removeResponseListener(this);
                                                    if (resultOK) {
                                                        Dialog.show("Alert", "Reclamation supprimée avec succes", new Command("OK"));
                                                        ReclamationIndex ri = new ReclamationIndex();
                                                        ri.show();

                                                    } else {
                                                        Dialog.show("Alert", "error", new Command("OK"));
                                                        System.out.println("*****" + resultOK);

                                                    }

                                                }
                                            });

                                            NetworkManager.getInstance().addToQueue(con);
                                            //finvalider action
                                        });

                                    }

                                }
                                return cell;
                            }
                        };
                        hi.add(BorderLayout.CENTER, table);
                        hi.getToolbar().addCommandToOverflowMenu("Retour", null, new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                ReclamationIndex f2 = new ReclamationIndex();
                                f2.show();
                            }
                        });

                        hi.getToolbar().addSearchCommand(e -> {
                            String text = (String) e.getSource();
                            if (text == null || (text.length() > 0 && text.length() < 7)) {
                                // clear search
                                for (Component cmp : hi.getContentPane()) {
                                    cmp.setHidden(false);
                                    cmp.setVisible(true);
                                }
                                hi.getContentPane().animateLayout(150);
                            } else {
                                text = text.toLowerCase();
                                /*   for(Component cmp : hi.getContentPane()) {
                                                    MultiButton mb = (MultiButton)cmp;
                                                    String line1 = mb.getTextLine1();
                                                    String line2 = mb.getTextLine2();
                                                    boolean show = line1 != null && line1.toLowerCase().indexOf(text) > -1 ||
                                                            line2 != null && line2.toLowerCase().indexOf(text) > -1;
                                                    mb.setHidden(!show);
                                                    mb.setVisible(show);
                                                }*/

                                for (Reclamation d : rec) {
                                    if (d.getEmail() != null && d.getEmail().contains(text)) {
                                        //something here
                                        Form f = new Form(" Reclamation");
                                        f.addComponent(new Label("Email"));
                                        TextField email = new TextField("", "email");
                                        email.setText(d.getEmail());

                                        f.add(email);
                                        f.addComponent(new Label("Objet"));
                                        TextField objet = new TextField("", "objet");
                                        objet.setText(d.getObjet());
                                        f.add(objet);
                                        f.addComponent(new Label("Description"));
                                        TextField desc = new TextField("", "Description");
                                        desc.setText(d.getDescription());
                                        f.add(desc);
                                        f.addComponent(new Label("Status"));
                                        TextField status = new TextField("", "status");
                                        status.setText(d.getStatus());
                                        f.add(status);
                                        f.addComponent(new Label("Type Reclamation"));
                                        TextField type = new TextField("", "Type");
                                        type.setText(d.getTypeReclamation());
                                        f.add(type);
                                        f.addComponent(new Label("Image "));
                                        int deviceWidth = Display.getInstance().getDisplayWidth();
                                        Image placeholder = Image.createImage(deviceWidth, deviceWidth, 0xbfc9d2);
                                        EncodedImage enc = EncodedImage.createFromImage(placeholder, false);
                                        //EncodedImage placeholder, String storageFile, String url
                                        Image img4 = URLImage.createToStorage(enc, "Medium_" + "http://localhost:8080/pi-project/web/uploads/photos/" + d.getImage(), "http://localhost:8080/pi-project/web/uploads/photos/" + d.getImage());
                                        ImageViewer im = new ImageViewer(img4);
                                        f.add(im);
                                        f.getToolbar().addCommandToOverflowMenu("Retour", null, new ActionListener() {

                                            @Override
                                            public void actionPerformed(ActionEvent evt) {
                                                ReclamationIndex f2 = new ReclamationIndex();
                                                f2.show();
                                            }
                                        });
                                        f.show();

                                    } //endif

                                }

                                hi.getContentPane().animateLayout(150);
                            }
                        }, 4);

                        hi.show();
                    } catch (IOException ex) {

                    }

                }
            });//fin conn
            NetworkManager.getInstance().addToQueue(con);
            //finvalider action

        });
        tb.addMaterialCommandToSideMenu("New", FontImage.MATERIAL_WEB, e -> {
            NewReclamation nr = new NewReclamation();
            nr.show();
        });
        tb.addMaterialCommandToSideMenu("Chart", FontImage.MATERIAL_SETTINGS, e -> {
            ReclamationCharts rc = new ReclamationCharts();
            rc.createPieChartForm().show();
        });
        tb.addMaterialCommandToSideMenu("Mail", FontImage.MATERIAL_INFO, e -> {
            Message m = new Message("Body of message");
            Display.getInstance().sendMessage(new String[]{"hamouchka7@gmail.com"}, "Subject of message", m);

        });

        show();
    }

    public ArrayList<Reclamation> getRec(ArrayList<Reclamation> r) {
        return r;
    }
}
