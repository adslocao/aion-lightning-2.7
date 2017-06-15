package ai.instance.beshmundirTemple;

import com.aionemu.gameserver.ai2.AIName;

import ai.AggressiveNpcAI2;

/**
 * @author Kairyu
 *
 */
@AIName("sorcererhakiki")
public class SorcererHakiki extends AggressiveNpcAI2 {
  
	@Override
	public int modifyDamage(int damage)	{
		return 2;
	}

}
