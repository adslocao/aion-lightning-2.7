/*
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package com.aionemu.gameserver.geoEngine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.MappedByteBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import com.aionemu.gameserver.geoEngine.math.Matrix3f;
import com.aionemu.gameserver.geoEngine.math.Vector3f;
import com.aionemu.gameserver.geoEngine.models.GeoMap;
import com.aionemu.gameserver.geoEngine.scene.Geometry;
import com.aionemu.gameserver.geoEngine.scene.Mesh;
import com.aionemu.gameserver.geoEngine.scene.Node;
import com.aionemu.gameserver.geoEngine.scene.Spatial;
import com.aionemu.gameserver.geoEngine.scene.VertexBuffer;


/**
 * @author Mr. Poke
 */
public class GeoWorldLoader {

	private static String GEO_DIR = "data/geo/";

	private static boolean DEBUG = false;

	public static void setDebugMod(boolean debug) {
		DEBUG = debug;
	}

	@SuppressWarnings("resource")
	public static Map<String, Spatial> loadMeshs(String fileName) throws IOException {
		Map<String, Spatial> geoms = new HashMap<String, Spatial>();
		File geoFile = new File(fileName);
		FileChannel roChannel = null;
		MappedByteBuffer geo = null;
		roChannel = new RandomAccessFile(geoFile, "r").getChannel();
		geo = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) roChannel.size()).load();
		geo.order(ByteOrder.LITTLE_ENDIAN);
		while (geo.hasRemaining()) {
			short namelenght = geo.getShort();
			byte[] nameByte = new byte[namelenght];
			geo.get(nameByte);
			String name = new String(nameByte);
			int modelCount = geo.getShort();
			Node node = new Node(DEBUG ? name : null);
			for (int c = 0; c < modelCount; c++) {
				Mesh m = new Mesh();
				int vectorCount = ((int)geo.getShort()) * 3;
				FloatBuffer vertices = FloatBuffer.allocate(vectorCount);
				for (int x = 0; x < vectorCount; x++) {
					vertices.put(geo.getFloat());
				}
				int tringle = geo.getInt();
				ShortBuffer indexes = ShortBuffer.allocate(tringle);
				for (int x = 0; x < tringle; x++) {
					indexes.put(geo.getShort());
				}
				m.setBuffer(VertexBuffer.Type.Position, 3, vertices);
				m.setBuffer(VertexBuffer.Type.Index, 3, indexes);
				m.createCollisionData();
				Geometry geom = new Geometry(null, m);
				if (modelCount == 1)
					geoms.put(name, geom);
				node.attachChild(geom);
			}
			if (!node.getChildren().isEmpty()) {
				geoms.put(name, node);
			}
		}
		return geoms;
	}
	
	@SuppressWarnings("resource")
	public static boolean loadWorld(int worldId, Map<String, Spatial> models, GeoMap map) throws IOException {
		File geoFile = new File(GEO_DIR + worldId + ".geo");
		FileChannel roChannel = null;
		MappedByteBuffer geo = null;
		roChannel = new RandomAccessFile(geoFile, "r").getChannel();
		geo = roChannel.map(FileChannel.MapMode.READ_ONLY, 0, (int) roChannel.size()).load();
		geo.order(ByteOrder.LITTLE_ENDIAN);
		if (geo.get() == 0)
			map.setTerrainData(new short[] { geo.getShort() });
		else {
			int size = geo.getInt();
			short[] terrainData = new short[size];
			for (int i = 0; i < size; i++)
				terrainData[i] = geo.getShort();
			map.setTerrainData(terrainData);
		}
		while (geo.hasRemaining()) {
			int nameLenght = geo.getShort();
			byte[] nameByte = new byte[nameLenght];
			geo.get(nameByte);
			String name = new String(nameByte);
			Vector3f loc = new Vector3f(geo.getFloat(), geo.getFloat(), geo.getFloat());
			float[] matrix = new float[9];
			for (int i = 0; i < 9; i++)
				matrix[i] = geo.getFloat();
			float scale = geo.getFloat();
			Matrix3f matrix3f = new Matrix3f();
			matrix3f.set(matrix);
			Spatial node = models.get(name.toLowerCase());
			if (node != null) {
				Spatial nodeClone = null;
				try {
					nodeClone = node.clone();
				}
				catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				nodeClone.setTransform(matrix3f, loc, scale);
				nodeClone.updateModelBound();
				map.attachChild(nodeClone);
			}
		}
		map.updateModelBound();
		return true;
	}
}
