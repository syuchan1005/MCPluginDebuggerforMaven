package com.github.syuchan1005.mcplugindebugger;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * Created by syuchan on 2017/07/4.
 */
@Mojo(
		name = "send",
		defaultPhase = LifecyclePhase.PACKAGE
)
public class PluginDebuggerMojo extends AbstractMojo {
	@Parameter(required = true)
	private String host;

	@Parameter(required = true)
	private int port;

	@Parameter(required = true)
	private String pluginName;

	@Parameter(required = true)
	private File jarFilePath;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (!jarFilePath.isFile()) {
			throw new MojoFailureException("`JarFilePath` isn't File");
		}
		Log log = getLog();
		try (Socket socket = new Socket(host, port);
			 DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())) {
			log.info("Create Socket");
			dataOutputStream.writeUTF(pluginName);
			log.info("Check Plugin");
			if (!socket.isClosed()) {
				log.info("Send File");
				FileInputStream fileInputStream = new FileInputStream(jarFilePath);
				byte[] buffer = new byte[512];
				int fLength;
				while ((fLength = fileInputStream.read(buffer)) > 0) {
					dataOutputStream.write(buffer, 0, fLength);
				}
				fileInputStream.close();
				log.info("Send Complete");
			}
			dataOutputStream.flush();
		} catch (Exception e) {
			throw new MojoFailureException(e.getMessage());
		}
	}

	public static boolean isNumber(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
