/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 *    LWL.java
 *    Copyright (C) 1999, 2002, 2003 Len Trigg, Eibe Frank, Ashraf M. Kibriya
 *
 */
package weka.classifiers.MN;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.SingleClassifierEnhancer;

import java.util.regex.*;

import attributeChange.BestInstant;



import java.io.*;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;

import weka.core.*;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;
import MN.*;

public class RMN extends SingleClassifierEnhancer implements OptionHandler, WeightedInstancesHandler {

	/**
	 * a ZeroR model in case no model can be built from the data or the network
	 * predicts all zeros for the classes
	 */
	private Classifier m_ZeroR;
	/** Whether to use the default ZeroR model */
	private boolean m_useDefaultModel = false;
	private double[] m_classvalues;
	/** The number of classes. */
	private int m_numClasses = 0;
	private int m_minBucketSize = 6;
	/** The number of attributes. */
	private int m_numAttributes = 0; // note the number doesn't include the
										// class.
	private MN m_MN;
	private boolean m_ConsiderMissingValues = false;
	private boolean m_FullyConnectedNetwork = false;
	private double[][] m_OneRRule_buckets;
	String[][] s_row_column_value, s_row_column_value_trans;

	private class OneRRule implements Serializable {

		/** The class attribute. */
		private Attribute m_class;
		/** The number of instances used for building the rule. */
		private int m_numInst;
		/** Attribute to test */
		private Attribute m_attr;
		/** Training set examples this rule gets right */
		private int m_correct;
		/** Predicted class for each value of attr */
		private int[] m_classifications;
		/** Predicted class for missing values */
		private int m_missingValueClass = -1;
		/** Breakpoints (numeric attributes only) */
		private double[] m_breakpoints, m_buckets;

		/**
		 * Constructor for nominal attribute.
		 */
		public OneRRule(Instances data, Attribute attribute) throws Exception {

			m_class = data.classAttribute();
			m_numInst = data.numInstances();
			m_attr = attribute;
			m_correct = 0;
			m_classifications = new int[m_attr.numValues()];
		}

		/**
		 * Constructor for numeric attribute.
		 */
		public OneRRule(Instances data, Attribute attribute, int nBreaks) throws Exception {

			m_class = data.classAttribute();
			m_numInst = data.numInstances();
			m_attr = attribute;
			m_correct = 0;
			m_classifications = new int[nBreaks];
			m_breakpoints = new double[nBreaks - 1]; // last breakpoint is
														// infinity
		}

		/**
		 * Returns a description of the rule.
		 */
		public String toString() {

			try {
				StringBuffer text = new StringBuffer();
				text.append(m_attr.name() + ":\n");
				for (int v = 0; v < m_classifications.length; v++) {
					text.append("\t");
					if (m_attr.isNominal()) {
						text.append(m_attr.value(v));
					} else if (v < m_breakpoints.length) {
						text.append("< " + m_breakpoints[v]);
					} else if (v > 0) {
						text.append(">= " + m_breakpoints[v - 1]);
					} else {
						text.append("not ?");
					}
					text.append("\t-> " + m_class.value(m_classifications[v]) + "\n");
				}
				if (m_missingValueClass != -1) {
					text.append("\t?\t-> " + m_class.value(m_missingValueClass) + "\n");
				}
				text.append("(" + m_correct + "/" + m_numInst + " instances correct)\n");
				return text.toString();
			} catch (Exception e) {
				return "Can't print OneR classifier!";
			}
		}
	}

	// private OneRRule m_OneRRule;
	/**
	 * Returns a string describing classifier
	 * 
	 * @return a description suitable for displaying in the explorer/experimenter
	 *         gui
	 */
	public String globalInfo() {
		return "Class for performing MN learning. Can do " + "classification  or regression  ";
	}

	/**
	 * Constructor.
	 */
	public RMN() {
	}

	/**
	 * Returns an enumeration describing the available options.
	 *
	 * @return an enumeration of all the available options.
	 */
	/**
	 * Returns an enumeration describing the available options.
	 *
	 * @return an enumeration of all the available options.
	 */
	public Enumeration listOptions() {

		Vector newVector = new Vector(1);

		String string = "\tThe minimum number of objects in a bucket (default: 6).";

		newVector.addElement(
				new Option("\t Consider Missing Valusesn.\n" + "\t(default Ingore Missing Data)", "M", 0, "-M"));
		newVector.addElement(new Option(string, "B", 1, "-B <minimum bucket size>"));
		newVector.addElement(new Option("\t Use the Fully Connected Network.\n" + "\t(default Don't Use  )", "C", 0,
				"-C <minimum bucket size>"));
		return newVector.elements();

	}

	/**
	 * Parses a given list of options. Valid options are:
	 * <p>
	 * 
	 */
	public void setOptions(String[] options) throws Exception {

		m_ConsiderMissingValues = Utils.getFlag('M', options);

		String bucketSizeString = Utils.getOption('B', options);
		if (bucketSizeString.length() != 0) {
			m_minBucketSize = Integer.parseInt(bucketSizeString);
		} else {
			m_minBucketSize = 6;
		}
	}

	/**
	 * Gets the current settings of the classifier.
	 *
	 * @return an array of strings suitable for passing to setOptions
	 */
	public String[] getOptions() {

		String[] options = new String[4];
		int current = 0;

		if (m_ConsiderMissingValues) {
			options[current++] = "-M";
		}

		options[current++] = "-B";

		options[current++] = "" + m_minBucketSize;

		if (m_FullyConnectedNetwork) {
			options[current++] = "-C";
		}

		while (current < options.length) {
			options[current++] = "";
		}
		return options;

	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String ConsiderMissingValuesTipText() {
		return "Whether to consider missing values.";
	}

	/**
	 * 须有set 和 get 函数 才可以出现参参数对话框改变参数
	 *
	 * @return true if instance data is saved
	 */
	public boolean getConsiderMissingValues() {

		return m_ConsiderMissingValues;
	}

	/**
	 * Set whether instance data is to be saved.
	 * 
	 * @param v
	 *            true if instance data is to be saved
	 */
	public void setConsiderMissingValues(boolean v) {

		m_ConsiderMissingValues = v;
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String FullyConnectedNetworkTipText() {
		return "Whether to Use the fully connected network.";
	}

	/**
	 * 须set 和 get 函数 才可以出现参参数对话框改变参数
	 *
	 * @return true if instance data is saved
	 */
	public boolean getFullyConnectedNetwork() {

		return m_FullyConnectedNetwork;
	}

	/**
	 * Set whether instance data is to be saved.
	 * 
	 * @param v
	 *            true if instance data is to be saved
	 */
	public void setFullyConnectedNetwork(boolean v) {

		m_FullyConnectedNetwork = v;
	}

	/**
	 * Returns the tip text for this property
	 * 
	 * @return tip text for this property suitable for displaying in the
	 *         explorer/experimenter gui
	 */
	public String minBucketSizeTipText() {
		return "The minimum bucket size used for discretizing numeric " + "attributes.";
	}

	/**
	 * Get the value of minBucketSize.
	 * 
	 * @return Value of minBucketSize.
	 */
	public int getMinBucketSize() {

		return m_minBucketSize;
	}

	/**
	 * Set the value of minBucketSize.
	 * 
	 * @param v
	 *            Value to assign to minBucketSize.
	 */
	public void setMinBucketSize(int v) {

		m_minBucketSize = v;
	}
    public static Instances thebest;
    
    public static Instances initInstances;
    public static int set=0;
	public void getBest(Instances instances) throws Exception{
		this.initInstances=instances;
		//深拷贝
		BestInstant BI=new BestInstant();
		BI.findBestInstant(instances);
		FastVector fv=new FastVector();
         for(int time3=0;time3<BI.fv.size();time3++){
        	 fv.addElement(BI.fv.elementAt(time3));
        	 
        }
		Instances changeInstances=new Instances("test",fv,instances.numInstances());
        for(int i=0;i<instances.numInstances();i++){
        	Instance it=instances.instance(i);
        	changeInstances.add(it);
        	
    
        }
       
        System.out.println("深拷贝之后的数据集");
       System.out.println(changeInstances); 
        
         //使用BI2进行全排列操作
		BestInstant BI2=new BestInstant();
		BI2.findBestInstant(changeInstances);
	    BI2.permutation(BI2.AttributeString, BI2.fv, BI2.attributes,0);
		Instances best=BI2.returnBestInstances();
		this.thebest=best;
		
	}
	/*
	static{
		//C:\\Users\\tianzhaoning\\Desktop\\等待校验\\weather.arff
		 //设定布尔值
		boolean tag=false;
		//实例化BestInstant对象
		BestInstant BI=new BestInstant();
		//从磁盘读入文件
		File inputFile=BI.readFile();
		//判断文件合法性
		while(!inputFile.exists()){
		   inputFile=BI.readFile();
		}
		
	
		//C:\\等待校验\\Surveillance.csv
		String name=inputFile.getName().split("\\.")[1];
		
		Instances instancesTrain=null;
		switch(name){
		   case "arff":
			    //创建数据加载器
	    		ArffLoader atf = new ArffLoader();
	    		//加载数组
			try {
				atf.setFile(inputFile);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
	            //获取数据集
			try {
				instancesTrain = atf.getDataSet();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}  
			   break;
		   case "csv" :
			     //创建数据加载器
			     CSVLoader loader = new CSVLoader();
			     //加载文件
			try {
				loader.setSource(inputFile);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			     //获取数据集
			try {
				instancesTrain = loader.getDataSet();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			    // System.out.println(instancesTrain);
			     break;
		 
		}
	
        //初始化BI
        BI.findBestInstant(instancesTrain);
        //进行全排列，创建数据集,训练
	    try {
			BI.permutation(BI.AttributeString, BI.fv, BI.attributes,0);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    //在内存中获取最好的数据集
	    thebest=BI.returnBestInstances();
	    
	}*/
	public Instances permutationInstances(Instances instances) throws Exception{
		
		// System.out.println("训练样本");
		if(this.thebest==null){
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
	        ObjectOutputStream oo = new ObjectOutputStream(bo);
	        oo.writeObject(instances);
	        // 从流里读出来
	        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
	        ObjectInputStream oi = new ObjectInputStream(bi);
	        Instances it=(Instances) oi.readObject();
		   this.getBest(it);
		   System.out.println("最好的样本为:");
		   System.out.println(this.thebest);
		}
		//System.out.println("我被执行了");
		//System.out.println(instance);
		BestInstant BI=new BestInstant();
		BI.findBestInstant(this.thebest);
		Instances changeInstances=new Instances("test",BI.fv,instances.numInstances());
		for(int k=0;k<instances.numInstances();k++){
			Instance instance=instances.instance(k);
			
		HashMap<String,String>datasource=new HashMap<String, String>();
		for(int j=0;j<instance.numAttributes();j++){
			String name=instance.attribute(j).toString();
			//System.out.println(name);
			String value=instance.toString(j);
			//System.out.println(value);
			datasource.put(name, value);
		}
		
		
		Instance orderInstance=this.thebest.instance(0);
		Instance it=new DenseInstance(instance.numAttributes());
		  for(int i=0;i<orderInstance.numAttributes();i++){
			       
			       
			       
				    Attribute attribute=orderInstance.attribute(i);
				    //System.out.println(attribute);
				    String name=attribute.toString();
				    String value=datasource.get(name);
				  //  System.out.println(value);
		    	
		            if(attribute.type()==Attribute.NUMERIC){
				          if(value.equals("?")){
				        	    // System.out.println("我是numeric,值为问号");
				    	           double a=0;
				    	           it.setValue(attribute,a);
				    	           
				           }else{
				        	  // System.out.println("我是numeric,值正常");
			   	                   double a =Double.parseDouble(value);
			   	                 it.setValue(attribute,a);
				           }
			    
		    		     
		           }else{
		        	               try {
		        	            	       
		        		                    it.setValue(attribute,value);
		        		                   
					               } catch (Exception e) {
					            	   System.out.println("出错");
					            	      // this.test();
						                  
					               }
	                 
		  	       }
		           
	      }
		// Instances bit=this.thebest;
	     //bit.add(it);
	     //it=bit.instance(this.thebest.numInstances()-1);
	       //System.out.println(it);
	       changeInstances.add(it);
		}
		return changeInstances;
	}
	public void buildClassifier(Instances instances) throws Exception {
		//System.out.println("buildclassifier");
		//System.out.println("初始进入分类器的样本");
		//System.out.println(instances);
		/*
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream oo = new ObjectOutputStream(bo);
        oo.writeObject(instances);
        // 从流里读出来
        ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
        ObjectInputStream oi = new ObjectInputStream(bi);
        Instances it=(Instances) oi.readObject();
		// System.out.println("训练样本");
		if(this.thebest==null){
		
		   this.getBest(it);
		   System.out.println("最好的样本为:");
		   System.out.println(this.thebest);
		}
		//System.out.println(111111111);
		
		  
		
		    System.out.println("初始进入分类器的样本为:");
		    
		    System.out.println(instances);
			//instance=this.permutationInstance(instance);
			instances=this.permutationInstances(instances);
			
		
		 
	      ///	System.out.println(instances);
		System.out.println("将要构造分类器的样本为:");
		System.out.println(instances);
		instances.setClassIndex(instances.numAttributes()-1);
		
		*/
		
		instances=this.permutationInstances(instances);
		instances.setClassIndex(instances.numAttributes()-1);
		
		
		
		
		
		int i_row = 0, i_max_row = 0;

		if (instances.checkForStringAttributes()) {
			throw new UnsupportedAttributeTypeException("Cannot handle string attributes!");
		}

		// Copy the instances
		Instances m_instances = new Instances(instances);
		// 删除未打标识的数据，即分类值为空的数据
		m_instances.deleteWithMissingClass();

		if (m_instances.numInstances() == 0) {
			throw new Exception("No instances with a class value!");
		}

		// only class? -> build ZeroR model
		if (m_instances.numAttributes() == 1) {
			System.err.println(
					"Cannot build model (only class attribute present in data!), " + "using ZeroR model instead!");
			m_ZeroR = new weka.classifiers.rules.ZeroR();
			m_ZeroR.buildClassifier(m_instances);
			m_useDefaultModel = true;
			return;
		} else {
			m_ZeroR = null;
			m_useDefaultModel = false;
		}

		if (m_instances.numInstances() == 0) {
			throw new IllegalArgumentException("No training instances.");
		}

		// 属性个数
		m_numAttributes = m_instances.numAttributes() - 1;// 除去最后一列，因最后一列为分类值
		//System.out.println("m_numAttributes : " + m_numAttributes);

		i_max_row = 0;
		m_OneRRule_buckets = new double[m_numAttributes][];
		// 每个属性有多少个离散化取值
		for (int i = 0; i < m_numAttributes; i++) {
			//System.out.println("m_instances.attribute(i) : " + m_instances.attribute(i));
			if (m_instances.attribute(i).isNominal()) {
				i_row = m_instances.attribute(i).numValues();
				//System.out.println("nominal m_instances.attribute(i).numValues(): " + m_instances.attribute(i).numValues());
			} else {
				//System.out.println("numeric m_instances.attribute(i).numValues(): " + m_instances.attribute(i).numValues());
				for(int k=0; k<m_instances.attribute(i).numValues(); k++) {
					System.out.println(m_instances.attribute(i).value(k));
				}
				OneRRule m_OneRRule = newRule(m_instances.attribute(i), m_instances);
				i_row = m_OneRRule.m_buckets.length;
				m_OneRRule_buckets[i] = m_OneRRule.m_buckets;
			}

			if (i_max_row < i_row) {
				i_max_row = i_row;// 最多值的个数，建网时的行数
			}
		}
		

		/**
		 * if (!classAttribute().isNominal()) { return 1; } else { return
		 * classAttribute().numValues(); }
		 */
		// 分类属性标签一共有多少种  就是最后一列 分类属性有几种结果
		m_numClasses = m_instances.numClasses();
		//System.out.println("m_numClasses 类的数量: " + m_numClasses);
		m_classvalues = new double[m_numClasses];
		for (int i = 0; i < m_numClasses; ++i) {
			// 获取分类结果属性的值 存入数组中
			m_classvalues[i] = m_instances.classAttribute().indexOfValue(m_instances.classAttribute().value(i));
		}
		

		// ----------------------------------------------
		// 形成名称矩阵 s_row_column_value[][]
		// ----------------------------------------------
		/**
		 * 第一个属性            第二个属性	 第三个属性  	   第四个属性 
		 * sunny,       hot,      high,   TRUE, 
		 * overcast,    mild, 	  normal, FALSE, 
		 * rainy,       cool,      2_2,     2_3,
		 */
		s_row_column_value = new String[i_max_row][m_numAttributes];
		
		// O(N^2)
		for (int j = 0; j < m_numAttributes; ++j) {
			if (m_instances.attribute(j).isNominal()) {
				//System.out.println("进入isNominal中....");

				// 每次循环都是根据i_max_row来生成
				for (int i = 0; i < i_max_row; ++i) {
					if (i > (m_instances.attribute(j).numValues() - 1)) {
						s_row_column_value[i][j] = i + "_" + j;// 	
					} else {
						s_row_column_value[i][j] = m_instances.attribute(j).value(i).toString();// 没超出行的范围，直接采用属性值。
						//System.out.println("s_row_column_value[i][j] : -->" + s_row_column_value[i][j]);
					}
				}

			} else {
				
				//System.out.println("进入no nominal....");
				
				for (int i = 0; i < m_OneRRule_buckets[j].length; ++i) {
					try {
						s_row_column_value[i][j] = Utils.doubleToString(m_OneRRule_buckets[j][i], 5);// 没超出行的范围，直接采用属性值。
					} catch (Exception e) {
						System.out.println("testEx, catch exception");
					}

				}
				for (int i = m_OneRRule_buckets[j].length; i < i_max_row; ++i) {
					s_row_column_value[i][j] = i + "_" + j;// 超出了行的范围，由系统赋名称
				}

			}
		}
		// ----------------------------------------------
		// 以名称矩阵为基础，建网
		// ----------------------------------------------
		m_MN = new MN(s_row_column_value, m_numClasses, m_FullyConnectedNetwork);

		// 参数对应说明：名称矩阵 多少个类别 是否建立全联网
		// ----------------------------------------------
		// 求名称转置矩阵 用以查index用
		// ----------------------------------------------
		s_row_column_value_trans = ArrayTranspose(s_row_column_value);
		// ----------------------------------------------
		// 给网的每一link赋值。
		// ----------------------------------------------

		if (m_FullyConnectedNetwork) {
			for (int i = 0; i < m_instances.numInstances(); i++) {
				// instance(i)是得到第i个样本
				// 多少个列（或称属性）
				for (int j = 0; j < m_numAttributes - 1; j++) {
					int i_name_source;
					if (m_instances.instance(i).isMissing(j))
						continue;// test if the value is missing
					if (m_instances.instance(i).attribute(j).isNominal()) {
						i_name_source = GetIndex(m_instances.instance(i).stringValue(j), s_row_column_value_trans[j]);
					} else {
						i_name_source = GetIndex(m_instances.instance(i).value(j), s_row_column_value_trans[j]);
					}
					for (int k = j; k < m_numAttributes - 1; k++) {
						// System.out.println(m_instances.instance(i));
						int i_name_destination;

						if (m_instances.instance(i).isMissing(k + 1)) {
							continue;
						}

						if (m_instances.instance(i).attribute(k + 1).isNominal()) {
							i_name_destination = GetIndex(m_instances.instance(i).stringValue(k + 1),
									s_row_column_value_trans[k + 1]);
						} else {
							i_name_destination = GetIndex(m_instances.instance(i).value(k + 1),
									s_row_column_value_trans[k + 1]);
						}

						String s_name_source = i_name_source + "_" + j;
						String s_name_destination = i_name_destination + "_" + (k + 1);

						m_MN.add_link_weight(s_name_source, s_name_destination, m_instances.instance(i).weight(),
								m_instances.instance(i).classValue());
					}
				}
				// System.out.println(m_numAttributes + " " + i_max_row);
			}
		} else

		{
			// O(N^2)
			for (int i = 0; i < m_instances.numInstances(); i++) {
				// instance(i)是得到第i个样本
				//System.out.println("\n\t This is the " + i + "st/rd instance: " + m_instances.instance(i) + "\n");
				for (int j = 0; j < m_numAttributes - 1; j++) {

					//System.out.println("\n\t This is the " + j + "st/rd attribute: "
							//+ m_instances.instance(i).attribute(j).toString() + "\n");

					int i_name_source, i_name_destination;

					if (m_instances.instance(i).isMissing(j))
						continue;// test if the value is missing

					if (m_instances.instance(i).attribute(j).isNominal()) {
						i_name_source = GetIndex(m_instances.instance(i).stringValue(j), s_row_column_value_trans[j]);
					} else {

						i_name_source = GetIndex(m_instances.instance(i).value(j), s_row_column_value_trans[j]);
					}

					if (m_instances.instance(i).isMissing(j + 1))
						continue;// test if the value is missing
					if (m_instances.instance(i).attribute(j + 1).isNominal()) {
						i_name_destination = GetIndex(m_instances.instance(i).stringValue(j + 1),
								s_row_column_value_trans[j + 1]);
					} else {
						i_name_destination = GetIndex(m_instances.instance(i).value(j + 1),
								s_row_column_value_trans[j + 1]);
						// System.out.println("\n\t This is the j+1=" + (j + 1)
						// + "st/rd attribute value: "
						// + m_instances.instance(i).value(j + 1) + "\n");
					}

					String s_name_source = i_name_source + "_" + j;
					String s_name_destination = i_name_destination + "_" + (j + 1);
					// System.out.println("\n\t s_name_source=" + s_name_source
					// + "; s_name_destination= " + s_name_destination
					// + "\n");

					/*
					 * System.out.println(" m_instances.instance(i).weight() : " +
					 * m_instances.instance(i).weight());
					 * System.out.println(" m_instances.instance(i) : " + m_instances.instance(i));
					 * System.out.println("m_instances.instance(i).classValue() : " +
					 * m_instances.instance(i).classValue());
					 * System.out.println("m_instances.toString() ： " + m_instances.toString());
					 */
					m_MN.add_link_weight(s_name_source, s_name_destination, m_instances.instance(i).weight(),
							m_instances.instance(i).classValue());
				}

			}
			m_MN.getAllMNLink();
		}
		
		
		{
			for (int i = 0; i < m_instances.numInstances(); i++) {
				// instance(i)是得到第i个样本
				for (int j = 0; j < m_numAttributes - 1; j++) {

					//System.out.println("\n\t This is the " + j + "st/rd attribute: "
							//+ m_instances.instance(i).attribute(j).toString() + "\n");

					int i_name_source, i_name_destination;

					if (m_instances.instance(i).isMissing(j))
						continue;// test if the value is missing

					if (m_instances.instance(i).attribute(j).isNominal()) {
						i_name_source = GetIndex(m_instances.instance(i).stringValue(j), s_row_column_value_trans[j]);

					} else {

						i_name_source = GetIndex(m_instances.instance(i).value(j), s_row_column_value_trans[j]);

					}

					
					if (m_instances.instance(i).isMissing(j + 1))
						continue;// test if the value is missing
					if (m_instances.instance(i).attribute(j + 1).isNominal()) {
						i_name_destination = GetIndex(m_instances.instance(i).stringValue(j + 1),
								s_row_column_value_trans[j + 1]);
					} else {
						i_name_destination = GetIndex(m_instances.instance(i).value(j + 1),
								s_row_column_value_trans[j + 1]);
						// System.out.println("\n\t This is the j+1=" + (j + 1)
						// + "st/rd attribute value: "
						// + m_instances.instance(i).value(j + 1) + "\n");
					}

					String s_name_source = i_name_source + "_" + j;
					String s_name_destination = i_name_destination + "_" + (j + 1);
					// System.out.println("\n\t s_name_source=" + s_name_source
					// + "; s_name_destination= " + s_name_destination
					// + "\n");

					/*
					 * m_MN.add_link_weight(s_name_source, s_name_destination,
					 * m_instances.instance(i).weight(), m_instances.instance(i).classValue());
					 */
					double weight = m_MN.get_link_weight(s_name_source, s_name_destination, (int)m_instances.instance(i).classValue());
					//System.out.println("s_name_source :" + s_name_source  + "  s_name_destination : " +s_name_destination + " : " + 
					//(int)m_instances.instance(i).classValue() + "weight : " + weight);

				}

			}
		}
		
		
		
		
		//System.out.println("结束分类 time :" + df.format(new Date()));
	}

	// 得到s_value 在 s_value_array的下标
	public int GetIndex(String s_value, String[] s_value_array) {
		int i_index = 0;
		for (int i = 0; i < s_value_array.length; i++) {
			if (s_value == s_value_array[i]) {
				i_index = i;
				break;
			}
		}
		return i_index;
	}

	public static boolean isDouble(String str) {
		// 模式匹配
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");

		return pattern.matcher(str).matches();

	}
	
	// 求数值型 连续值 的下表
	public int GetIndex(double d_value, String[] s_value_array) {
		int i_index = 0;
		double d_diff = Math.abs(d_value - Double.parseDouble(s_value_array[0]));
		for (int i = 0; i < s_value_array.length; i++) {
			if (!isDouble(s_value_array[i])) {
				break;
			}
			if (d_value == Double.parseDouble(s_value_array[i])) {
				i_index = i;
				break;
			} else {
				double d_temp = Math.abs(d_value - Double.parseDouble(s_value_array[i]));
				if (d_temp < d_diff) {
					d_diff = d_temp;
					i_index = i;
				}

			}
		}
		return i_index;
	}

	// 求转置矩阵
	/**
	 * 1 2 3 4 1 5 9 5 6 7 8 ---- >>> 2 6 10 9 10 11 12 3 7 11 4 8 12
	 */
	public String[][] ArrayTranspose(String[][] s_array) {
		int m = s_array.length;
		int n = s_array[0].length;
		String[][] s_Trans = new String[n][m];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				s_Trans[j][i] = s_array[i][j];
			}
		}
		return s_Trans;
	}

	/*public double classifyInstance(Instance instance) throws Exception {

		if (m_ZeroR != null) {
			return m_ZeroR.classifyInstance(instance);
		}

		double[] d_ClassValues = new double[m_numClasses];
		for (int i = 0; i < m_numClasses; ++i)
			d_ClassValues[i] = 0;

		for (int j = 0; j < m_numAttributes - 1; j++) {
			int i_name_source, i_name_destination;
			if (instance.attribute(j).isNominal()) {
				i_name_source = GetIndex(instance.stringValue(j), s_row_column_value_trans[j]);
			} else {
				i_name_source = GetIndex(instance.value(j), s_row_column_value_trans[j]);
			}

			if (instance.attribute(j + 1).isNominal()) {
				i_name_destination = GetIndex(instance.stringValue(j + 1), s_row_column_value_trans[j + 1]);
			} else {
				i_name_destination = GetIndex(instance.value(j + 1), s_row_column_value_trans[j + 1]);
			}

			String s_name_source = i_name_source + "_" + j;
			String s_name_destination = i_name_destination + "_" + (j + 1);

			for (int i = 0; i < m_numClasses; ++i)
				d_ClassValues[i] += m_MN.get_link_weight(s_name_source, s_name_destination, i);
		}
		return Utils.maxIndex(d_ClassValues);

	}*/

	/**
	 * Calculates the class membership probabilities for the given test instance.
	 *
	 * @param instance
	 *            the instance to be classified
	 * @return predicted class probability distribution
	 * @exception Exception
	 *                if class is numeric
	 */
	public Instance permutationInstance(Instance instance){
		//System.out.println("我被执行了");
		//System.out.println(instance);
		HashMap<String,String>datasource=new HashMap<String, String>();
		for(int j=0;j<instance.numAttributes()-1;j++){
			String name=instance.attribute(j).toString();
			//System.out.println(name);
			String value=instance.toString(j);
			//System.out.println(value);
			datasource.put(name, value);
		}
		
		
		Instance orderInstance=this.thebest.instance(0);
		Instance it=new DenseInstance(instance.numAttributes());
		  for(int i=0;i<orderInstance.numAttributes()-1;i++){
			       
			       
			       
				    Attribute attribute=orderInstance.attribute(i);
				   // System.out.println(attribute);
				    String name=attribute.toString();
				    String value=datasource.get(name);
				   // System.out.println(value);
		    	
		            if(attribute.type()==Attribute.NUMERIC){
				          if(value.equals("?")){
				        	    // System.out.println("我是numeric,值为问号");
				    	           double a=0;
				    	           it.setValue(attribute,a);
				    	           
				           }else{
				        	  // System.out.println("我是numeric,值正常");
			   	                   double a =Double.parseDouble(value);
			   	                 it.setValue(attribute,a);
				           }
			    
		    		     
		           }else{
		        	               try {
		        	            	       
		        		                    it.setValue(attribute,value);
		        		                   
					               } catch (Exception e) {
					            	   System.out.println("出错");
					            	      // this.test();
						                  
					               }
	                 
		  	       }
		           
	      }
		  BestInstant BI=new BestInstant();
			BI.findBestInstant(this.thebest);
           Instances bit=new Instances("test",BI.fv,1);

	       bit.add(it);
	  
	      it=bit.instance(0);
	      System.out.println(it);	
		
		return it;
	}
	
	
	public double[] distributionForInstance(Instance instance) throws Exception {
		//System.out.println("测试1");
		System.out.println("评估");
		instance=this.permutationInstance(instance);
	//	System.out.println("测试2");
	//	System.out.println(instance);
		
         
		double[] d_ClassValues = new double[m_numClasses];// 分类值分项汇总
		double[] d_2ClassValues = new double[m_numClasses];// 分类值分项汇总
		double[][] d_2DClassvalue = new double[m_numAttributes - 1][m_numClasses];
		double[] d_tempClassValues = new double[m_numClasses];
		double[] probs = new double[m_numClasses];
		double d_total = 0, d_col_total = 0;

		for (int i = 0; i < m_numClasses; ++i) {
			d_ClassValues[i] = 0;
			d_2ClassValues[i] = 0;
			for (int j = 0; j < m_numAttributes - 1; j++)
				d_2DClassvalue[j][i] = 0;
		}

		if (m_FullyConnectedNetwork) {
			for (int j = 0; j < m_numAttributes - 1; j++) {
				int i_name_source;

				if (instance.isMissing(j))
					continue;// test if the value is missing
				if (instance.attribute(j).isNominal()) {
					i_name_source = GetIndex(instance.stringValue(j), s_row_column_value_trans[j]);
				} else {
					i_name_source = GetIndex(instance.value(j), s_row_column_value_trans[j]);
				}

				for (int k = j; k < m_numAttributes - 1; k++) {
					int i_name_destination;
					if (instance.isMissing(k + 1))
						continue;// test if the value is missing
					if (instance.attribute(k + 1).isNominal()) {
						i_name_destination = GetIndex(instance.stringValue(k + 1), s_row_column_value_trans[k + 1]);
					} else {
						i_name_destination = GetIndex(instance.value(k + 1), s_row_column_value_trans[k + 1]);
					}

					String s_name_source = i_name_source + "_" + j;
					String s_name_destination = i_name_destination + "_" + (k + 1);

					for (int i = 0; i < m_numClasses; ++i) {
						d_ClassValues[i] += m_MN.get_link_weight(s_name_source, s_name_destination, i);
					}
				}
			}
		} else {
			for (int j = 0; j < m_numAttributes - 1; j++) {
				int i_name_source, i_name_destination;
				if (instance.isMissing(j))
					continue;// test if the value is missing
				if (instance.attribute(j).isNominal()) {
					i_name_source = GetIndex(instance.stringValue(j), s_row_column_value_trans[j]);
				} else {
					i_name_source = GetIndex(instance.value(j), s_row_column_value_trans[j]);
				}
				if (instance.isMissing(j + 1))
					continue;// test if the value is missing
				if (instance.attribute(j + 1).isNominal()) {
					i_name_destination = GetIndex(instance.stringValue(j + 1), s_row_column_value_trans[j + 1]);
				} else {
					i_name_destination = GetIndex(instance.value(j + 1), s_row_column_value_trans[j + 1]);
				}

				String s_name_source = i_name_source + "_" + j;
				String s_name_destination = i_name_destination + "_" + (j + 1);

				for (int i = 0; i < m_numClasses; ++i) {
					// 计算一行不同属性的权重大小
					d_ClassValues[i] += m_MN.get_link_weight(s_name_source, s_name_destination, i);
					// 计算每个属性权重大小
					d_2DClassvalue[j][i] = m_MN.get_link_weight(s_name_source, s_name_destination, i);
				}
			}
		}

		for (int j = 0; j < m_numAttributes - 1; j++) {
			d_col_total = 0.0;
			// 计算权重值的总和
			for (int i = 0; i < m_numClasses; ++i) {
				d_col_total += d_2DClassvalue[j][i];
			}
			if (d_col_total == 0.0)
				continue;

			// 每一个类型的权值与这一行权值之和 的比例值之和
			for (int i = 0; i < m_numClasses; ++i)
				d_2ClassValues[i] += (float) d_2DClassvalue[j][i] / d_col_total;

		}
		
		/*

		// 所有属性的权值之和
		/*for (int i = 0; i < m_numClasses; ++i) {
			d_total += d_ClassValues[i];
		}*/

		/*
		if (d_total == 0.0) {
			return probs;
		}
		for (int i = 0; i < m_numClasses; ++i) {
			probs[i] = d_ClassValues[i] / d_total;
		}*/

		Utils.normalize(d_2ClassValues);
		// return probs;
		return d_2ClassValues;
	}

	public static void main(String[] argv) {
		System.out.println("我正在执行MN算法");
		try {
			System.out.println(Evaluation.evaluateModel(new RMN(), argv));
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Create a rule branching on this attribute.
	 *
	 * @param attr
	 *            the attribute to branch on
	 * @param data
	 *            the data to be used for creating the rule
	 * @exception Exception
	 *                if the rule can't be built successfully
	 */
	public OneRRule newRule(Attribute attr, Instances data) throws Exception {

		OneRRule r;

		// ... create array to hold the missing value counts
		int[] missingValueCounts = new int[data.classAttribute().numValues()];
		if (attr.isNominal()) {
			r = newNominalRule(attr, data, missingValueCounts);
		} else {
			r = newNumericRule(attr, data, missingValueCounts);
		}
		r.m_missingValueClass = Utils.maxIndex(missingValueCounts);
		if (missingValueCounts[r.m_missingValueClass] == 0) {
			r.m_missingValueClass = -1; // signal for no missing value class
		} else {
			r.m_correct += missingValueCounts[r.m_missingValueClass];
		}
		return r;
	}

	/**
	 * Create a rule branching on this nominal attribute.
	 *
	 * @param attr
	 *            the attribute to branch on
	 * @param data
	 *            the data to be used for creating the rule
	 * @param missingValueCounts
	 *            to be filled in
	 * @exception Exception
	 *                if the rule can't be built successfully
	 */
	public OneRRule newNominalRule(Attribute attr, Instances data, int[] missingValueCounts) throws Exception {

		// ... create arrays to hold the counts
		int[][] counts = new int[attr.numValues()][data.classAttribute().numValues()];

		// ... calculate the counts
		Enumeration enu = data.enumerateInstances();
		while (enu.hasMoreElements()) {
			Instance i = (Instance) enu.nextElement();
			if (i.isMissing(attr)) {
				missingValueCounts[(int) i.classValue()]++;
			} else {
				counts[(int) i.value(attr)][(int) i.classValue()]++;
			}
		}

		OneRRule r = new OneRRule(data, attr); // create a new rule
		for (int value = 0; value < attr.numValues(); value++) {
			int best = Utils.maxIndex(counts[value]);
			r.m_classifications[value] = best;
			r.m_correct += counts[value][best];
		}
		return r;
	}

	/**
	 * Create a rule branching on this numeric attribute
	 *
	 * @param attr
	 *            the attribute to branch on
	 * @param data
	 *            the data to be used for creating the rule
	 * @param missingValueCounts
	 *            to be filled in
	 * @exception Exception
	 *                if the rule can't be built successfully
	 */
	public OneRRule newNumericRule(Attribute attr, Instances data, int[] missingValueCounts) throws Exception {

		// ... can't be more than numInstances buckets
		int[] classifications = new int[data.numInstances()];
		double[] breakpoints = new double[data.numInstances()];

		// create array to hold the counts
		int[] counts = new int[data.classAttribute().numValues()];
		int correct = 0;
		int lastInstance = data.numInstances();

		// missing values get sorted to the end of the instances
		data.sort(attr);
		while (lastInstance > 0 && data.instance(lastInstance - 1).isMissing(attr)) {
			lastInstance--;
			missingValueCounts[(int) data.instance(lastInstance).classValue()]++;
			//System.out.println("lastInstance : " + lastInstance);
			//System.out.println(Arrays.toString(missingValueCounts));
		}
		int i = 0;
		int cl = 0; // index of next bucket to create
		int it;
		while (i < lastInstance) { // start a new bucket
			for (int j = 0; j < counts.length; j++) {
				counts[j] = 0;
			}
			do { // fill it until it has enough of the majority class
				it = (int) data.instance(i++).classValue();
				counts[it]++;
			} while (counts[it] < m_minBucketSize && i < lastInstance);
			// while class remains the same, keep on filling
			while (i < lastInstance && (int) data.instance(i).classValue() == it) {
				counts[it]++;
				i++;
			}
			while (i < lastInstance && // keep on while attr value is the same
					(data.instance(i - 1).value(attr) == data.instance(i).value(attr))) {
				counts[(int) data.instance(i++).classValue()]++;
			}
			// 找出属性值最多的那一列
			for (int j = 0; j < counts.length; j++) {
				if (counts[j] > counts[it]) {
					it = j;
				}
			}
			if (cl > 0) { // can we coalesce with previous class?
				if (counts[classifications[cl - 1]] == counts[it]) {
					it = classifications[cl - 1];
				}
				if (it == classifications[cl - 1]) {
					cl--; // yes!
				}
			}
			correct += counts[it];
			classifications[cl] = it;
			if (i < lastInstance) {
				breakpoints[cl] = (data.instance(i - 1).value(attr) + data.instance(i).value(attr)) / 2;
			}
			cl++;
		}
		if (cl == 0) {
			throw new Exception("Only missing values in the training data!");
		}
		OneRRule r = new OneRRule(data, attr, cl); // new rule with cl branches
		r.m_correct = correct;

		// add
		r.m_buckets = new double[cl + 1];
		r.m_buckets[0] = data.instance(0).value(attr);
		//
		for (int v = 0; v < cl; v++) {
			r.m_classifications[v] = classifications[v];
			if (v < cl - 1) {
				r.m_breakpoints[v] = breakpoints[v];
				//
				r.m_buckets[v + 1] = breakpoints[v];
				//
			}
			//
			r.m_buckets[cl] = data.instance(lastInstance - 1).value(attr);
			//
		}
		return r;
	}
}
