package in.flobizAPI.utils;


import org.testng.annotations.Test;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class ServerUtils {



		 Session session = null;
         Channel channel= null;

    	 public  FileReader excelFileReader;
    	 public  Properties excelFileProperties;
    	 
    public  ServerUtils() {
     	  
    	try {
    		excelFileReader = new FileReader("config.properties"); 
    		excelFileProperties = new Properties(); 
    		excelFileProperties.load(excelFileReader);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}		
    }
    	
         @Test
    public  void ReDeployBatchNovopay() throws JSchException {
    	connectSSH();
    	 
    	executeSSHCommand(excelFileProperties.getProperty("removeBatchNovopayCommand")); 
    	closeSSH();
    }
        public  void connectSSH() throws JSchException {
   
        java.util.Properties config = new java.util.Properties(); 
        config.put("StrictHostKeyChecking", "no");
        JSch jsch = new JSch();
        Session session=jsch.getSession(excelFileProperties.getProperty("serverUserName"), excelFileProperties.getProperty("serverHostName"), 22);
        session.setPassword(excelFileProperties.getProperty("serverPassword"));
        session.setConfig(config);
        session.connect(); // connected to the server
//        System.out.println("Connected");
        channel=session.openChannel("exec"); //
        }
        
        public  void executeSSHCommand(String removeBatchNovopayCommand)
        {
       try{  
        ((ChannelExec)channel).setCommand(removeBatchNovopayCommand);
        ((ChannelExec)channel).setPty(true);
        channel.setInputStream(null);
        ((ChannelExec)channel).setErrStream(System.err);
         
        InputStream in=channel.getInputStream();
        channel.connect();
        byte[] tmp=new byte[1024];
        while(true){
          while(in.available()>0){
            int i=in.read(tmp, 0, 1024);
            if(i<0)break;
            System.out.print(new String(tmp, 0, i));
          }
          if(channel.isClosed()){
        	  connectSSH();
//            System.out.println("exit-status: "+channel.getExitStatus());
            break;
          }
        }
    }catch(Exception e){
        e.printStackTrace();
    }
      
} 
        public  void closeSSH()
        {

            channel.disconnect();
            if(session!=null)
            session.disconnect();
//            System.out.println("Session closed");
        }
        

}