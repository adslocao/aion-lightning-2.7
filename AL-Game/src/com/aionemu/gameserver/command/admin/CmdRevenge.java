package com.aionemu.gameserver.command.admin;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.command.BaseCommand;
import com.aionemu.gameserver.global.additions.MessagerAddition;
import com.aionemu.gameserver.model.gameobjects.Creature;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.model.gameobjects.player.RequestResponseHandler;
import com.aionemu.gameserver.network.aion.serverpackets.SM_QUESTION_WINDOW;
import com.aionemu.gameserver.services.player.PlayerReviveService;
import com.aionemu.gameserver.services.teleport.TeleportService;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.utils.Util;
import com.aionemu.gameserver.world.World;

/*syntax //revenge <PlayerName> */
public class CmdRevenge extends BaseCommand {
	

        public int adminscore;
        public int playerscore;
        public boolean admIsWin1;
        public boolean playerIsWin1;
        public boolean admIsWin2;
        public boolean playerIsWin2;
        public boolean isDraw1;
        public boolean isDraw2;
	
	public void execute(final Player admin, String... params) {
		if (params == null || params.length < 1) {
			showHelp(admin);
			return;
		}

		final Player player = World.getInstance().findPlayer(Util.convertName(params[0]));
		if (player == null) {
			PacketSendUtility.sendMessage(admin, "The specified player is not online.");
			return;
		}

		if (player == admin) {
			PacketSendUtility.sendMessage(admin, "Cannot use this command on yourself.");
			return;
		}
                String message = "Игрок ["+admin.getName()+"] Желает сразиться с вами,Принять Приглашение?";
	        RequestResponseHandler responseHandler = new RequestResponseHandler(player){

	            public void acceptRequest(Creature requester, Player responder)
	            {
	                start(player, admin);
	                return;
	            }

	            public void denyRequest(Creature requester, Player responder){ return; }
	        };
	        boolean requested = player.getResponseRequester().putRequest(902247, responseHandler);
	        if(requested){PacketSendUtility.sendPacket(player, new SM_QUESTION_WINDOW(902247, 0, message));return;}
	}
        

	public void onFail(Player admin, String message) {
		showHelp(admin);
		return;
	}
        
        private void start(final Player admin, final Player player)
        {
            TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
            TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
            MessagerAddition.announce(player, "[Revenge]: У вас есть 15 секунд на подготовку к битве!\nЧерез 15 секунд вы будете телепортированы к врагу!");
            ThreadPoolManager.getInstance().schedule(new Runnable() {

		@Override
		public void run() {
	          TeleportService.teleportTo(admin, player.getWorldId(), player.getX(), player.getY(), player.getZ(), 3000, true);
                  MessagerAddition.announce(player, "[Revenge]Битва Началась!");
                  if(player.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(player, 100, 100, false);
                      PlayerReviveService.revive(admin, 100, 100, false);
                      adminscore += 1;
                      MessagerAddition.announce(player, "[Revenge]Вы Проиграли В битве! Игрок" + admin.getName() + "Получает одно Очко");
                      MessagerAddition.announce(admin, "[Revenge]Поздравляю!Вы Выйграли,вы получаете Одно очко в свой счет");
                      admIsWin1 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  else if(player.getLifeStats().isAlreadyDead() & admin.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      PlayerReviveService.revive(player, 100, 100, false);
                      MessagerAddition.announce(player, "[Revenge]Ни один не выйграл,никто не получает дополнительное очко");
                      MessagerAddition.announce(admin, "[Revenge]Ни один не выйграл,никто не получает дополнительное очко");
                      isDraw1 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                 else if(admin.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      playerscore += 1;
                      MessagerAddition.announce(player, "[Revenge]Поздравляю Вы Выйграли!Вы получаете одно дополнительное очко!");
                      MessagerAddition.announce(admin, "[Revenge]Вы Проиграли,ваш противник получает дополнительное очко!");
                      playerIsWin1 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(player.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Игрок" + player.getName() + "Ушел с поля битвы!\nВыигрывает "+ admin.getName(), 0);
                  }
                  if(admin.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Игрок" + admin.getName() + "Ушел с поля битвы!\nВыигрывает "+ player.getName(), 0);
                  }				  
                  else if (admIsWin1 || playerIsWin1 || isDraw1)
                   {
                      stage2(player, admin);
                   }
                
		}

	}, 18000);  
            
        }
        private void stage2(final Player admin, final Player player)
        {
            TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
            TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
            MessagerAddition.announce(player, "[Revenge]: У вас есть 15 секунд на подготовку к битве!\nЧерез 15 секунд вы будете телепортированы к врагу!");
            ThreadPoolManager.getInstance().schedule(new Runnable() {

		@Override
		public void run() {
	          TeleportService.teleportTo(admin, player.getWorldId(), player.getX(), player.getY(), player.getZ(), 3000, true);
                  MessagerAddition.announce(player, "[Revenge]Битва Началась!Раунд 2");
                  if(player.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(player, 100, 100, false);	  
                      adminscore += 1;
                      MessagerAddition.announce(player, "[Revenge]Вы Проиграли В битве! Игрок" + admin.getName() + "Получает одно Очко");
                      MessagerAddition.announce(admin, "[Revenge]Поздравляю!Вы Выйграли,вы получаете Одно очко в свой счет");
                      admIsWin2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(player.getLifeStats().isAlreadyDead() & admin.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      PlayerReviveService.revive(player, 100, 100, false);
                      MessagerAddition.announce(player, "[Revenge]Ни один не выйграл,никто не получает дополнительное очко");
                      MessagerAddition.announce(admin, "[Revenge]Ни один не выйграл,никто не получает дополнительное очко");
                      isDraw2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(admin.getLifeStats().getCurrentHp() == 0)
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      playerscore += 1;
                      MessagerAddition.announce(player, "[Revenge]Поздравляю Вы Выйграли!Вы получаете одно дополнительное очко!");
                      MessagerAddition.announce(admin, "[Revenge]Вы Проиграли,ваш противник получает дополнительное очко!");
                      playerIsWin2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
				  if(admIsWin2 || playerIsWin2 || isDraw2)
                  {
                     stage3(player, admin);
                  }
                  if(player.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Игрок" + player.getName() + "Ушел с поля битвы!\nВыигрывает "+ admin.getName(), 0);
                  }
                  if(admin.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Игрок" + admin.getName() + "Ушел с поля битвы!\nВыигрывает "+ player.getName(), 0);
                  }
                  
                   
		}

	}, 18000);  
            
        }
       private void stage3(final Player admin, final Player player)
        {
            TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
            TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
            MessagerAddition.announce(player, "[Revenge]: У вас есть 15 секунд на подготовку к битве!\nЧерез 15 секунд вы будете телепортированы к врагу!");
            ThreadPoolManager.getInstance().schedule(new Runnable() {

		@Override
		public void run() {
	          TeleportService.teleportTo(admin, player.getWorldId(), player.getX(), player.getY(), player.getZ(), 3000, true);
                  MessagerAddition.announce(player, "[Revenge]Битва Началась!Раунд 2");
                  if(player.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(player, 100, 100, false);	  
                      adminscore += 1;
                      MessagerAddition.announce(player, "[Revenge]Вы Проиграли В битве! Игрок" + admin.getName() + "Получает одно Очко");
                      MessagerAddition.announce(admin, "[Revenge]Поздравляю!Вы Выйграли,вы получаете Одно очко в свой счет");
                      admIsWin2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(player.getLifeStats().isAlreadyDead() & admin.getLifeStats().isAlreadyDead())
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      PlayerReviveService.revive(player, 100, 100, false);
                      MessagerAddition.announce(player, "[Revenge]Ни один не выйграл,никто не получает дополнительное очко");
                      MessagerAddition.announce(admin, "[Revenge]Ни один не выйграл,никто не получает дополнительное очко");
                      isDraw2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
                  if(admin.getLifeStats().getCurrentHp() == 0)
                  {
                      PlayerReviveService.revive(admin, 100, 100, false);
                      playerscore += 1;
                      MessagerAddition.announce(player, "[Revenge]Поздравляю Вы Выйграли!Вы получаете одно дополнительное очко!");
                      MessagerAddition.announce(admin, "[Revenge]Вы Проиграли,ваш противник получает дополнительное очко!");
                      playerIsWin2 = true;
                      restore(player);
                      restore(admin);
                      TeleportService.teleportTo(player, 510010000, 256, 256, 49, 3000, true);
                      TeleportService.teleportTo(admin, 520010000, 256, 256, 49, 3000, true);
                  }
		 if(admIsWin2 || playerIsWin2 || isDraw2)
                  {
                     onEnd(player, admin);
                  }
                  if(player.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Игрок" + player.getName() + "Ушел с поля битвы!\nВыигрывает "+ admin.getName(), 0);
                  }
                  if(admin.getWorldId() != 510010000)
                  {
                      MessagerAddition.announceAll("[Revenge]: Игрок" + admin.getName() + "Ушел с поля битвы!\nВыигрывает "+ player.getName(), 0);
                  }
                  
                   
		}

	}, 19000);  
            
        }
        private void onEnd(final Player player, final Player admin)
        {
            MessagerAddition.announceAll("[Revenge]:И так результаты битвы " + admin.getName() + "vs" +player.getName(), 0);
             ThreadPoolManager.getInstance().schedule(new Runnable() {
             
	@Override
	public void run() {
              
		if(adminscore == playerscore)
                {
                    MessagerAddition.announceAll("[Revenge]: И так результаты битвы " + admin.getName() + "vs" +player.getName() + " <НИЧЬЯ> !", 0);
                }
                if(adminscore > playerscore)
                {
                    MessagerAddition.announceAll("[Revenge]: И так результаты битвы " + admin.getName() + "vs" +player.getName() + " - " + admin.getName() + " Выигрывает!", 0);
                }
                else
                    MessagerAddition.announceAll("[Revenge]: И так результаты битвы " + admin.getName() + "vs" +player.getName() + " - " + player.getName() + " Выигрывает!", 0);
                TeleportService.moveToBindLocation(player, true);
                TeleportService.moveToBindLocation(admin, true);
                admIsWin1 = false;
                playerIsWin1 = false;
                admIsWin2 = false;
                playerIsWin2 = false;
                isDraw1 = false;
                isDraw2 = false;
	}
	}, 15000);
       
        }
        void restore(Player player)
        {
		player.getLifeStats().restoreMp();
		player.getLifeStats().restoreHp();
                player.getLifeStats().restoreFp();
        }

}