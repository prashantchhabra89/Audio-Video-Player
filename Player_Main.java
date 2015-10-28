
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.media.*;

class  Player extends JFrame implements ActionListener, ControllerListener
{
	//normal buttons
	JButton Open;
	JButton Play;
	JButton Fast_Forward;
	JButton Fast_Rewind;
	JButton Pause;
	JButton Stop;
	//normal slider
	JSlider Slider;
	JTextField tf;
	JPanel Panel_for_buttons=new JPanel();
	//panel will contain buttons and slider
	JPanel Panel_for_Slider=new JPanel();
	File f;
	javax.media.Player created_player=null;
	static float rate;
	//JButton Check;
	java.util.Timer timer;
	long Total_Time;
	long played_time;
	int slider_value;
	//we need to get some container as we cannot directly add panel to JFrame
	Container Content;
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
		Slider=new JSlider(SwingConstants.HORIZONTAL, 0,100,0);
		tf=new JTextField(3);
		played_time_for_pause=new Time(0.0);
	}

	public void player_gui()
	{	
		//how buttons arranged
		Panel_for_buttons.setLayout(new FlowLayout());
		Panel_for_Slider.setLayout(new FlowLayout());
		Panel_for_Slider.add(Slider);
		//adding buttons to panel
		Panel_for_buttons.add(Open);
		Panel_for_buttons.add(Play);
		Panel_for_buttons.add(Fast_Forward);
		Panel_for_buttons.add(Fast_Rewind);
		Panel_for_buttons.add(Pause);
		Panel_for_buttons.add(Stop);
		//Panel_for_buttons.add(Check);
		Panel_for_buttons.add(tf);
		//content pane is good option for our purpose. Others like glasspane
		Content=this.getContentPane();
		//Specifying layout here is necessary Appends the specified component to the end of this container.
		Content.add(Panel_for_buttons, BorderLayout.SOUTH);
		//slider is above buttons
		Content.add(Panel_for_Slider, BorderLayout.NORTH);
		Open.addActionListener(this);
		Play.addActionListener(this);
		Stop.addActionListener(this);
		Fast_Forward.addActionListener(this);
		Pause.addActionListener(this);

	}

	public File open()
	{
		//JFileChooser provides a simple mechanism for the user to choose a file. For information about using JFileChooser, see How to Use File Choosers, a section in The Java Tutorial. 
		JFileChooser chooser= new JFileChooser();
		//Sets the JFileChooser to allow the user to just select files, just select directories, or select both files and directories. The default is JFilesChooser.FILES_ONLY.
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//Pops up an "Open File" file chooser dialog. Note that the text that appears in the approve button is determined by the L&F.
		chooser.showOpenDialog(this);
		//Returns the selected file. This can be set either by the programmer via setSelectedFile or by a user action, such as either typing the filename into the UI or selecting the file from a list in the UI.
		return chooser.getSelectedFile();
	}

	public void displayrate()
	{
		long player_time=created_player.getMediaNanoseconds()/1000000000;
		minutes_for_textfield=player_time/60;
		seconds_for_textfield=player_time%60;
		tf.setText(String.valueOf(minutes_for_textfield)+":"+String.valueOf(seconds_for_textfield));
		Total_Time=(long)(created_player.getDuration().getSeconds());
		played_time=(long)(created_player.getMediaNanoseconds()/1000000000);
		slider_value=(int)((played_time*100)/Total_Time);
		Slider.setValue(slider_value);
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
				created_player.setMediaTime(played_time_for_pause);
			}else
			{
				try
				{
					created_player= Manager.createPlayer(f.toURI().toURL());
					created_player.addControllerListener(this);
					displayrate();
				}
				catch(Exception ex)
				{
					System.err.println("Got exception "+ex);
				}
			}
			created_player.start();
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
			created_player.stop();
			created_player.deallocate();
			played_time_for_pause=new Time(0.0);
			player_started=0;
		}
		if(e.getSource()==Fast_Forward)
		{
			forward_rate+=.5f;
			created_player.setRate(forward_rate);
		}
		if(e.getSource()==Pause)
		{
			played_time_for_pause=created_player.getMediaTime();
			created_player.stop();
		}
	}
	public synchronized void controllerUpdate(ControllerEvent event) 
	{
		if (event instanceof RealizeCompleteEvent) 
		{
			Component comp;
			if ((comp = created_player.getVisualComponent()) != null)
				Content.add(comp, BorderLayout.CENTER, 1);
			//resize window as per its components
			pack();   
		}
	} 


}
public class Player_Main{
	public static void main(String args[])
	{
		Player player_obj=new Player();
		player_obj.setVisible(true);
		player_obj.player_gui();
		//closes when we try to close
		player_obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		player_obj.setBackground(Color.pink);
		//where should it appear
		player_obj.setLocation(300,300);
		player_obj.setSize(500, 100);
		//by default it's false hence hidden	
	}
}
