
import javax.swing.*;

//import javax.media.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.media.*;

	
class  Player extends JFrame implements ActionListener, ControllerListener
{
    JButton Open;//normal buttons
	JButton Play;
	JButton Fast_Forward;
	JButton Fast_Rewind;
	JButton Pause;
	JButton Stop;
	JSlider Slider;//normal slider
	JTextField tf;
	JPanel Panel_for_buttons=new JPanel();
	JPanel Panel_for_Slider=new JPanel();//panel will contain buttons and slider
	File f;
	javax.media.Player created_player=null;
	static float rate;
	//JButton Check;
	java.util.Timer timer;
    long Total_Time;
    long played_time;
    int slider_value;
	Container Content;//we need to get some container as we cannot directly add panel to JFrame
	int flag=0;
	//static int flag_for_display_rate=0;
	Time tt;
	int flag_for_display_rate=0;
	Time played_time_for_pause;
	double seconds=0;
	int player_started=0;
	long minutes_for_textfield;
	long seconds_for_textfield;
	static float forward_rate=1;
	
			public Player()
			{
				super("Prat's Player");
				Open=new JButton("○");
				Play=new JButton("►");
				Fast_Forward=new JButton("»");
				Fast_Rewind=new JButton("«");
				Pause=new JButton("ll");
				Stop=new JButton("■");
				//Check=new JButton("checkit");
				Slider=new JSlider(SwingConstants.HORIZONTAL, 0,100,0);
				tf=new JTextField(3);
				played_time_for_pause=new Time(0.0);
				
			}
			public void player_gui()
			{	
				
				Panel_for_buttons.setLayout(new FlowLayout());//how buttons arranged
				Panel_for_Slider.setLayout(new FlowLayout());
				Panel_for_Slider.add(Slider);
				Panel_for_buttons.add(Open);//adding buttons to panel
				Panel_for_buttons.add(Play);
				Panel_for_buttons.add(Fast_Forward);
				Panel_for_buttons.add(Fast_Rewind);
				Panel_for_buttons.add(Pause);
				Panel_for_buttons.add(Stop);
				//Panel_for_buttons.add(Check);
				Panel_for_buttons.add(tf);
				Content=this.getContentPane();//content pane is good option for our purpose. Others like glassapne
				//Content.add(Open);
				Content.add(Panel_for_buttons, BorderLayout.SOUTH);//Specifying layout here is necessary Appends the specified component to the end of this container.
				Content.add(Panel_for_Slider, BorderLayout.NORTH);//slider is above buttons
				//System.out.println("2");
				Open.addActionListener(this);
				Play.addActionListener(this);
				Stop.addActionListener(this);
				Fast_Forward.addActionListener(this);
				//Check.addActionListener(this);
				Pause.addActionListener(this);
				
			}
			public File open()
			{
				
				JFileChooser chooser= new JFileChooser();//JFileChooser provides a simple mechanism for the user to choose a file. For information about using JFileChooser, see How to Use File Choosers, a section in The Java Tutorial. 
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);//Sets the JFileChooser to allow the user to just select files, just select directories, or select both files and directories. The default is JFilesChooser.FILES_ONLY.
				chooser.showOpenDialog(this);//Pops up an "Open File" file chooser dialog. Note that the text that appears in the approve button is determined by the L&F.
				return chooser.getSelectedFile();//Returns the selected file. This can be set either by the programmer via setSelectedFile or by a user action, such as either typing the filename into the UI or selecting the file from a list in the UI.
				
			}
			public void displayrate()
			{
				
				long player_time=created_player.getMediaNanoseconds()/1000000000;
				minutes_for_textfield=player_time/60;
				seconds_for_textfield=player_time%60;
				tf.setText(String.valueOf(minutes_for_textfield)+":"+String.valueOf(seconds_for_textfield));
				Total_Time=(long)(created_player.getDuration().getSeconds());
					//System.out.println(Total_Time);
			    played_time=(long)(created_player.getMediaNanoseconds()/1000000000);
				slider_value=(int)((played_time*100)/Total_Time);
					//long total_time=created_player.getMediaTime();
				Slider.setValue(slider_value);
					
					
				//System.out.println("i am happy to be called");
				//played_time_string.valueOf((created_player.getMediaTime())/100000000);
				
			    //	tf.setText(played_time_string);
			
			}
			public void Reminder()
			{
				
					timer=new java.util.Timer();
					timer.scheduleAtFixedRate(new RemindTask(),1000,1);
					
				
			}
			class RemindTask extends java.util.TimerTask
			{
				public void run()
				{
					displayrate();
					timer.cancel();
				}
			}
			
			
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				//System.out.println("fn opening");
				// TODO Auto-generated method stub
				if(e.getSource()==Open)
				{
					try
					{
						f=open();
					}
					catch(Exception fx)
					{
						fx.printStackTrace();
					}
					
					  
				}
				if(e.getSource()==Play)
				{
					if(player_started==1)
					{
						//System.out.println("when trying to resume"+created_player.getState());
					created_player.setMediaTime(played_time_for_pause);
					}else
					{
							  try
							{
								
								created_player= Manager.createPlayer(f.toURI().toURL());
							//	System.out.println("just afetr manager.createplyaer"+created_player.getState());
								created_player.addControllerListener(this);
							//	System.out.println("just after addingcontrollerlistener"+created_player.getState());
								displayrate();
							//	System.out.println("just after displayratefn"+created_player.getState());
							}
							//created_player=Manager.createPlayer(f.toURI().toURL());
							catch(Exception ex)
							{
								System.err.println("Got exception "+ex);
							}
					}
					//System.out.println("just before calling start"+created_player.getState());
					created_player.start();
					//System.out.println("just after calling start"+created_player.getState());
					
					
					player_started=1;
					
					
					if(flag_for_display_rate==0)
					{
						Reminder();
						Reminder();
						flag_for_display_rate=1;
						
					}		
				}
				if(e.getSource()==Stop)
				{
				  // System.out.println("before calling stop"+created_player.getState());
				   created_player.stop();
				   //System.out.println("after calling stop and before deallocate"+created_player.getState());
		           created_player.deallocate();
		         //  System.out.println("after calling deallocate"+created_player.getState());
		           played_time_for_pause=new Time(0.0);
		           player_started=0;
		            
				}
				if(e.getSource()==Fast_Forward)
				{
					
				   forward_rate+=.5f;
				   created_player.setRate(forward_rate);
				}
			/*	if(e.getSource()==Check)
				{
					
					//System.out.println("YO YO");
					//System.out.println("YO YO"+created_player.getMediaTime());
					
					
				}*/
				if(e.getSource()==Pause)
				{
					played_time_for_pause=created_player.getMediaTime();
					created_player.stop();
					//created_player.deallocate();
					//System.out.println(played_time_for_pause);
				}
			}
			public synchronized void controllerUpdate(ControllerEvent event) 
			   {
				//System.out.println("beginning of controllerupdate"+created_player.getState());
				//System.out.println("I am in play");
				   if (event instanceof RealizeCompleteEvent) 
				   {
				        Component comp;
				        if ((comp = created_player.getVisualComponent()) != null)
				             //add ("North", comp);
							Content.add(comp, BorderLayout.CENTER, 1);
				        //if ((comp = player.getControlPanelComponent()) != null)
				          //   add ("South", comp);	     
				       // validate();  
						pack();   //resize window as per its components
						
				   }
				 //  created_player.setMediaTime(played_time_for_pause);
				   
			   } 
	
	
}
public class Player_Main{
	public static void main(String args[])
	{
	//	System.out.println("ok main is working");
		Player player_obj=new Player();
		player_obj.setVisible(true);
		player_obj.player_gui();
		player_obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//closes when we try to close
		player_obj.setBackground(Color.pink);
		player_obj.setLocation(300,300);//where should it appear
		player_obj.setSize(500, 100);
		//by default it's false hence hidden	
	}
}
