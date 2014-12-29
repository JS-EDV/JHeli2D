package com.heligame;

import java.awt.image.BufferedImage;

public class Cloud extends Sprite {
	private static final long serialVersionUID = 1L;
	final int SPEED = 50;
	
	
	public Cloud(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		
		if((int)(Math.random()*2)<1){
			setHorizontalSpeed(-SPEED);
		} else {
			setHorizontalSpeed(SPEED);
		}		
	}
	
	@Override
	public void doLogic(long delta){
		super.doLogic(delta);
		
		if(getHorizontalSpeed()>0 && getX()>parent.getWidth()){
			x = -getWidth();
		}
		if(getHorizontalSpeed()<0 && getX()+getWidth()<0){
			x = parent.getWidth()+getWidth();
		}
	}

	@Override
	public boolean colliededWith(Sprite s) {
		return false;
	}
}
