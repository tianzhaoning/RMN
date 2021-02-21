/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MN;

/**
 * 
 * @author Zhenhua Li
 */

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.*;



import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import MN.*;

public class MN implements Serializable {
	
	private int i, j, k, l, m, i_row, i_column, i_NodeValueNum;
	String s_name_temp, s_name_source, s_name_target;
	MNNode N_source, N_target;

	private DefaultDirectedGraph<MNNode, MNLink> g;
	private HashMap<String, MNNode> nodeList;
	public MN() {
	}
 
	/**
	 * 
	 * @param s_row_column_value Ãû³Æ¾ØÕó
	 * @param i_NodeValueNum ÓÐ¶àÉÙÖÖÊôÐÔ
	 * @param b_FullyConnectedNetwork ÊÇ·ñ½¨Á¢È«Á¬½Ó
	 */
	public MN(String[][] s_row_column_value, int i_NodeValueNum,
			boolean b_FullyConnectedNetwork) {
		// ²ÎÊý¶ÔÓ¦ËµÃ÷£ºÃû³Æ¾ØÕó ¶àÉÙ¸öÀà±ð ÊÇ·ñ½¨Á¢È«ÁªÍøb

		this.i_row = s_row_column_value.length;// ÐÐÊý
		this.i_column = s_row_column_value[0].length;// ÁÐÊý
		this.i_NodeValueNum = i_NodeValueNum;// ÓÐ¶àÉÙ·ÖÀàÊôÐÔÈ¡Öµ

		g = new DefaultDirectedGraph<MNNode, MNLink>(MNLink.class);
		nodeList = new HashMap<String, MNNode>();

		// test

		// ³õÊ¼»¯½áµã
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//ÉèÖÃÈÕÆÚ¸ñÊ½
		//System.out.println("init node begin time: " + df.format(new Date()));// new Date()Îª»ñÈ¡µ±Ç°ÏµÍ³Ê±¼ä
		
		for (int i = 0; i < this.i_row; ++i)
			for (int j = 0; j < this.i_column; ++j) {
				String s_name_temp = i + "_" + j;
				MNNode node_temp = new MNNode(s_name_temp,
						s_row_column_value[i][j], this.i_NodeValueNum);
				// Ìí¼Óµ½ nodeList
				addMNNode(node_temp);
			}
		System.out.println();
		//System.out.println("init node end time: " + df.format(new Date()));// new Date()Îª»ñÈ¡µ±Ç°ÏµÍ³Ê±¼ä
		
			/* create nodes as following:
			0_0      0_1      0_2      ...   0_(i_column-1)
			1_0      1_1      1_2      ...   1_(i_column-1)
			2_0      2_1      2_2      ...   2_(i_column-1)
			...      ...      ...      ...         ...
			(i-1)_0 (i-1)_2 (i-1)_3    ...   (i-1)_(i_column-1)
			*/
           			//³õÊ¼»¯ ±ß
		if (b_FullyConnectedNetwork) {
			BuildFullyConnectedNet();
		} else {
			
			BuildNet();
		}
	}

	public void print_g() {
		System.out.println(g.toString());
	}
	
	private Object GetVertex(String s_Name, Graph g) {
		Object N_result = g.vertexSet().iterator().next();

		for (Object Vertex : g.vertexSet()) {
			if (Vertex.toString().equals(s_Name)) {
				N_result = Vertex;
				break;
			}
		}

		return N_result;
	}
	
      /**
     * Add a new KNNode to knowledge network
     * @param newNode
     */
	public void addMNNode(MNNode newNode) {
		g.addVertex(newNode);
		nodeList.put(newNode.getName(), newNode);
	}

	public MNLink addMNLink(String name, MNNode srcNode, MNNode desNode) {
		MNLink link = g.addEdge(srcNode, desNode);
		link.setName(name);
		link.setDirected(true);
		return link;
	}

	public void addMNLink(String name, MNNode srcNode, MNNode desNode,
			int i_Num) {
		// MNLink link = g.addEdge(srcNode, desNode);
		MNLink link = addMNLink(name, srcNode, desNode);
		link.v_weight = new double[i_Num];
		for (int i = 0; i < i_Num; ++i)
			link.v_weight[i] = 0;

	}

    /**
     * Add a new MNLink to knowledge network.
     * @param name The name of MNLink
     * @param srcNode Source MNNode of link
     * @param desNode Destination MNNode of link
     * @param isDirectedLink True if this link is directed link other False
     */
    public void addMNLink(String name, MNNode srcNode, MNNode desNode, boolean isDirectedLink){
        MNLink link1 = g.addEdge(srcNode, desNode);
        link1.setName(name);
        link1.setDirected(isDirectedLink);
        if(!isDirectedLink){
            MNLink link2 = g.addEdge(srcNode, desNode);
            link2.setName(name);
            link2.setDirected(isDirectedLink);
        }
    }
    
 
	public void BuildNet() {
		for (int j = 0; j < this.i_column - 1; ++j)
			for (int i = 0; i < this.i_row; ++i)
				for (int k = 0; k < this.i_row; ++k) {
					String s_name_source = i + "_" + j;
					l = j + 1;
					String s_name_target = k + "_" + l;
					N_source = getMNNode(s_name_source);
					N_target = getMNNode(s_name_target);
					addMNLink(N_source.getName() + "to" + N_target.getName(),
							N_source, N_target, this.i_NodeValueNum);

				}
	}
	
 
	public void BuildFullyConnectedNet() {

		for (j = 0; j < this.i_column - 1; ++j)
			for (i = 0; i < this.i_row; ++i)
				for (m = j; m < this.i_column - 1; ++m)
					for (k = 0; k < this.i_row; ++k) {
						s_name_source = i + "_" + j;
						l = m + 1;
						s_name_target = k + "_" + l;
						N_source = getMNNode(s_name_source);
						N_target = getMNNode(s_name_target);
						addMNLink(
								N_source.getName() + "to" + N_target.getName(),
								N_source, N_target, this.i_NodeValueNum);
					}

	}
   
    /**
     * Get a MNNode by name and return exists MNNode
     *
     * @param name Name of MNNode need get
     * @return MNNode if KN contain this name or not null will return
     */
    public MNNode getMNNode(String name){
        return nodeList.get(name);
    }    
    /**
     * Get all link of a MNNode
     * @param node A MNNode need get all MNLink
     * @return Set of MNLink
     */
    public Set<MNLink> getMNLinkSetOf(MNNode node){
        return g.edgesOf(node);
    }
    
    /**
     * Get all link beetwen two MNNode
     * @param source
     * @param des
     * @return
     */
	public MNLink getMNLinkOf(MNNode source, MNNode des) {
		return g.getEdge(source, des);
	}
	public MNLink getMNLinkOf(String src, String des) {
		MNNode srcNode = this.getMNNode(src);
		MNNode desNode = this.getMNNode(des);
		if (srcNode != null && desNode != null)
			return g.getEdge(this.getMNNode(src), this.getMNNode(des));
		return null;
	}
    /**
     * Get all MNNode of knowledge network
     * @return Set of all MNNode
     */
    public Set<MNNode> getAllMNNode(){
        return g.vertexSet();
    }
    /**
     * Get all MNLink of knowledge network
     * @return Set of all MNNode
     */
    public Set<MNLink> getAllMNLink(){
        return g.edgeSet();
    }
    /**
     * Delete a MNNode from knowledgenetwork
     * @param node
     * @return <i>True</i> if delete successful
     */
    public boolean delMNNode(MNNode node){
        nodeList.remove(node.getName());
        return g.removeVertex(node) ;
    }
    /**
     * Override method toString. Print information of knowledge network
     * @return Information string about all MNNode and MNLink
     */
    @Override
    public String toString(){
        return g.toString();
    }
    
   
	public void add_link_weight(String s_name_source, String s_name_target,
			int i_weight, int i_No) {
		MNLink link = getMNLinkOf(s_name_source, s_name_target);
		link.v_weight[i_No] += i_weight;
	}
	
	public void add_link_weight(String s_name_source, String s_name_target,
			double d_weight, double d_No) {
		int i_No = (int) d_No;
		MNLink link = getMNLinkOf(s_name_source, s_name_target);
		link.v_weight[i_No] += d_weight;
	}
	
	// »ñÈ¡ source µ½ targetÖ®¼äµÄÈ¨Öµ
	public double get_link_weight(String s_name_source, String s_name_target,
			int i_No) {
		MNLink link = getMNLinkOf(s_name_source, s_name_target);
		return link.v_weight[i_No];
	}
     
    
	/**
	 *  Ã»ÓÐµ÷ÓÃ
	 *	
	 */
	Node_Chain OutputNodeChain(int i_No, boolean b_Max) //// void OutputNodeChain(int i_No,bool b_Max)
	{
		Node_Chain node_chain = new Node_Chain();
		node_chain.v_node_chain = new String[i_column];
		MNNode v_source, v_target;
		String s_name;
		// ,s_name_source,s_name_target;
		double d_most_value;
		int i_temp;
		String s_target_name;

		i_temp = i_column - 1;
		s_name_target = "0_" + i_temp;
		v_target = getMNNode(s_name_target);
		s_target_name = s_name_target;
		d_most_value = v_target.v_NodeValue[i_No].getSecond();

		for (i = 1; i < i_row; ++i) {
			i_temp = i_column - 1;
			s_name_target = i + "_" + i_temp;;
			v_target = getMNNode(s_name_target);

			if (b_Max) {
				if (v_target.v_NodeValue[i_No].getSecond() > d_most_value) {
					s_target_name = s_name_target;
					d_most_value = v_target.v_NodeValue[i_No].getSecond();
				}

			} else {
				if (v_target.v_NodeValue[i_No].getSecond() < d_most_value) {
					s_target_name = s_name_target;
					d_most_value = v_target.v_NodeValue[i_No].getSecond();

				}

			}
		}
		node_chain.d_total_value = d_most_value;
		node_chain.v_node_chain[i_column - 1] = s_target_name;

		for (j = i_column - 1; j > 0; --j) {
			v_target = getMNNode(node_chain.v_node_chain[i_column - 1]);
			node_chain.v_node_chain[j - 1] = v_target.v_NodeValue[i_No]
					.getFirst();

		}

		for (j = 0; j < i_column; ++j)
		{
			System.out.println(node_chain.v_node_chain[j] + "->");
		}
		return node_chain;
	}

}
