package attributeChange;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;



import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.Loader;

public class AttributeChange {
    public  ArrayList<String> attr;//�������
    public  ArrayList<HashMap<String,Object>>data;//�������
    public void  initAttr(){
    	String n1="temperature";
    	String n2="outlook";
    	String n3="humidity";
    	String n4="windy";
    	String n5="play";
    	int i;
    	int j;
    	
    }
    public  void changeAttr(int i,int j){
          this.attr=new ArrayList();
          String altitude1=this.attr.get(j);
		  String altitude2=this.attr.get(i);
		  attr.set(j,altitude2);
		  attr.set(i,altitude1);
        
    }
	public  void getdata(String file) throws IOException{  //fileΪԴ��ַ
		  
		  //�Ӵ��̶����ļ�
		  File inputFile=new File(file);
		  ArffLoader loader=new ArffLoader();
	      loader.setSource(inputFile);
	      
	      
	      //��ȡ���ݼ�
	      Instances dataset=loader.getDataSet();
	      Instance inst;
	      int n=dataset.numInstances();
	      inst=loader.getNextInstance(dataset);
	      inst.numAttributes();
	      //������ȡ��������
	      while((inst=loader.getNextInstance(dataset))!=null){
	    	  HashMap<String,Object>map=new HashMap();//HashMap����������Ƽ���ֵ
	    	  for(int m=0;m<attr.size();m++){
	    		  String name=attr.get(m);//���Ե��ַ�������
	    		  Attribute attrname=dataset.attribute(name);//ͨ���ַ������ͱ��Attrbute����
	    		  Object obj=inst.value(attrname);//ͨ��Attribute���ͻ�ȡֵobj
	    		  map.put(name,obj);//����ָ�������ݽṹ
	    	  }
	    	  this.data.add(map);
	      }
			
	}
	
	public void createarff(String sourcefile,String targerfile) throws IOException{ //fileΪ��Ҫ��ŵĵ�ַ
		  
		//�Ӵ��̶����ļ�
		  File inputFile=new File(sourcefile);
		  ArffLoader loader=new ArffLoader();
	      loader.setSource(inputFile);
	    //��ȡ���ݼ�
	      Instances dataset=loader.getDataSet();
	      Instance inst;
	      //�������Լ�
	      FastVector  attributes =new FastVector();
	      //��Դ�ļ������Է������Լ�
	      for(int m=0;m<attr.size();m++){
    		  String name=attr.get(m);//���Ե��ַ�������
    		  Attribute attrname=dataset.attribute(name);//ͨ���ַ������ͱ��Attrbute����
    		  attributes.addElement(attrname);
    	  }
		  //�����µ����ݼ�
	       Instances instances=new Instances("Test",attributes ,0);
	      for(int i=0;i<data.size();i++){
	    	  HashMap<String,Object> map=data.get(i);
	    	  double[] obj=new double[dataset.numAttributes()];
	    	  for(int j=0;j<dataset.numAttributes();j++){
	    		  String key=attr.get(j);
	    		  obj[j]=(double) map.get(key);
	    		  
	    	   }
	    	  instances.add(new DenseInstance(1.0,obj));    	  
	    	  
	      }
	      //����arff
		
	}
	
	public static void main(String[] args) {
		
		AttributeChange at=new AttributeChange();
	    
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
