package com.heligame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;



import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements Runnable, KeyListener, ActionListener {
	private static final long serialVersionUID = 1L;
	JFrame frame;
	boolean dbg= true;
	long delta = 0;
	long last = 0;
	long fps = 0;
	
	long obs = 0;
	long rmvdobs =0;
	long helis =0;
	long clds=0;
	long rocks =0;
	long score=0;


	Heli copter;
	ArrayList<Sprite> actors;
	ArrayList<Sprite> painter;

	boolean up;
	boolean down;
	boolean left;
	boolean right;
	boolean started;
	int speed = 50;
	
	Timer timer;
	BufferedImage[] rocket;
	BufferedImage background;
	
	public static void main(String[] args) {
		new GamePanel(800, 600);
	}
	public GamePanel(int w, int h) {
		this.setPreferredSize(new Dimension(w, h));
		this.setBackground(Color.blue);
		frame = new JFrame("Name of the Game");
		frame.setLocation(100, 100);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.addKeyListener(this);
		frame.pack();
		frame.setVisible(true);

		Thread th = new Thread(this);
		th.start();
	}
	private void doInits() {
		last = System.nanoTime();
		BufferedImage[] heli = loadPics("com/heligame/pics/heli.gif", 4);
		rocket = loadPics("com/heligame/pics/rocket.gif", 8);
		background = loadPics("com/heligame/pics/background.jpg",1)[0];
		actors = new ArrayList<Sprite>();
		painter = new ArrayList<Sprite>();
		copter = new Heli(heli, 400, 300, 100, this);
		createClouds();
		actors.add(copter);
		helis++;
		
		
		
		timer = new Timer(3000,this);
		timer.start();
		
		started = true;
	}
	private void createClouds() {
		BufferedImage[] bi = loadPics("com/heligame/pics/cloud.gif", 1);
		
		for(int y=10; y<getHeight(); y+=50){
			int x = (int)(Math.random()*getWidth());
			Cloud cloud = new Cloud(bi, x, y, 1000, this);
			ListIterator<Sprite> itcC = actors.listIterator();
			itcC.add(cloud);
			clds++;
		}
	}
	private void createRocket(){
		int x = 0;
		int y = (int)(Math.random()*getHeight());
		int hori = (int)(Math.random()*2);
		
		if(hori == 0){
			x = -30;
		} else {
			x = getWidth()+30;
		}
		
		Rocket rock = new Rocket(rocket,x,y,100,this);
		if(x<0){
			rock.setHorizontalSpeed(100);
		} else {
			rock.setHorizontalSpeed(-100);
		}
		
		ListIterator<Sprite> itcR = actors.listIterator();
		itcR.add(rock);
	}	
	
	@Override
	public void run() {
		while (frame.isVisible()) {
			computeDelta();
			
			if(isStarted()){
				checkKeys();
				doLogic();
				moveObjects();
				cloneArrays();
			}
			repaint();
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {}	
		}
	}
	@SuppressWarnings(value = {"unchecked"})
	private void cloneArrays() {
		painter = (ArrayList<Sprite>) actors.clone();
	}
	
	public boolean isStarted() {
		return started;		
	}
	public void setStarted(boolean started){
		this.started = started;
	}
	private void computeDelta() {
		delta = System.nanoTime() - last;
		last = System.nanoTime();
		fps = ((long) 1e9) / delta;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(background, 0, 0, this);
		
		g.setColor(Color.MAGENTA);
		g.drawString("Score: "	+ Long.toString(score), 20, 10);
		if(dbg){
			g.setColor(Color.green);
			g.drawString("FPS: " 	+ Long.toString(fps), 20, 35);
			g.drawString("onScreen Objects: "+ Long.toString(obs),20,50);
			g.drawString("onScreen Helicopter:"+ Long.toString(helis),20,65);
			g.drawString("onScreen Clouds: "+ Long.toString(clds),20,80);
			g.drawString("onScreen Rockets: "+ Long.toString(rocks),20,95);
			g.drawString("Removed Rockets: "+ Long.toString(rmvdobs),20,110);
		} 
		if (!started) {
			return;
		}

		if (painter != null) {
			for (ListIterator<Sprite> itpC = painter.listIterator(); itpC.hasNext();) {
				Sprite r = itpC.next();
				r.drawObjects(g);
			}
		}
	}
	private void moveObjects() {
		for (ListIterator<Sprite> itmO = actors.listIterator(); itmO.hasNext();) {
			Sprite r = itmO.next();
			r.move(delta);
		}
	}
	private void doLogic() {
		for (ListIterator<Sprite> itdL = actors.listIterator(); itdL.hasNext();) {
			Sprite r = itdL.next();
			r.doLogic(delta);
			
			if(r.remove){
				itdL.remove();
				rmvdobs++;
				rocks--;
				score +=50;
			}
		}
		obs = actors.size();
	}

	private void checkKeys() {
		if (up) {
			copter.setVerticalSpeed(-speed);
		}
		if (down) {
			copter.setVerticalSpeed(speed);
		}
		if (right) {
			copter.setHorizontalSpeed(speed);
		}
		if (left) {
			copter.setHorizontalSpeed(-speed);
		}
		if (!up && !down) {
			copter.setVerticalSpeed(0);
		}
		if (!left && !right) {
			copter.setHorizontalSpeed(0);
		}
	}
	private BufferedImage[] loadPics(String path, int pics) {
		BufferedImage[] anim = new BufferedImage[pics];
		BufferedImage source = null;

		URL pic_url = getClass().getClassLoader().getResource(path);

		try {
			source = ImageIO.read(pic_url);
		} catch (IOException e) {
		}

		for (int x = 0; x < pics; x++) {
			anim[x] = source.getSubimage(x * source.getWidth() / pics, 0,
					source.getWidth() / pics, source.getHeight());
		}
		return anim;
	}
	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			up = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			down = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			left = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			right = true;
		}
	}
	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			up = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			down = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			left = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			right = false;
		}

		if (e.getKeyCode() == KeyEvent.VK_ENTER){
			if(!isStarted()){
				doInits();					
				setStarted(true);
			}
		}
		
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
			if(isStarted()){
				setStarted(false);
				timer.stop();
			}else{
				frame.dispose();
			}
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}
	@Override
	public void actionPerformed(ActionEvent e) {		
		if(isStarted() && e.getSource().equals(timer)){
			createRocket();
			rocks++;
		}
	}
}