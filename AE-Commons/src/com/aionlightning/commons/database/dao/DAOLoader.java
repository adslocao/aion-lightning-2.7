/*
 * This file is part of aion-emu <aion-emu.com>.
 *
 * aion-emu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * aion-emu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aion-emu.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aionlightning.commons.database.dao;

import com.aionlightning.commons.scripting.classlistener.ClassListener;
import com.aionlightning.commons.utils.ClassUtils;

import java.lang.reflect.Modifier;

/**
 * Utility class that loads all DAO's after script context initialization.<br>
 * DAO should be public, not abstract, not interface, must have default no-arg public constructor.
 *
 * @author SoulKeeper, Aquanox
 */
public class DAOLoader implements ClassListener {

	@SuppressWarnings("unchecked")
	@Override
	public void postLoad(Class<?>[] classes) {
		// Register DAOs
		for (Class<?> clazz : classes) {
			if (!isValidDAO(clazz))
				continue;

			try {
				DAOManager.registerDAO((Class<? extends DAO>) clazz);
			} catch (Exception e) {
				throw new Error("Can't register DAO class", e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void preUnload(Class<?>[] classes) {
		// Unregister DAO's
		for (Class<?> clazz : classes) {
			if (!isValidDAO(clazz))
				continue;

			try {
				DAOManager.unregisterDAO((Class<? extends DAO>) clazz);
			} catch (Exception e) {
				throw new Error("Can't unregister DAO class", e);
			}
		}
	}

	/**
	 * @param clazz
	 * @return boolean
	 */
	public boolean isValidDAO(Class<?> clazz) {
		if (!ClassUtils.isSubclass(clazz, DAO.class))
			return false;

		final int modifiers = clazz.getModifiers();

		if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers))
			return false;

		if (!Modifier.isPublic(modifiers))
			return false;

		if (clazz.isAnnotationPresent(DisabledDAO.class))
			return false;

		return true;
	}
}
