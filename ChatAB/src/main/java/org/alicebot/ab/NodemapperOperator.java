package org.alicebot.ab;
/* Program AB Reference AIML 2.0 implementation
        Copyright (C) 2013 ALICE A.I. Foundation
        Contact: info@alicebot.org

        This library is free software; you can redistribute it and/or
        modify it under the terms of the GNU Library General Public
        License as published by the Free Software Foundation; either
        version 2 of the License, or (at your option) any later version.

        This library is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
        Library General Public License for more details.

        You should have received a copy of the GNU Library General Public
        License along with this library; if not, write to the
        Free Software Foundation, Inc., 51 Franklin St, Fifth Floor,
        Boston, MA  02110-1301, USA.
*/
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NodemapperOperator {
    /**
     * number of branches from node
     *
     * @param node    Nodemapper object
     * @return        number of branches
     */
    public static int size(Nodemapper node) {
        HashSet set = new HashSet();
        if (node.shortCut) set.add("<THAT>");
        if (node.key != null) set.add(node.key);
        if (node.map != null) set.addAll(node.map.keySet());
        return set.size();
    }

    /**
     * insert a new link from this node to another, by adding a key, value pair
     *
     * @param node       Nodemapper object
     * @param key        key word
     * @param value      word maps to this next node
     */
   public static void put(Nodemapper node, String key, Nodemapper value) {
       if (node.map != null) {
           node.map.put(key, value);
       }
       else { // node.type == unary_node_mapper
             node.key = key;
             node.value = value;

       }
   }

    /**
     * get the node linked to this one by the word key
     *
     * @param node     Nodemapper object
     * @param key      key word to map
     * @return         the mapped node or null if the key is not found
     */
   public static Nodemapper get(Nodemapper node, String key) {
       if (node.map != null) {
//           if (!node.map.containsKey(key)) {
//               for (String s : node.map.keySet())
//                   if (s.contains("<REGEXP>")) {
//                       Pattern p = Pattern.compile((s.substring(8, s.length() - 9)).toLowerCase());
//                       Matcher m = p.matcher(key.toLowerCase());
//                       boolean b = m.matches();
//                       if (b) return node.map.get(s);
//                   }
//           }
           return node.map.get(key);
       }
       else {// node.type == unary_node_mapper
           if (key.equals(node.key)) return node.value;
           else return null;
       }

   }
	public static Nodemapper getReg(Nodemapper node, String key) {
		if (node.map != null) {
			if (!node.map.containsKey(key)) {
				for (String s : node.map.keySet())
					if (s.contains("<REGEXP>")) {
						Pattern p = Pattern.compile((s.substring(8, s.length() - 9)).toLowerCase());
						Matcher m = p.matcher(key.toLowerCase());
						boolean b = m.find();
						if (b) return node.map.get(s);
					}
			}
			return node.map.get(key);
		}
		else {// node.type == unary_node_mapper
			if (node.key != null && node.key.contains("</REGEXP>")) {
				Pattern p = Pattern.compile(node.key.substring(8, node.key.length() - 9));
				Matcher m = p.matcher(key.toUpperCase());
				if (m.find()) return node.value;
			}
//			if (key.substring(8, key.length() - 9).toUpperCase().equals(node.key.substring(8, node.key.length() - 9))) return node.value;
			return null;
		}

	}
    /**
     * check whether a node contains a particular key
     *
     * @param node    Nodemapper object
     * @param key     key to test
     * @return        true or false
     */
   public static boolean containsKey(Nodemapper node, String key)  {
       //System.out.println("containsKey: Node="+node+" Map="+node.map);
       if (node.map != null) {
           boolean containsKey = node.map.containsKey(key);
//           if (Main.anyKey) {
//               if (!containsKey) {
//                   for (String s : node.map.keySet()) {
//                       if (s.contains("</REGEXP>")) {
//                           Pattern p = Pattern.compile((s.substring(8, s.length() - 9)).toLowerCase());
//                           Matcher m = p.matcher(key.toLowerCase());
//                           containsKey = m.matches();
//                           System.out.println("contains = " + containsKey);
//                           System.out.println();
//                       }
//                       else continue;
//                       if (containsKey) return containsKey;
//                   }
//               }
//           }
           return containsKey;
       }
       else {// node.type == unary_node_mapper
           if (key.equals(node.key)) return true;
           else return false;
       }
   }

	public static int[] containsKeyReg(Nodemapper node, String key)  {
		int[] ageOfReg = {-1, -1};
		if (node.map != null) {
			for (String s : node.map.keySet()) {
				if (s.contains("</REGEXP>")) {
					Pattern p = Pattern.compile((s.substring(8, s.length() - 9)).toLowerCase());
					Matcher m = p.matcher(key.toLowerCase());
					if (m.find()) {
						ageOfReg[0] = m.start();
						ageOfReg[1] = m.end();
						return ageOfReg;
					}
				}
			}
		}
		else if (node.key != null && node.key.contains("</REGEXP>")) {
			Pattern p = Pattern.compile(node.key.substring(8, node.key.length() - 9).toLowerCase());
			Matcher m = p.matcher(key.toLowerCase());
			if (m.find()) {
				ageOfReg[0] = m.start();
				ageOfReg[1] = m.end();
				return ageOfReg;
			}
		}
		return ageOfReg;
	}

    /**
     * print all node keys
     *
     * @param node Nodemapper object
     */
    public static void printKeys (Nodemapper node)  {
        Set set = keySet(node);
        Iterator iter = set.iterator();
        while (iter.hasNext()) {
            System.out.println("" + iter.next());
        }
    }

    /**
     * get key set of a node
     *
     * @param node    Nodemapper object
     * @return        set of keys
     */
    public static Set<String> keySet(Nodemapper node) {
        if (node.map != null) {
            return node.map.keySet();
        }
        else {// node.type == unary_node_mapper
            Set set = new HashSet<String>();
            if (node.key != null) set.add(node.key);
            return set;
        }

    }

    /**
     * test whether a node is a leaf
     *
     * @param node     Nodemapper object
     * @return         true or false
     */
    public static boolean isLeaf(Nodemapper node) {
        return (node.category != null);
    }

    /**
     * upgrade a node from a singleton to a multi-way map
     *
     * @param node  Nodemapper object
     */
    public static void upgrade(Nodemapper node) {
        //System.out.println("Upgrading "+node.id);
        //node.type = MagicNumbers.hash_node_mapper;
        node.map = new HashMap<String, Nodemapper>();
        node.map.put(node.key, node.value);
        node.key = null;
        node.value = null;
    }
}
