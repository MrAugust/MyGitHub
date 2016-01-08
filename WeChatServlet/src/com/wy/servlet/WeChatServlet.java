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
			// 随机字符串  
			String echostr = request.getParameter("echostr"); 
	        //判断是否是微信接入激活验证，只有首次接入验证时才会收到echostr参数，此时需要把它直接返回
			if (echostr != null && echostr.length() > 1) {  
				System.out.println("echostr != null");
				// 微信加密签名  
				String signature = request.getParameter("signature");  
				// 时间戳  
				String timestamp = request.getParameter("timestamp");  
				// 随机数  
				String nonce = request.getParameter("nonce");  
				// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
				if (SignUtil.checkSignature(signature, timestamp, nonce)) {  
					System.out.println("checkSignature success");
					out.print(echostr);  
				} else {
					System.out.println("checkSignature fail");
				}
	        } else {
	        	System.out.println("echostr == null");
	        	 //读取接收到的xml消息
	            StringBuffer sb = new StringBuffer();  
	            InputStream is = request.getInputStream();  
	            InputStreamReader isr = new InputStreamReader(is, "UTF-8");  
	            BufferedReader br = new BufferedReader(isr);  
	            String s = "";  
	            while ((s = br.readLine()) != null) {  
	                sb.append(s);  
	            }  
	            String xml = sb.toString(); //次即为接收到微信端发送过来的xml数据 
	            System.out.println("from:"+xml);
	            //正常的微信处理流程  
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
        FileOutputStream out=new FileOutputStream(file,false); //如果追加方式用true        
        StringBuffer sb=new StringBuffer();
        sb.append("-----------"+sdf.format(new Date())+"------------\n");
        sb.append(str+"\n");
        out.write(sb.toString().getBytes("utf-8"));//注意需要转换对应的字符集
        out.close();
        }
        catch(IOException ex)
        {
            System.out.println(ex.getStackTrace());
        }
    } 
}