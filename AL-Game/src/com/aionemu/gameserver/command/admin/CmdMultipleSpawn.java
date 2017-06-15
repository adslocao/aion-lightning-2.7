package com.aionemu.gameserver.command.admin;

import java.util.ArrayList;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.model.gameobjects.VisibleObject;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.services.HTMLService;
import com.aionemu.gameserver.spawnengine.SpawnEngine;
import com.aionemu.gameserver.utils.PacketSendUtility;

public class CmdMultipleSpawn extends BaseCommand{
	/*
	 * linex/y  id npcRadius MaxRang
	 * hcyl id npcRadius MaxRang
	 * cyl  id npcRadius MaxRang
	 * star id npcRadius MaxRang nbPointed 
	 * square  id npcRadius MaxRang
	 * rec id npcRadius Height Length
	 *
	 *
	 * exemple:
	 * - cyl
	 * -- //ms cyl 700454 2 30
	 * --- http://gyazo.com/bb37563de27c13d2a2559af5a1c20402
	 * 
	 * - Star
	 * -- //ms star 700454 4 10 50
	 * --- http://gyazo.com/71666250ddb98de286b2804da4ab9d5a
	 * 
	 * - Square
	 * -- //ms square 700454 4 20
	 * --- http://gyazo.com/6eb0a3b6a3928646cd0e04584c7cf786
	 * 
	 *
	 */
	
	static class despawn{
		public despawn(ArrayList<VisibleObject> npcs){
			if(npcs != null)
				for(VisibleObject npc : npcs)
					if(npc != null)
						npc.getController().onDelete();
		}
	}
	
	private final static String help = "============ MultipleSpawn ============= <br>"
									  +"- linex/y id npcRadius MaxRang          <br>"
									  +"- hcyl    id npcRadius MaxRang          <br>"
									  +"- cyl     id npcRadius MaxRang          <br>"
									  +"- star    id npcRadius nbPointed MaxRang<br>"
									  +"- square  id npcRadius MaxRang          <br>"
									  +"- rec     id npcRadius Height Length    <br>"
									  +"========================================"
									  +" cmd -t despawnTime(sec) "
									  +"========================================";
			
	public void execute(Player admin, String... params) {
		if (params.length < 4) {
			HTMLService.showHTML(admin, help);
		}
		if(Integer.parseInt(params[2]) <= 0){
			PacketSendUtility.sendMessage(admin, "incorect npcRadius");
			return;
		}
		if(Integer.parseInt(params[3]) <= 0 || Integer.parseInt(params[3]) > 100){
			PacketSendUtility.sendMessage(admin, "incorect MaxRang");
			return;
		}
		ArrayList<VisibleObject> npcs = null;
		
		if(params[0].equalsIgnoreCase("hcyl"))
			npcs = hcyl(admin, Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]));
		else if(params[0].equalsIgnoreCase("cyl"))
			npcs = cyl(admin, Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]));
		else if(params[0].equalsIgnoreCase("star"))
			npcs = star(admin, Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]), Integer.parseInt(params[4]));
		else if(params[0].equalsIgnoreCase("square"))
			npcs = square(admin, Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]));
		else if(params[0].equalsIgnoreCase("rec"))
			npcs = rec(admin, Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]), Integer.parseInt(params[4]));
		else if(params[0].equalsIgnoreCase("linex"))
			npcs = xLine(admin, Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]));
		else if(params[0].equalsIgnoreCase("liney"))
			npcs = yLine(admin, Integer.parseInt(params[1]), Integer.parseInt(params[2]), Integer.parseInt(params[3]));
		
		for(int i = 0 ; i < params.length ; i++)
			if(params[i] == "-t"){
				toDespawn(npcs, Integer.parseInt(params[i+1])*1000);
				break;
			}
		PacketSendUtility.sendMessage(admin, "Spawn done");
	}
	
	public ArrayList<VisibleObject> rec(Player admin, int npcId , int npcRadius, int Height, int Length){
		ArrayList<VisibleObject> npcs = new ArrayList<VisibleObject>();
		for(int xOff = -Height/2 ;  xOff <= Height/2 ; xOff = xOff+npcRadius )
			for(int yOff = -Length/2 ;  yOff <= Length/2 ; yOff = yOff+npcRadius )
				npcs.add(SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(admin.getWorldId(), npcId, admin.getX()+xOff,  admin.getY()+yOff, admin.getZ(), admin.getHeading()), admin.getInstanceId()));
		return npcs;
	}
	
	public ArrayList<VisibleObject> yLine(Player admin, int npcId , int npcRadius, int MaxRang){
		ArrayList<VisibleObject> npcs = new ArrayList<VisibleObject>();
		for(int yOff = 0 ;  yOff <= MaxRang ; yOff = yOff+npcRadius )
				npcs.add(SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(admin.getWorldId(), npcId, admin.getX(),  admin.getY()+yOff, admin.getZ(), admin.getHeading()), admin.getInstanceId()));
		return npcs;
	}
	public ArrayList<VisibleObject> xLine(Player admin, int npcId , int npcRadius, int MaxRang){
		ArrayList<VisibleObject> npcs = new ArrayList<VisibleObject>();
		for(int xOff = 0 ;  xOff <= MaxRang ; xOff = xOff+npcRadius )
			npcs.add(SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(admin.getWorldId(), npcId, admin.getX()+xOff,  admin.getY(), admin.getZ(), admin.getHeading()), admin.getInstanceId()));
		return npcs;
	}
	public ArrayList<VisibleObject> square(Player admin, int npcId , int npcRadius, int MaxRang){
		ArrayList<VisibleObject> npcs = new ArrayList<VisibleObject>();
		for(int xOff = -MaxRang ;  xOff <= MaxRang ; xOff = xOff+npcRadius )
			for(int yOff = -MaxRang ;  yOff <= MaxRang ; yOff = yOff+npcRadius )
				npcs.add(SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(admin.getWorldId(), npcId, admin.getX()+xOff,  admin.getY()+yOff, admin.getZ(), admin.getHeading()), admin.getInstanceId()));
		return npcs;
	}
	
	
	public ArrayList<VisibleObject> cyl(Player admin, int npcId , int npcRadius, int MaxRang){
		ArrayList<VisibleObject> npcs = new ArrayList<VisibleObject>();
		int rang = 0;
		int nbSp = 1;
		do{
			npcs.addAll(hcylBis(admin, npcId,nbSp,rang));
			rang += 2* npcRadius;//inc by diameters
			nbSp = nbSpawnInCyrcleof(rang, npcRadius);
		}
		while(rang <= MaxRang);
		return npcs;
	}
	
	
	public ArrayList<VisibleObject> star(Player admin, int npcId , int radius, int MaxRang , int nbPointed){
		ArrayList<VisibleObject> npcs = new ArrayList<VisibleObject>();
		for(int rang = 0 ; rang <= MaxRang ; rang = rang + radius)
			npcs.addAll(hcylBis(admin, npcId,nbPointed,rang));
		return npcs;
	}
	
	public ArrayList<VisibleObject> hcyl(Player admin, int npcId, int npcRadius, int MaxRang){
		ArrayList<VisibleObject> npcs = new ArrayList<VisibleObject>();
		int nbSp = nbSpawnInCyrcleof(MaxRang , npcRadius);
		final double pi = 3.14;
		double baseAngle = (2*pi) / nbSp;
		
		for(double i = 0 ; i < 2*pi ; i = i + baseAngle)
			npcs.add(SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(admin.getWorldId(), npcId, (float) (admin.getX()+Math.cos(i)*MaxRang), (float) (admin.getY()+Math.sin(i)*MaxRang), admin.getZ(), admin.getHeading()), admin.getInstanceId()));
		return npcs;
	}
	
	public ArrayList<VisibleObject> hcylBis(Player admin, int npcId,int nb,int MaxRang){
		ArrayList<VisibleObject> npcs = new ArrayList<VisibleObject>();
		final double pi = 3.14;
		double baseAngle = (2*pi) / nb;
		
		for(double i = 0 ; i < 2*pi ; i = i + baseAngle)
			npcs.add(SpawnEngine.spawnObject(SpawnEngine.addNewSingleTimeSpawn(admin.getWorldId(), npcId, (float) (admin.getX()+Math.cos(i)*MaxRang), (float) (admin.getY()+Math.sin(i)*MaxRang), admin.getZ(), admin.getHeading()), admin.getInstanceId()));
		return npcs;
	}
	
	
	public int nbSpawnInCyrcleof(int cycleSize , int npcRadius){
		return (int) (2*3.14*cycleSize)/npcRadius;
	}
	
	
	
	
	
	private void toDespawn(final ArrayList<VisibleObject> npcs, int delay) {
		if(npcs == null)
			return;
		ThreadPoolManager.getInstance().schedule(new Runnable() {

			@Override
			public void run() {
					for(VisibleObject npc : npcs)
						if(npc != null)
							npc.getController().onDelete();
			}
		}, delay);
	}
}
