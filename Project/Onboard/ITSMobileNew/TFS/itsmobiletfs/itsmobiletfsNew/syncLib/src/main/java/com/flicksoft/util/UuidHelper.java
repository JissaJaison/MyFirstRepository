package com.flicksoft.util;

import java.util.UUID;
import java.io.*;

/**
* UUID helper class
*
*/
public class UuidHelper {
	public static byte[] getGuidAsByteArray(UUID theID) throws IOException{
		ByteArrayOutputStream ba = new ByteArrayOutputStream(16);
		DataOutputStream da = new DataOutputStream(ba);
		da.writeLong(theID.getMostSignificantBits());
		da.writeLong(theID.getLeastSignificantBits());
		return ba.toByteArray();
	}

}