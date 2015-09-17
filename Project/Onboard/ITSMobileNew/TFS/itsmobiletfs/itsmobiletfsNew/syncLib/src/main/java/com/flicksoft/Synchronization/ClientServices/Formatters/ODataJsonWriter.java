package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.flicksoft.Synchronization.ClientServices.IOfflineEntity;
import com.flicksoft.util.Base64Coder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;


public class ODataJsonWriter extends SyncWriter{
	
	public ODataJsonWriter(URI serviceUri) {
		super(serviceUri);
	}
	public class DateSerializer implements JsonSerializer<Date> 
    {
		private long localToGmt(Date date)
		{
			long result = 0;
			 Calendar c = Calendar.getInstance();
			 int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);
	        int dstOffset = c.get(java.util.Calendar.DST_OFFSET);
	        
	        result = date.getTime() + zoneOffset + dstOffset;
	        
	        return result;
		}
	        
        public JsonElement serialize(Date date, Type typfOfT, JsonSerializationContext context)
        {
            if (date == null)
                return null;

            String dateStr = "/Date(" + localToGmt(date) + ")/";
            return new JsonPrimitive(dateStr);
        }
    }
	public static class ByteArraySerializer implements JsonSerializer<byte[]> {
        public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
                String encodedString = new String(com.flicksoft.util.Base64Coder.encodeLines(src));
                return new JsonPrimitive(encodedString);
        }
}

	public void StartFeed(boolean isLastBatch, byte[] serverBlob, ByteArrayOutputStream outputStream) {
		  
		     JsonWriter writer;
		     this.outputS = outputStream;
		     try {
		    	 writer = new JsonWriter(new OutputStreamWriter(outputS, "UTF-8"));
		    	 writer.beginObject();
					writer.name("root");
					writer.beginObject();
						writer.name("__sync");
						writer.beginObject();
						writer.name("moreChangesAvailable").value(isLastBatch);
						writer.name("serverBlob").value(serverBlob == null ? "" : Base64Coder.encodeLines(serverBlob));
						writer.endObject();
						writer.name("results");
						writer.beginArray();
						writer.endArray();	
					writer.endObject();
					writer.endObject();
					writer.close();
		     } catch (IOException e) {
		    	 e.printStackTrace();
		     }
		  
	  }

	@Override
	public void WriteItemInternal(IOfflineEntity live, String liveTempId,
			IOfflineEntity conflicting, String conflictingTempId, String desc,
			boolean isConflict, boolean emitMetadataOnly) {
		// TODO Auto-generated method stub
		live.ServiceMetadata.tempId = liveTempId;
		live.ServiceMetadata.type = live.getClass().getName();
		JsonReader jsonReader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(this.outputS.toByteArray())));
		JsonParser jp = new JsonParser();
		JsonObject root = jp.parse(jsonReader).getAsJsonObject();
		JsonArray resultsArray = root.get("root").getAsJsonObject().get("results").getAsJsonArray();
		Gson gson = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteArraySerializer()).registerTypeHierarchyAdapter(Date.class, new DateSerializer()).create();
		resultsArray.add(jp.parse(gson.toJson(live)));	
		Log.i("ODataJsonWriter", root.toString());
		this.outputS = new ByteArrayOutputStream();
		try {
			outputS.write(root.toString().getBytes());
			this.outputS.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("SyncLib", e.getMessage());
		}
	}

	@Override
	public void WriteFeed(OutputStream outputStream)  {
		// TODO Auto-generated method stub

	}
	

}
