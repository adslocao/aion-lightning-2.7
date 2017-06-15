/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aionemu.gameserver.global.additions;

import com.aionemu.commons.network.util.ThreadPoolManager;
import com.aionemu.gameserver.model.gameobjects.player.Player;
import com.aionemu.gameserver.utils.PacketSendUtility;
import com.aionemu.gameserver.world.World;
import com.aionemu.gameserver.world.knownlist.Visitor;

/**
 *
 * @author Dean
 */
public class MessagerAddition {
    
        protected void DEEPINSIDE()
        {
       
        }
        
        public static void announceAll(final String msg, int delay) {
        if (delay > 0) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                        @Override
                        public void visit(Player sender) {
                            PacketSendUtility.sendBrightYellowMessageOnCenter(sender, msg);
                            return;
                        }
                    });
                }
            }, delay);
        }
        else {
            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player sender) {
                    PacketSendUtility.sendBrightYellowMessageOnCenter(sender, msg);
                    return;
                }
            });
        }
        
    }
    public static void announce(Player player, String msg)
    {
        PacketSendUtility.sendBrightYellowMessageOnCenter(player, msg);
    }
    public static void message(Player player, String msg)
    {
        PacketSendUtility.sendMessage(player, msg);
    }
    public static void whiteMsg(Player player ,String msg)
    {
        PacketSendUtility.sendWhiteMessage(player, msg);
    }
    public static void whiteMsgOnCtr(Player player, String msg)
    {
        PacketSendUtility.sendWhiteMessageOnCenter(player, msg);
    }
    public static void yellowMsg(Player player, String msg)
    {
        PacketSendUtility.sendYellowMessage(player, msg);
    }
    public static void yellowMsgOnCtr(Player player, String msg)
    {
        PacketSendUtility.sendYellowMessageOnCenter(player, msg);
    }
     public static void messageToAll(final String msg, int delay) {
        if (delay > 0) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                        @Override
                        public void visit(Player sender) {
                            PacketSendUtility.sendMessage(sender, msg);
                            return;
                        }
                    });
                }
            }, delay);
        }
        else {
            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player sender) {
                    PacketSendUtility.sendMessage(sender, msg);
                    return;
                }
            });
        }
        
    }
          public static void whiteMsgToAll(final String msg, int delay) {
        if (delay > 0) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                        @Override
                        public void visit(Player sender) {
                            PacketSendUtility.sendWhiteMessage(sender, msg);
                            return;
                        }
                    });
                }
            }, delay);
        }
        else {
            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player sender) {
                     PacketSendUtility.sendWhiteMessage(sender, msg);
                    return;
                }
            });
        }
        
    }
       public static void whiteAnnounceToAll(final String msg, int delay) {
        if (delay > 0) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                        @Override
                        public void visit(Player sender) {
                            PacketSendUtility.sendWhiteMessageOnCenter(sender, msg);
                            return;
                        }
                    });
                }
            }, delay);
        }
        else {
            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player sender) {
                     PacketSendUtility.sendWhiteMessageOnCenter(sender, msg);
                    return;
                }
            });
        }
        
    }
       public static void yellowMsgToAll(final String msg, int delay) {
        if (delay > 0) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                        @Override
                        public void visit(Player sender) {
                            PacketSendUtility.sendYellowMessage(sender, msg);
                            return;
                        }
                    });
                }
            }, delay);
        }
        else {
            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player sender) {
                     PacketSendUtility.sendYellowMessage(sender, msg);
                    return;
                }
            });
        }
        
    }
          public static void yellowAnnounceToAll(final String msg, int delay) {
        if (delay > 0) {
            ThreadPoolManager.getInstance().schedule(new Runnable() {
                @Override
                public void run() {
                    World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                        @Override
                        public void visit(Player sender) {
                            PacketSendUtility.sendYellowMessageOnCenter(sender, msg);
                            return;
                        }
                    });
                }
            }, delay);
        }
        else {
            World.getInstance().doOnAllPlayers(new Visitor<Player>() {
                @Override
                public void visit(Player sender) {
                     PacketSendUtility.sendYellowMessageOnCenter(sender, msg);
                    return;
                }
            });
        }
       
    }

}
