package share;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
 
/**
 * ѹ���㷨��
 * ʵ���ļ�ѹ�����ļ���ѹ�����Լ��ļ����ļ��еĻ��ѹ��
 * 
 *
 */
public class CompressAlgorithm {
 
	/**
	 * ��ɵĽ���ļ�--�����ѹ���ļ�
	 */
	File targetFile;
	
	public CompressAlgorithm() {}
	
	public CompressAlgorithm(File target)throws IllegalArgumentException {
		targetFile = target;
		if (targetFile.exists()) {
			throw new IllegalArgumentException(target.getPath()+" already exists");
		}
	}
 
	/**
	 * ѹ���ļ�
	 * 
	 * @param srcfile
	 */
	public void zipFiles(File srcfile) {
 
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(targetFile));
			
			if(srcfile.isFile()){
				zipFile(srcfile, out, "");
			} else{
				File[] list = srcfile.listFiles();
				for (int i = 0; i < list.length; i++) {
					compress(list[i], out, "");
				}
			}
			
			System.out.println("ѹ�����");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("failed to compress");
		} finally {
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
 
	/**
	 * ѹ���ļ�������ļ�
	 * �����֪�����ļ������ļ���--- ͳһ���ø÷���
	 * @param file
	 * @param out
	 * @param basedir
	 */
	private void compress(File file, ZipOutputStream out, String basedir) {
		/* �ж���Ŀ¼�����ļ� */
		if (file.isDirectory()) {
			this.zipDirectory(file, out, basedir);
		} else {
			this.zipFile(file, out, basedir);
		}
	}
 
	/**
	 * ѹ�������ļ�
	 * 
	 * @param srcfile
	 */
	public void zipFile(File srcfile, ZipOutputStream out, String basedir) {
		if (!srcfile.exists())
			return;
 
		byte[] buf = new byte[1024];
		FileInputStream in = null;
 
		try {
			int len;
			in = new FileInputStream(srcfile);
			out.putNextEntry(new ZipEntry(basedir + srcfile.getName()));
 
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null)
					out.closeEntry();
				if (in != null)
					in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
 
	/**
	 * ѹ���ļ���
	 * @param dir
	 * @param out
	 * @param basedir
	 */
	public void zipDirectory(File dir, ZipOutputStream out, String basedir) {
		if (!dir.exists())
			return;
 
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			/* �ݹ� */
			compress(files[i], out, basedir + dir.getName() + "/");
		}
	}
 
	
	//����
	public static void main(String[] args) {
		File f = new File("E:/Study/Java");
		new CompressAlgorithm(new File( "D:/test",f.getName()+".zip")).zipFiles(f);
	}
 
}
