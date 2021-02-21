package attributeChange;

import java.io.File;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class FindBestInstant {
	    public double correctrate[];
	    public int testlength;
	    //��ʼ��
	    public void init(int length){
	    	this.testlength=length;
	    	this.correctrate=new double[this.testlength];
	    	for(int i=0;i<this.testlength;i++){
	    		correctrate[i]=0;
	    	}
	    }
	    public void testInstant(String file,int set,int filenumber) throws Exception{
	    	
	    	 //�Ӵ��̶���ѵ���ļ�
            File inputFile = new File(file);
            
            //����arff����
            ArffLoader atf = new ArffLoader();
            
            //arff����
            atf.setFile(inputFile);
            
            //��ȡ���ݼ�
            Instances instancesTrain = atf.getDataSet();
            
            //���÷������������кţ���һ��Ϊ0
            instancesTrain.setClassIndex(set);
            
            //����������
            Classifier classifier1;
            
            // mn�㷨
            classifier1 = (Classifier) Class.forName("weka.classifiers.MN.M_N").newInstance();
            
            //�������ݼ�
            classifier1.buildClassifier(instancesTrain);
            
            //������������
            Evaluation eval = new Evaluation(instancesTrain);
            
            //����
            eval.crossValidateModel(classifier1, instancesTrain,10,new Random(1));
            
            //���
            double correctrate=1-eval.errorRate();
	     	this.correctrate[filenumber]=correctrate;
	    }
	    
	    
	    //�ҵ���õ�,�ļ��Ŵ�1��ʼ,�����0��ʼ
	    public int findBest(){
	    	  double initnumber=0;
	    	  int set=0;
	    	  for(int i=0;i<this.testlength;i++){
	    		     if(this.correctrate[i]>initnumber){
	    		    	 initnumber=this.correctrate[i];
	    		    	 set=i;
	    		    	 
	    		    }
	    		
	    	  }
			return ++set;
	    	
	    }
	public static void main(String[] args) throws Exception {
		
		FindBestInstant fbi=new FindBestInstant();
		fbi.init(6);
		System.out.println(1111);
		fbi.testInstant("C:\\test\\4_result.arff", 0, 3);
		System.out.println(1111);
		for(int i=0;i<fbi.testlength;i++){
			System.out.print(fbi.correctrate[i]+" ");
			
			
		}
		System.out.println(fbi.findBest());
	}
	   
	  
        
	
}
