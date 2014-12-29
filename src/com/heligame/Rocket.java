package com.heligame;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class Rocket extends Sprite{
	private static final long serialVersionUID = 1L;
	
	int verticalspeed = 70;
	Rectangle2D.Double target;
	boolean locked = false;
		
	public Rocket(BufferedImage[] i, double x, double y, long delay, GamePanel p) {
		super(i, x, y, delay, p);
		
		if(getY()<parent.getHeight()/2){
			setVerticalSpeed(verticalspeed);
		}else{
			setVerticalSpeed(-verticalspeed);
		}
	}
	
	@Override
	public void doLogic(long delta){
		super.doLogic(delta);
		
		if(getHorizontalSpeed()>0){
			target = new Rectangle2D.Double(getX()+getWidth(),getY(),
							parent.getWidth()-getX(),getHeight());
		} else {
			target = new Rectangle2D.Double(0,getY(),getX(),getHeight());
		}
		if(!locked && parent.copter.intersects(target)){
			setVerticalSpeed(0);
			locked = true;
		}
		if(locked){
			if(getY()<parent.copter.getY()){
				setVerticalSpeed(40);
			}
			if(getY()>parent.copter.getY()+parent.copter.getHeight()){
				setVerticalSpeed(-40);
			}
		}
		
		if(getHorizontalSpeed()>0 && getX()>parent.getWidth()){
			remove = true;
		}
		if(getHorizontalSpeed()<0 && getX()+getWidth()<0){
			remove = true;
		}
	}
	@Override
	public void setHorizontalSpeed(double d){
		super.setHorizontalSpeed(d);
		
		if(getHorizontalSpeed()>0){
			setLoop(4, 7);
		} else {
			setLoop(0, 3);
		}
		
	}

	@Override
	public boolean colliededWith(Sprite s) {
		
		if(remove){
			return false;
		}
		
		if(this.intersects(s)){
			if(s instanceof Heli){
				parent.createExplosion((int)getX(),(int)getY());
				parent.createExplosion((int)s.getX(),(int)s.getY());
				
				remove = true;
				s.remove = true;
				System.out.println("Rums!!!");
				return true;
			}
			if(s instanceof Rocket){
				parent.createExplosion((int)getX(),(int)getY());
				parent.createExplosion((int)s.getX(),(int)s.getY());
				remove = true;
				s.remove = true;
				System.out.println("Rocket HIT!");
				return true;
			}
		}
		return false;
	}
}