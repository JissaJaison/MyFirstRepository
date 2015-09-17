package com.flicksoft.util;

import java.io.IOException;
import java.io.InputStream;




import org.xmlpull.v1.*;

  


import java.lang.reflect.Type;
import java.io.*;
import java.util.*;

import org.w3c.dom.Element;

import android.util.Log;
/**
 * a "sequential" xml parser
 *
 */
public class XmlReader {
	public XmlPullParser parser = null;
	static XmlReader instance = null;
	private InputStream is = null;
	public static XmlReader Create(InputStream stream) {
		
		if (instance == null)
		{
			instance = new XmlReader();
		}
		
		
		instance.is = stream;
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			instance.parser = factory.newPullParser();
			instance.parser.setFeature(org.xmlpull.v1.XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			InputStreamReader st = new InputStreamReader(instance.is);
			instance.parser.setInput(st);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return instance;
	}

    public boolean IsEndDocument()
    {
    	return (getNodeType() == XmlPullParser.END_DOCUMENT);
    }
    
	public void MoveToContent() {

		int eventType;
		try {
			try {
				parser.next();
				
				eventType = parser.getEventType();
				
				while (eventType == XmlPullParser.COMMENT ||
						eventType == XmlPullParser.DOCDECL ||
						eventType == XmlPullParser.IGNORABLE_WHITESPACE ||
						eventType == XmlPullParser.PROCESSING_INSTRUCTION)
				{
					try {
						parser.nextToken();
						eventType = parser.getEventType();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			Log.e("SyncLib", e.getMessage());
		}
		// TODO Auto-generated method stub
		
	}

	public short getNodeType() {
		try {
			return (short) parser.getEventType();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public boolean MovetoTag(String tag) {
		
		boolean result = false; 
		try {
			if (parser.getEventType() == XmlPullParser.START_TAG
			        && parser.getName().equals(tag))
			{
				result = true;
			}
			else
			{
				try {
			
				
				
				 int eventType = parser.next();
				 //if tag not found, continue until the end of document 
					while (eventType != XmlPullParser.END_DOCUMENT )
					{
						try {
							String curName = parser.getName();
							if (eventType == XmlPullParser.START_TAG
									&& curName.equals(tag))
							{
							    result = true;
							    break;
							}
							else
							{
								parser.next();
								
								eventType = parser.getEventType();	
							}
							
							
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							eventType = XmlPullParser.END_DOCUMENT;
							break;
						}
						
					}

					
			} catch (XmlPullParserException e) {
				
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			}
		} catch (XmlPullParserException e1) {
			// TODO Auto-generated catch block
			
			e1.printStackTrace();
	
		}
		
		
		
			
			
		return result;
	}
	public String getElementValueByTagName(String tag) {
		String result = null;
		try {
			String curText = parser.getName();
			if (parser.getEventType() == XmlPullParser.START_TAG
			        && curText.equals(tag))
			{
				
				result = parser.nextText();
			}
			else if (parser.nextTag() == XmlPullParser.START_TAG
			        && parser.getName().equals(tag))
			{
				result = parser.nextText();
			}
			else
			{
				result = null;
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	public boolean getComplexElementByTagName(String tag) {
		try {
			if (parser.getEventType() == XmlPullParser.START_TAG
			        && parser.getName().equals(tag))
			{
				return true;
			}
			if (parser.nextTag() == XmlPullParser.START_TAG
			        && parser.getName().equals(tag))
			{
				return true;
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public String getLocalName() {
		return parser.getName();

	}

	public String getNamespaceURI() {
		// TODO Auto-generated method stub
		return parser.getNamespace();
	}


	public String getValue()
	{
		try {
			return parser.nextText();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public Boolean ReadElementContentAsBoolean() {
		// TODO Auto-generated method stub
		//pullParser.
		return Boolean.parseBoolean(getValue());
	}

	public String ReadElementContentAsString() {
		// TODO Auto-generated method stub
		//pullParser.
		return getValue();
	}
	public void Skip() {
		try {
			parser.nextTag();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		
	}
	
	public boolean MovetoNextSubElementOf(String tag) {
		// TODO Auto-generated method stub
		boolean result = false;
		String name = null;
		try {
			
			parser.next();
			 int eventType =parser.getEventType();
			 name = parser.getName();
				while (eventType != XmlPullParser.END_DOCUMENT )
				{

					if (eventType == XmlPullParser.START_TAG)
					{
						result = true;
						break;
					}
					else if (eventType == XmlPullParser.END_TAG  && tag.equals(name ))
					{
						result = false;
						break;
					}
					else
					{
						try {
							parser.next();
							
							eventType = parser.getEventType();
							
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							result = false;
							break;
						}
					}
				}

				
		} catch (XmlPullParserException e) {
			result = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result =false;
		}
		
		return result;
	}

	public boolean Read() {
		// TODO Auto-generated method stub
		boolean result = false;
		try {
			
			parser.next();
			 int eventType =parser.getEventType();
				while (eventType != XmlPullParser.END_DOCUMENT && eventType != XmlPullParser.START_TAG)
				{
					try {
						parser.next();
						
						eventType = parser.getEventType();
						
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						eventType = XmlPullParser.END_DOCUMENT;
						break;
					}
					
				}
				if (eventType == XmlPullParser.END_DOCUMENT)
				{
					result = false;
				}
				else
				{
					result = true;
				}

				
		} catch (XmlPullParserException e) {
			result = false;
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result =false;
		}
		
		return result;
	}



	public String getAttributeValue(String namespace, String name) {
		return parser.getAttributeValue(namespace, name);
	}
	public String getAttributeValue(int index) {
		return parser.getAttributeValue(index);
	}
	public boolean getAttributeValueAsBoolean(String namespace, String name) {
		String value = parser.getAttributeValue(namespace, name);
		return Boolean.parseBoolean(value);
	}

	

}
