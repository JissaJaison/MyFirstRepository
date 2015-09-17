package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.util.*;
import java.lang.reflect.*;
import java.text.*;
	/**
	* contains utility functions used by formatter
	**/

class FormatterUtilities {

	public static String GetEdmType(java.lang.Class type) {
		TypeVariable[] types  = type.getTypeParameters();
		if (types.length!=0) {
			return GetEdmType(types[0].getClass());
		}
		String result="";
		String typeName = type.getSimpleName();
		if (typeName.equalsIgnoreCase("boolean")){
			return "Edm.Boolean";
		}
		else if (typeName.equalsIgnoreCase( "String") || typeName.equalsIgnoreCase("char")){
			return "Edm.String";
		}
		else if (typeName.equalsIgnoreCase("Date")){
			return "Edm.DateTime";
		}
		else if (typeName.equalsIgnoreCase("BigDecimal")){
			return "Edm.Decimal";
		}
		else if (typeName.equalsIgnoreCase("double")){
			return "Edm.Double";
		}
		else if (typeName.equalsIgnoreCase("short")){
			return "Edm.Int16";
		}
		else if (typeName.equalsIgnoreCase("int")){
			return "Edm.Int32";
		}
		else if (typeName.equalsIgnoreCase("long")){
			return "Edm.Int64";
		}
		else if (typeName.equalsIgnoreCase("Byte[]")){
			return "Edm.Binary";
		}
		else if (typeName.equalsIgnoreCase("UUID")){
			return "Edm.Guid";
		}
		else if (typeName.equalsIgnoreCase("float")){
			return "Edm.Single";
		}
		else if (typeName.equalsIgnoreCase("byte")){
			return "Edm.Byte";
		}
		else{
			return "";
			//throw new Exception("TypeCode " + typeName + " is not a supported type.");
		}

		
	}

	/**
	*   <summary>
	*   Looks at passed in Type and calls the appropriate Date functions for Atom
	*   < summary>
	*   <param name="objValue">Actual value< param>
	*   <param name="type">Type coverting from< param>
	*   <returns>Atom representation< returns>
	**/
	public static Object ConvertDateTimeForType_Atom(Object objValue, java.lang.Class type) {
		if ( type == com.flicksoft.Synchronization.ClientServices.Formatters.FormatterConstants.DateTimeType ) {
			return ConvertDateTimeToAtom((java.util.Date) objValue);
		}
		return  ConvertTimeToAtom((Date)objValue);
	}

	/**
	*   <summary>
	*   Converts DateTime to OData Atom format as specified in http:  www.odata.org developers protocols atom-format#PrimitiveTypes for DateTime
	*   Format is"yyyy-MM-ddThh:mm:ss.fffffff"
	*   <param name="date">DateTime to convert< param>
	*   < summary>
	*   <returns>Atom representation of DateTime< returns>
	**/
	public static String ConvertDateTimeToAtom(java.util.Date date) {
		SimpleDateFormat format =
            new SimpleDateFormat(FormatterConstants.AtomDateTimeLexicalRepresentation);


		
		return format.format(date) ;
	}

	/**
	*   <summary>
	*   Converts a TimeSpan to OData atom format as specified in http:  www.odata.org developers protocols atom-format#PrimitiveTypes for Time
	*   Actual lexical representation is time'hh:mm:ss.fffffff'
	*   < summary>
	*   <param name="t">Timespan to convert< param>
	*   <returns>Atom representation of Timespan< returns>
	**/
	public static String ConvertTimeToAtom(Date t) {
		SimpleDateFormat format =
            new SimpleDateFormat("hh:mm:ss.fffffff");


		
		return format.format(t) ;

	}

	static Object ParseDateTimeFromString(String value, java.lang.Class type) {
         return ParseAtomString(value, type);

	}

	private static Object ParseAtomString(String value, java.lang.Class type) {
		Object result = null;
		try
		{
		SimpleDateFormat format =
            new SimpleDateFormat(FormatterConstants.AtomDateTimeLexicalRepresentation);
		
		
		result = format.parseObject(value);
		}
		catch (Exception ex)
		{
		}
		return result;
//		if ( FormatterConstants.DateTimeType.IsAssignableFrom(type) ) {
//			
//			return /* TODO [ XmlConvert.ToDateTime(value, FormatterConstants.AtomDateTimeLexicalRepresentation) ] */;
//		}
//		 else {
//			//  Its a TimeSpan
//			return /* TODO [ TimeSpan.Parse(value) ] */;
//		}
	}
}