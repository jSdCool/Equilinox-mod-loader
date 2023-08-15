package com.modloader;

public final class AsyncLoopExec extends Thread{
	private static boolean exsists =false;
	AsyncLoopExec(){
		if(!exsists) {
			exsists = true;
			return;
		}else {
			throw new RuntimeException("attmpted to creat too many instanced of "+this.getClass());
		}
	}
	public void run() {
		if(Main.asyncLoopingObjects.size()==0) {
			System.out.println("no async loops found. terminated async loop thread");
			return;
		}
		while(Main.gameRunning) {
			for(int i=0;i<Main.asyncLoopingObjects.size();i++) {
				try {
					Main.asyncLoopingObjects.get(i).asyncLoop();
				} catch (Exception e) {
					System.err.println("An async loop ran into an error: ");
					e.printStackTrace();
				}
			}
		}
	}
}
