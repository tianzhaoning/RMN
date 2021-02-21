package attributeChange;

import java.io.IOException;

public class Testall {
     public static void main(String[] args) throws Exception {
    	    System.out.println("��ʼ");
    		String path="C:\\test\\grub-damage.arff";
        	Permutation pt=new Permutation();
        	pt.setLengthandSize(path);
        	pt.init(path);
        	pt.permutation(pt.attributes, 0);
        	
        	
        	
        	FindBestInstant fbi=new FindBestInstant();
        	
        	fbi.init(pt.total);
        	
    	    for(int i=1;i<pt.total+1;i++){
    	    	String filename="C:\\test\\"+i+"_result.arff";
    	    	System.out.print("���ڶ�ȡ"+filename);
    	    	int set=pt.confirmrow(filename);
    	    	
    	    	try{
    	        fbi.testInstant(filename, set, i-1);
    	            System.out.println("��"+i+"���ļ���������");
    	            
    	    	}catch(Exception e){
    	    		System.out.println("��"+i+"���ļ��޷�����");
    	    	   
    	    	}
    	        
    	    }
    	    int best=fbi.findBest();
     	    System.out.println("��õ��ļ���Ϊ"+best+"��ȷ��Ϊ"+fbi.correctrate[best-1]);
     	    System.out.println("Դ�ļ�����ȷ��Ϊ"+fbi.correctrate[0]);
    	 
    	 
	}
	
 
	
	
	
	
	
	
	
	
	
	
	
	
	
}
