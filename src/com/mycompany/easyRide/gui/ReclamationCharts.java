/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.easyRide.gui;

import com.codename1.charts.ChartComponent;
import com.codename1.charts.models.CategorySeries;
import com.codename1.charts.renderers.DefaultRenderer;
import com.codename1.charts.renderers.SimpleSeriesRenderer;
import com.codename1.charts.util.ColorUtil;
import com.codename1.charts.views.PieChart;
import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.Form;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.mycompany.easyRide.entities.Reclamation;
import com.mycompany.easyRide.service.ReclamationService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ASUS
 */
public class ReclamationCharts {
    ArrayList<Reclamation> rec;
    public ReclamationCharts() {

    }
    
    public float calcul_nbr_reclamation(ArrayList<Reclamation> r,String ch){
        
         ArrayList<Reclamation> rec = new ArrayList<Reclamation>();
         rec =         ReclamationService.getInstance().getAllTasks();

        
    int f=0;
    for(int i=0;i<rec.size();i++){
        if (rec.get(i).getTypeReclamation().equals(ch)){ f++;}
    }
    return f;
}
    
    private DefaultRenderer buildCategoryRenderer(int[] colors) {
    DefaultRenderer renderer = new DefaultRenderer();
    renderer.setLabelsTextSize(70);
    renderer.setLegendTextSize(70);
    renderer.setMargins(new int[]{20, 30, 15, 0});
    for (int color : colors) {
        SimpleSeriesRenderer r = new SimpleSeriesRenderer();
        r.setColor(color);
        renderer.addSeriesRenderer(r);
    }
    return renderer;
}

/**
 * Builds a category series using the provided values.
 *
 * @param titles the series titles
 * @param values the values
 * @return the category series
 */
protected CategorySeries buildCategoryDataset(String title, double[] values) {
    CategorySeries series = new CategorySeries(title);
        series.add("Commande", this.calcul_nbr_reclamation(rec, "Commande") );
        series.add("Location", this.calcul_nbr_reclamation(rec, "Location") );
         series.add("Maintenance", this.calcul_nbr_reclamation(rec, "Maintenance") );
          series.add("Evenement", this.calcul_nbr_reclamation(rec, "Evenement") );
    return series;
}

public Form createPieChartForm() {
    
    
    
    
    // Generate the values
    double[] values = new double[]{this.calcul_nbr_reclamation(rec, "Commande")
            , this.calcul_nbr_reclamation(rec, "Location"),
            this.calcul_nbr_reclamation(rec, "Maintenance"),
            this.calcul_nbr_reclamation(rec, "Evenement")};
System.out.println("**************"+this.calcul_nbr_reclamation(rec, "Commande"));
    // Set up the renderer
    int[] colors = new int[]{ColorUtil.BLUE, ColorUtil.GREEN, ColorUtil.MAGENTA, ColorUtil.YELLOW};
    DefaultRenderer renderer = buildCategoryRenderer(colors);
    renderer.setZoomButtonsVisible(true);
    renderer.setZoomEnabled(true);
    renderer.setChartTitleTextSize(20);
    renderer.setDisplayValues(true);
    renderer.setShowLabels(true);
    SimpleSeriesRenderer r = renderer.getSeriesRendererAt(0);
    r.setGradientEnabled(true);
    r.setGradientStart(0, ColorUtil.BLUE);
    r.setGradientStop(0, ColorUtil.GREEN);
    r.setHighlighted(true);

    // Create the chart ... pass the values and renderer to the chart object.
    PieChart chart = new PieChart(buildCategoryDataset("EasyRide reclamation", values), renderer);

    // Wrap the chart in a Component so we can add it to a form
    ChartComponent c = new ChartComponent(chart);

    // Create a form and show it.
    Form f = new Form("Reclamation", new BorderLayout());
    f.add(BorderLayout.CENTER, c);
    f.getToolbar().addCommandToOverflowMenu("Retour", null, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent evt) {
              ReclamationIndex f2 = new ReclamationIndex();
              f2.show();
            }
        });
    return f;

}

    
}
