package com.hhdb.csadmin.plugin.monitor;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.hhdb.csadmin.plugin.monitor.series.MemSeries;

/**
 * 内存监控图
 *
 */
public class MemMinitor extends JScrollPane {
	private static final long serialVersionUID = 1L;
	private MemSeries memseris;
	private ScheduledExecutorService executor =null;
	private JComboBox<String> sj=null;
	private int reflashTime=5;
	
	public MemMinitor(DBMonitor dbMonitorp){
		sj=new JComboBox<String>();
		sj.addItem("5");
		sj.addItem("10");
		sj.addItem("30");
		
		JPanel picHeader=new JPanel();
		JPanel pic=new JPanel();
		JPanel mem=new JPanel();
	
		
		picHeader.add(new JLabel("刷新频率:"));
		picHeader.add(sj);
		picHeader.add(new JLabel(" 秒"));
		picHeader.setBackground(Color.white);
		
		pic.setBackground(Color.white);
	    pic.add(picHeader);
	    
	    memseris=new MemSeries(reflashTime,dbMonitorp);
		pic.setPreferredSize(new Dimension(memseris.getPreferredSize().width, memseris.getPreferredSize().height+50));
	    pic.add(memseris);
	    
		mem.add(pic);
	    mem.setBackground(Color.white);
	    this.setBorder(null);
	    getViewport().add(mem);
	    
	    executor = Executors.newScheduledThreadPool(1); 
	    executor.scheduleAtFixedRate(memseris, reflashTime*1000,reflashTime*1000,TimeUnit.MILLISECONDS); 
	    
	    sj.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				switch (e.getStateChange())
                {
                	case ItemEvent.SELECTED: 
                		runGenerateChart();
	                break;
                }
			}
		});
	}
	private void runGenerateChart(){
		if(executor!=null&&!executor.isShutdown()){
			executor.shutdown();
		}
		reflashTime=Integer.parseInt(sj.getSelectedItem().toString());
	    executor = Executors.newScheduledThreadPool(1); 
	    memseris.setTime(reflashTime);
	    executor.scheduleAtFixedRate(memseris, reflashTime*1000,reflashTime*1000,TimeUnit.MILLISECONDS); 
	}
	public void closeRunable(){
		executor.shutdown();
		memseris.closeConnection();
	}
}
