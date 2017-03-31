package com.heqing.netty.protocol.file;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class FileServerHandler extends SimpleChannelInboundHandler<String> {

	private static final String CR = System.getProperty("line.separator");

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.SimpleChannelInboundHandler#messageReceived(io.netty
	 * .channel.ChannelHandlerContext, java.lang.Object)
	 */
	public void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
		File file = new File(msg);
		if (file.exists()) {
			if (!file.isFile()) {
				ctx.writeAndFlush("Not a file : " + file + CR);
				return;
			}
			saveFile(file);
			ctx.write(file + " " + file.length() + CR);
			RandomAccessFile randomAccessFile = new RandomAccessFile(msg, "r");
			FileRegion region = new DefaultFileRegion(randomAccessFile.getChannel(), 0, randomAccessFile.length());
			ctx.write(region);
			ctx.writeAndFlush(CR);
			randomAccessFile.close();
		} else {
			ctx.writeAndFlush("File not found: " + file + CR);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.netty.channel.ChannelHandlerAdapter#exceptionCaught(io.netty.channel
	 * .ChannelHandlerContext, java.lang.Throwable)
	 */
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}
	
	public void saveFile(File beforefile) {
		try {
			//这是你要保存之后的文件，是自定义的，本身不存在
			File afterfile = new File("d://temp//"+beforefile.getName());
			//定义文件输入流，用来读取beforefile文件
			FileInputStream fis = new FileInputStream(beforefile);
			//定义文件输出流，用来把信息写入afterfile文件中
			FileOutputStream fos = new FileOutputStream(afterfile);
			//文件缓存区
			byte[] b = new byte[1024];
			//将文件流信息读取文件缓存区，如果读取结果不为-1就代表文件没有读取完毕，反之已经读取完毕
			while(fis.read(b)!=-1){
				//将缓存区中的内容写到afterfile文件中
				fos.write(b);
				fos.flush();
			}
			fos.close();
			fis.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
