/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package MN;

import MN.Pair;
import java.util.*;
import java.io.Serializable;

/**
 *
 * @author Zhenhua Li
 */

public class MNNode implements Serializable {

	/**
	 * Ψһ�ؼ���
	 */
	String name;
	
	/**
	 * ��Ӧ��ֵ
	 */
	String value;
	
	/**
	 * key ��һ������� i_total_value ��ĿǰΪֹ ���ֵ ??
	 */
	Pair<String, Double>[] v_NodeValue;

	public MNNode() {

	}
	
	public MNNode(String name, int i_ValueNum) {
		this.name = name;
		v_NodeValue = new Pair[i_ValueNum];
		
		// ��ʼ��
		for (int i = 0; i < i_ValueNum; ++i) {
			v_NodeValue[i] = new Pair("", 0);
		}

	}

	public MNNode(String name, String value, int i_ValueNum) {
		this(name, i_ValueNum);
		this.value = value;
	}
	
	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {

		return name;
	}

}
