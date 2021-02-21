package attributeChange;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

public class NMN {
     public NMN() throws Exception{
    	    //�趨����ֵ
    		boolean tag=false;
    		//ʵ����BestInstant����
    		BestInstant BI=new BestInstant();
    		//�Ӵ��̶����ļ�
    		File inputFile=BI.readFile();
    		//�ж��ļ��Ϸ���
    		while(!inputFile.exists()){
    		   inputFile=BI.readFile();
    		}
    		
    	
    		//C:\\�ȴ�У��\\Surveillance.csv
    		String name=inputFile.getName().split("\\.")[1];
    		
    		Instances instancesTrain=null;
    		switch(name){
    		   case "arff":
    			    //�������ݼ�����
    	    		ArffLoader atf = new ArffLoader();
    	    		//��������
    	    		atf.setFile(inputFile);
    	            //��ȡ���ݼ�
    	            instancesTrain = atf.getDataSet();  
    			   break;
    		   case "csv" :
    			     //�������ݼ�����
    			     CSVLoader loader = new CSVLoader();
    			     //�����ļ�
    			     loader.setSource(inputFile);
    			     //��ȡ���ݼ�
    			     instancesTrain = loader.getDataSet();
    			    // System.out.println(instancesTrain);
    			     break;
    		 
    		}
    	
            //��ʼ��BI
            BI.findBestInstant(instancesTrain);
            //����ȫ���У��������ݼ�,ѵ��
    	    BI.permutation(BI.AttributeString, BI.fv, BI.attributes,0);
    	    //���ڴ��л�ȡ��õ����ݼ�
    		Instances best=BI.returnBestInstances();
    		//���÷������������кţ���һ��Ϊ0
            best.setClassIndex(best.numAttributes()-1);
            //����������
            Classifier classifier1;
            // mn�㷨
            classifier1 = (Classifier) Class.forName("weka.classifiers.MN.M_N").newInstance();
            //�������ݼ�
            classifier1.buildClassifier(best);
            //������������
            Evaluation eval = new Evaluation(best);
            //����  
            System.out.println("���ղ���");
            eval.crossValidateModel(classifier1, best,10,new Random(1));
            //�õ����
            double correctrate=1-eval.errorRate();
            //eval.numInstances();
            //������
    		System.out.println(correctrate);
    	
     }     
	
	public static void main(String[] args) throws Exception {
                  NMN nmn=new NMN();
                //C:\\�ȴ�У��\\data_banknote_authentication.arff
                //C:\\�ȴ�У��\\nursery.arff
          		//C:\\�ȴ�У��\\abalone.arff
          		
          		//
          		//C:\\�ȴ�У��\\cmc.arff
          		
                  //C:\\�ȴ�У��\\weather.arff 3 6 7 10 12
                  
	//C:\\Users\\name\\Desktop\\1.arff
                  //C:\\Users\\name\\Desktop\\2.arff  C:\\2.csv
	
	
                //C:\\�ȴ�У��\\weather.arff
                //E:\\�ȴ�У��\\balloons.arff
	
	
	
	
	
	
	
	
	
}
}
