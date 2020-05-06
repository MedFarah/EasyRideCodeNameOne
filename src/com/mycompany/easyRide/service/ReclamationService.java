/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.easyRide.service;

import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.events.ActionListener;
import com.mycompany.easyRide.entities.Reclamation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public class ReclamationService {
    public ArrayList<Reclamation> tasks;
    
    public static ReclamationService instance=null;
    public boolean resultOK;
    private ConnectionRequest req;

    private ReclamationService() {
         req = new ConnectionRequest();
    }

    public static ReclamationService getInstance() {
        if (instance == null) {
            instance = new ReclamationService();
        }
        return instance;
    }

    public boolean addTask(String t,String b) {
        String url = "http://localhost:8000/reclamation/add/tasks/" + t+ "/" + b;
        req.setUrl(url);
        req.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {
                resultOK = req.getResponseCode() == 200; //Code HTTP 200 OK
                req.removeResponseListener(this);
            }
        });
        NetworkManager.getInstance().addToQueueAndWait(req);
        return resultOK;
    }
    
    public ArrayList<Reclamation> parseTasks(String jsonText){
        try {
            tasks=new ArrayList<>();
            JSONParser j = new JSONParser();
            Map<String,Object> tasksListJson = j.parseJSON(new CharArrayReader(jsonText.toCharArray()));
            
            List<Map<String,Object>> list = (List<Map<String,Object>>)tasksListJson.get("root");
            for(Map<String,Object> obj : list){
                Float id = Float.parseFloat(obj.get("id").toString());
              int i = (int)Math.round(id);
               String tr = obj.get("typereclamation").toString();
              String email = obj.get("email").toString();
              String o = obj.get("objet").toString();
              String s = obj.get("status").toString();
              Reclamation r = new Reclamation();
              r.setTypeReclamation(tr);
              r.setEmail(email);
              r.setId(i);
              r.setObjet(o);
              r.setStatus(s);
                tasks.add(r);
            }
            
            
        } catch (IOException ex) {
            
        }
        return tasks;
    }
    
    public ArrayList<Reclamation> getAllTasks(){
        String url = "http://localhost:8000/reclamation/";
        req.setUrl(url);
        req.setPost(false);
        req.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {
                tasks = parseTasks(new String(req.getResponseData()));
                req.removeResponseListener(this);
            }
        });
        NetworkManager.getInstance().addToQueueAndWait(req);
        return tasks;
    }

   
}
