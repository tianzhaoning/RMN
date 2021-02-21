package attributeChange;

import java.io.File;
import java.io.BufferedReader;    
import java.io.FileReader;
import java.util.ArrayList;    
import java.io.BufferedWriter;    
import java.io.File;    
import java.io.FileNotFoundException;    
import java.io.FileWriter;    
import java.io.IOException;   

public class Process {
	public void TestRead (String FilePath) {
		try {
    		
            BufferedReader reader = new BufferedReader(new FileReader(FilePath));
            
            //������һ����Ϣ  
            reader.readLine();
            String line = null;
            line = reader.readLine();
            String item[] = line.split(",");
            
            //��¼Jref���������ʱ���ڴ����ļ���
            String JrefName = item[0]; 
            String path = "D:\\ResearchData\\Result\\" + JrefName + "_result.csv" ;
           

            //����������list,���������Ϣ
            ArrayList<String> Year = new ArrayList<>();
            ArrayList<Integer> ISSNCount = new ArrayList<>(); 
            ArrayList<Integer> ALLCount = new ArrayList<>();
            Year.add(item[1]);
            int ISSNNum = Integer.parseInt(item[2]);
            int ALLNum = Integer.parseInt(item[3]);
            ISSNCount.add(ISSNNum);
            ALLCount.add(ALLNum);
            
            //��ȡԭ����
            while((line=reader.readLine()) != null){    
                 String row[] = line.split(",");	//CSV��ʽ�ļ�Ϊ���ŷָ����ļ�������ÿһ�а������з�  
     
                 ISSNNum = Integer.parseInt(row[2]);
                 ALLNum = Integer.parseInt(row[3]);
                 Year.add(row[1]);
                 ISSNCount.add(ISSNNum);
                 ALLCount.add(ALLNum);
            }
            //����Ӱ��������
            File StorePath = new File(path); 
            // �½�һ��CSV�����ļ�   
            BufferedWriter bw = new BufferedWriter(new FileWriter(StorePath,false));
            bw.write("Year,ImpactFactor");
            for (int i = 2; i < Year.size(); i++) {
            	double ImpactFactor = (double)(ALLCount.get(i-1) + ALLCount.get(i-2))/(ISSNCount.get(i-1) + ISSNCount.get(i-2));
            	String res = Year.get(i) + "," + ImpactFactor;
            	bw.newLine(); 
            	bw.write(res);    
                  
            }
            //��ջ��������ر�������
            bw.flush();
            bw.close();
        }
    	catch (FileNotFoundException e){

            System.out.println("û���ҵ�ָ���ļ�");

        }catch (IOException e){

            System.out.println("�ļ���д����");

        }
    	catch (Exception e) {    
             e.printStackTrace();    
         }
	}
	public static void main(String[] args) {
		String FolderPath = "D:\\ResearchData\\SubTable";
		File TargetFolder = new File(FolderPath);
		String[] FileList = TargetFolder.list();
		Process a = new Process();
		int count = 0;
		for (int i = 0; i < FileList.length; i++) {
			String FilePath = FolderPath + "\\" + FileList[i];
			a.TestRead(FilePath);
			count ++;
		}
		System.out.println("һ��������"+ count +"�ļ�");
	}
}

