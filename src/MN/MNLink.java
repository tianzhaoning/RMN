/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package MN;

import org.jgrapht.graph.DefaultWeightedEdge;
import java.io.Serializable;

/**
 *
 * @author Zhenhua Li
 */
public class MNLink extends DefaultWeightedEdge implements Serializable{
    /**
     * Name of link
     */
    private String name;
    double[] v_weight;//存放权重值
    /**
     * True if this link is undirected link
     */
    private boolean directed = true;
    /**
     * Set link kind is directed link or not
     * @param isDirectedLink
     */
    public void setDirected(boolean isDirectedLink){
        this.directed = isDirectedLink;
    }
    /**
     * Return kind of this link is directed link or not
     * @return <i>True</i> if this link is directed link
     */
    public boolean isDirectedLink(){
        return this.directed;
    }
    /**
     * Get name of this link
     * @return Name of the link
     */
    public String getName(){
        return this.name;
    }
    /**
     * Set name for this link
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }
    /**
     * Override method toString to include <i>name</i> for this link.
     * @return Name and other information extend parent class.
     */
    @Override public String toString() {
        return this.name + ":" + super.toString();
    }
    
    
}
