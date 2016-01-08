package com.wy.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WeChatServlet extends HttpServlet {  
	private static final long serialVersionUID = 612703862391250168L;

	public WeChatServlet() {  
		super();  
	}  

	public void doGet(HttpServletRequest request, HttpServletResponse response)  
			throws ServletException, IOException {  
		System.out.println("doGet start");
		request.setCharacterEncoding("UTF-8");  
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires", 0); 
        
		PrintWriter out = response.getWriter();
		try {  
			// ����ַ���  
			String echostr = request.getParameter("echostr"); 
	        //�ж��Ƿ���΢�Ž��뼤����֤��ֻ���״ν�����֤ʱ�Ż��յ�echostr��������ʱ��Ҫ����ֱ�ӷ���
			if (echostr != null && echostr.length() > 1) {  
				System.out.println("echostr != null");
				// ΢�ż���ǩ��  
				String signature = request.getParameter("signature");  
				// ʱ���  
				String timestamp = request.getParameter("timestamp");  
				// �����  
				String nonce = request.getParameter("nonce");  
				// ͨ������signature���������У�飬��У��ɹ���ԭ������echostr����ʾ����ɹ����������ʧ��  
				if (SignUtil.checkSignature(signature, timestamp, nonce)) {  
					System.out.println("checkSignature success");
					out.print(echostr);  
				} else {
					System.out.println("checkSignature fail");
				}
	        } else {
	        	System.out.println("echostr == null");
	        	 //��ȡ���յ���xml��Ϣ
	            StringBuffer sb = new StringBuffer();  
	            InputStream is = request.getInputStream();  
	            InputStreamReader isr = new InputStreamReader(is, "UTF-8");  
	            BufferedReader br = new BufferedReader(isr);  
	            String s = "";  
	            while ((s = br.readLine()) != null) {  
	                sb.append(s);  
	            }  
	            String xml = sb.toString(); //�μ�Ϊ���յ�΢�Ŷ˷��͹�����xml���� 
	            System.out.println("from:"+xml);
	            //������΢�Ŵ�������  
	            String result = new WeChatProcess().processWeChatMsg(xml);
	            System.out.println(result);
	            out.print("to:"+result); 
	        }
			out.close();  
			out = null;  
			System.out.println("out close");
		} catch (Exception e) {  
			e.printStackTrace();
			if (out != null) {
				out.close();
				out = null;
			}
		} finally {
			System.out.println("touch finally");
		}
	}  

	public void doPost(HttpServletRequest request, HttpServletResponse response)  
			throws ServletException, IOException {  
		doGet(request, response);
	} 
	
	public static void writeLog(String str)
    {
        try
        {
        String path="testfilelog.log";
        File file=new File(path);
        if(!file.exists())
            file.createNewFile();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FileOutputStream out=new FileOutputStream(file,false); //���׷�ӷ�ʽ��true        
        StringBuffer sb=new StringBuffer();
        sb.append("-----------"+sdf.format(new Date())+"------------\n");
        sb.append(str+"\n");
        out.write(sb.toString().getBytes("utf-8"));//ע����Ҫת����Ӧ���ַ���
        out.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getStackTrace());
        }
    } 
}