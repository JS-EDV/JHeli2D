package com.heligame;

import java.awt.image.BufferedImage;

public class Heli extends Sprite {
	private static final long serialVersionUID = 1L;
	
	public Heli(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
	}

	@Override
	public void doLogic(long delta) {
		super.doLogic(delta);
		
		if(getX()<0){
			setHorizontalSpeed(0);
			setX(0);
		}
		if(getX()+getWidth()>parent.getWidth()){
			setX(parent.getWidth()-getWidth());
			setHorizontalSpeed(0);
		}
		if(getY()<0){
			setVerticalSpeed(0);
			setY(0);
		}
		if(getY()+getHeight()>parent.getHeight()){
			setY(parent.getHeight()-getHeight());
			setVerticalSpeed(0);
		}
	}

	public void setX(double x){
		this.x =x;
	}
	public void setY(double y){
		this.y=y;
	}

	@Override
	public boolean colliededWith(Sprite s) {
		
		if(remove){
			return false;
		}
		
		if(this.intersects(s)){

			if(s instanceof Rocket){
				parent.createExplosion((int)getX(),(int)getY());
				parent.createExplosion((int)s.getX(),(int)s.getY());
				remove = true;
				s.remove = true;
				System.out.println("Aua!");
				return true;
			}
		}
		return false;
	}
}