package com.uvsq.cloud;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CloudGenerator
 */
public class CloudGenerator extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CloudGenerator() {
        super();

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		
		String gitLink = request.getParameter("appgit");
		String appname = request.getParameter("appname");
		
	
		//generating the Docker file using the data 
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("FROM ubuntu:latest\n");
		sb.append("MAINTAINER Islam OUAHOUDA <iouahouda@gmail.com>\n");
		sb.append("RUN apt-get update\n");
		sb.append("RUN apt-get -y upgrade\n");
		sb.append("RUN apt-get -y install apache2\n");
		sb.append("RUN apt-get -y install git\n");
		sb.append("RUN git clone "+ gitLink + " \n");
		sb.append("RUN cp -R ./"+ appname + " /var/www/html\n");
		sb.append("ENV APACHE_RUN_USER www-data\n");
		sb.append("ENV APACHE_RUN_GROUP www-data\n");
		sb.append("ENV APACHE_LOG_DIR /var/log/apache2\n");
		sb.append("ENV APACHE_LOCK_DIR /var/lock/apache2\n");
		sb.append("ENV APACHE_PID_FILE /var/run/apache2.pid\n");
		sb.append("EXPOSE 80\n");
		sb.append("CMD /usr/sbin/apache2ctl -D FOREGROUND\n");

		String dockerFile = sb.toString();
		
		//Running the file generated using Process class !
		//the app is running on /home/islam/eclipse/jee-2018-12/eclipse
		Process process = Runtime.getRuntime().exec("mkdir /home/islam/Docker-Images/"+appname+"/");

		
		String appPwd;
		appPwd="/home/islam/Docker-Images/"+ appname +"/Dockerfile";
		BufferedWriter writer = new BufferedWriter(new FileWriter(appPwd));
		writer.write(dockerFile);
		writer.close();
		
		process = Runtime.getRuntime().exec("docker build -t islamouahouda/"+ appname +" /home/islam/Docker-Images/"+appname);
		
		//sudo docker build -t islamouahoudaWithapp2/apache .
		//sudo docker images
		
		StringBuilder output = new StringBuilder();

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			output.append(line + "\n");
		}
		
		
		process = Runtime.getRuntime().exec("sudo docker run -d -p 8081:80 islamouahouda/"+ appname);
		
		//sudo docker build -t islamouahoudaWithapp2/apache .
		//sudo docker images
		
		StringBuilder output2 = new StringBuilder();

		BufferedReader reader2 = new BufferedReader(new InputStreamReader(process.getInputStream()));

		String line2;
		while ((line2 = reader2.readLine()) != null) {
			output2.append(line2 + "\n");
		}
		

        request.setAttribute("dockerOutput",output.toString());
        request.setAttribute("dockerFile", dockerFile);
        request.setAttribute("dockerOutputRun", output2.toString());
        request.getRequestDispatcher("/dockerGenerator.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
