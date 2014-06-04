package org.openmrs.module.regadbintegration.web.servlet;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.openmrs.util.OpenmrsUtil;

/**
 * Servlet implementation class FileServlet
 */
public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public FileServlet() {
        // TODO Auto-generated constructor stub
    }
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		OutputStream out=response.getOutputStream();
        File f=new File(OpenmrsUtil.getApplicationDataDirectory()+File.separator+"report.rtf");
        FileInputStream fis=new FileInputStream(f);
        byte []b=new byte[(int)f.length()];
        fis.read(b);
        response.addHeader("Content-Disposition","attachment; filename=Report.rtf" );
        response.setContentType("application/rtf");
        response.setContentLength( (int) f.length() );
        for (int j = 0; j < b.length; j++) {
        out.write(b[j]);
        }
        fis.close();
        f.delete();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
