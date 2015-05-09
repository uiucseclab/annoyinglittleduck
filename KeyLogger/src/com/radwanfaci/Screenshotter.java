package com.radwanfaci;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseListener;

public class Screenshotter implements NativeMouseListener{
	
	int count = 0;
	int frequency = 5;
	int clickCount;
	private static ArrayList<BufferedImage> screenshots = new ArrayList<BufferedImage>();
	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
		if(clickCount++%frequency != 0)
		{
			System.out.println("Not taking screenshot...");
			return;
		}
		System.out.println("Taking screenshot...");
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        

        BufferedImage blackSquare = new BufferedImage(50, 50, BufferedImage.TYPE_3BYTE_BGR);
        for(int i = 0; i < blackSquare.getHeight(); i++){
            for(int j = 0; j < blackSquare.getWidth(); j++){
                blackSquare.setRGB(j, i, 128);
            }
        }

        try {
            Robot robot = new Robot();
            BufferedImage screenshot = robot.createScreenCapture(new Rectangle(0,0,width,height));
            PointerInfo pointer = MouseInfo.getPointerInfo();
            int x = (int) pointer.getLocation().getX();
            int y = (int) pointer.getLocation().getY();

            screenshot.getGraphics().drawImage(blackSquare, x, y, null);
            screenshots.add(screenshot);
            ImageIO.write(screenshot, "PNG", new File(NativeEventListeners.desktopPath + File.separatorChar + "screenshot_" + count+ ".PNG"));
            count++;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
		System.out.println("Screenshot saved!");
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public static ArrayList<BufferedImage> getScreenShots()
	{
		return screenshots;
	}

}
