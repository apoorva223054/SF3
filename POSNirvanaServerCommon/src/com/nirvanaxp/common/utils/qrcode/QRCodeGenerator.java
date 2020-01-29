/**
 * Copyright (c) 2012 - 2017 by NirvanaXP, LLC. All Rights reserved. Express
 * written consent required to use, copy, share, alter, distribute or transmit
 * this source code in part or whole through any means physical or electronic.
 **/
package com.nirvanaxp.common.utils.qrcode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.nirvanaxp.common.utils.ConfigFileReader;

import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

public class QRCodeGenerator
{

	public static boolean generateQRCode(String text, String path, String fileName) throws IOException
	{

		ByteArrayOutputStream out = QRCode.from(text).to(ImageType.PNG).stream();

		boolean isPathAvailable = ConfigFileReader.makeDirectories(path);

		if (isPathAvailable)
		{
			FileOutputStream fout = new FileOutputStream(new File(path + fileName + ".png"));

			fout.write(out.toByteArray());

			fout.flush();
			fout.close();
		}
		else
		{
			return false;
		}

		return true;
	}
}
