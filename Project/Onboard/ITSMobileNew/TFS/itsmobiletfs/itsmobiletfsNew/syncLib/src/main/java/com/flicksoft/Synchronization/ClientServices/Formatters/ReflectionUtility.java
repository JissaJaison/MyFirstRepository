package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.util.*;
import android.util.*;
import java.lang.reflect.*;
import java.util.UUID;
import com.flicksoft.Synchronization.ClientServices.IOfflineEntity;
import com.flicksoft.Synchronization.ClientServices.OfflineEntityMetadata;
import com.flicksoft.util.*;
//import com.sun.dn.library.Microsoft.VisualBasic.CollectionSupport;

	/**
	*   <summary>
	*   Class that will use java Reflection to serialize and deserialize an Entity to Atom
	*   < summary>
	* 
	**/

class ReflectionUtility {
	//static Object _lockObject = new Object();
	static HashMap<String, Object[]> _stringToPropInfoMapping =  new HashMap< String, Object[] > ();
	static HashMap<String, Object[]> _stringToPKPropInfoMapping =  new HashMap< String, Object[] > ();
	static HashMap<String, Constructor<?>> _stringToCtorInfoMapping =  new HashMap< String,Constructor<?> > ();

	public static Object[] GetPropertyInfoMapping(java.lang.Class<?> type) throws Exception {
		Object[] props = _stringToPropInfoMapping.get(type.getName());
		if ( props == null ) {
			//synchronized (_lockObject) {
			{
				props = type.getFields();
				if ( props != null ) {
					
					 //* TODO [ type.GetProperties(BindingFlags.Public | BindingFlags.Instance) ] */;
					ArrayList<Field> nonMetaProps = new ArrayList<Field>();
					 for (Object theField: props)
					 {
						 String fieldName = ((Field)theField).getName();
						if (!fieldName.equals("ServiceMetadata"))
						{
							nonMetaProps.add((Field)theField);
							
						}
					 }
					 
					 _stringToPropInfoMapping.put(type.getName(),  nonMetaProps.toArray());
					//  Look for the fields marked with [Key()] Attribute
					 ArrayList<Field> keyFields = new ArrayList<Field>();
					 for (Object everyField : props)
					 {
						 if (((Field)everyField).getAnnotation(com.flicksoft.util.Key.class) != null)
						 {
							 keyFields.add((Field)everyField);
							 
						 }
					 }
					 if ( keyFields.size() == 0 ) {
							throw new Exception(String.format("Entity %s does not have the any property marked with the [KeyAttribute].", type.getName()));
						}
					 _stringToPKPropInfoMapping.put(type.getName(),  keyFields.toArray());
					
					//  Look for the constructor info
					Constructor<?> ctorInfo = type.getConstructor(null);
					if ( ctorInfo == null ) {
						throw new Exception(String.format("Type %s does not have a public parameterless constructor.", type.getName()));
					}
					_stringToCtorInfoMapping.put(type.getName(), ctorInfo);
				}
			}
		}
		return props;
	}

	/**
	*   <summary>
	*   Get the PropertyInfo array for all Key fields
	*   < summary>
	*   <param name="type">Type to reflect on< param>
	*   <returns>PropertyInfo[]< returns>
	**/
	public static Object[] GetPrimaryKeysPropertyInfoMapping(java.lang.Class<?> type) {
		Object[] props =  _stringToPKPropInfoMapping.get(type.getName());
		if ( props != null ) {
			try {
				GetPropertyInfoMapping(type);
				props = _stringToPKPropInfoMapping.get(type.getName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return props;
	}

	/**
	*   <summary>
	*   Build the OData Atom primary keystring representation
	*   < summary>
	*   <param name="live">Entity for which primary key is required< param>
	*   <returns>String representation of the primary key< returns>
	**/
	public static String GetPrimaryKeyString(IOfflineEntity live) {
		String builder = new String();
		String sep = "";
		Object[] props = ReflectionUtility.GetPrimaryKeysPropertyInfoMapping(live.getClass());
		for (int forEachVar0 = 0; forEachVar0 < props.length; forEachVar0++) {
			Field keyInfo = (Field) props[forEachVar0];
			try
			{
			if ( keyInfo.getType() == FormatterConstants.GuidType ) {
				builder = String.format("%s %s=guid'%s'", sep, keyInfo.getName(), keyInfo.get(live));
			} else if ( keyInfo.getType() == FormatterConstants.StringType ) {
				builder = String.format("%s %s='%s'", sep, keyInfo.getName(), keyInfo.get(live));
			}
			 else {
				 builder = String.format("%s %s=%s", sep, keyInfo.getName(), keyInfo.get(live)) ;
			}
			}
			catch (Exception ex)
			{
			
			}
			if ( sep == null || sep == "" ) {
				sep = ", ";
			}
		}
		return "" + builder;
	}

	public static IOfflineEntity GetObjectForType(EntryInfoWrapper wrapper, ArrayList<java.lang.reflect.Type> knownTypes) throws Exception {
		java.lang.Class<?> entityType = null;
		 Constructor<?> ctorInfo = _stringToCtorInfoMapping.get(wrapper.TypeName);
		//  See if its cached first.
		if ( ctorInfo == null ) {
			//  Its not cached. Try to look for it then in list of known types.
			if ( knownTypes != null ) {
				for (int i = 0; i < knownTypes.size(); i++)
				{
					String fullName = ((Class<?>)knownTypes.get(i)).getName(); 
				    if ( fullName.equals( wrapper.TypeName))
				    {
				    	entityType = (Class<?>)knownTypes.get(i);
				    	break;
				    }
				}
				
				if ( entityType == null ) {
					throw new Exception(String.format("Unable to find a matching type for entry '{0}' in list of KnownTypes.", wrapper.TypeName)) ;
				}
			}
			 else {
				 Class<?> cls = Class.forName(wrapper.TypeName);
				 if (cls != null)
				 {
					Class<?>[] interfaces = cls.getInterfaces();
					int foundTimes = 0;
					for (int i = 0; i < interfaces.length; i++)
					{
					    if (interfaces[i].getName().equals("IOfflineEntity"))
					    {
					    	foundTimes++;
					    	entityType = interfaces[i];
					    }
					}
					if (foundTimes != 1)
					{
						entityType = null;
					}
				 }
				 //  Try to look for the type in the list of all loaded assemblies.
				
				if ( entityType == null ) {
					throw  new Exception(String.format("Unable to find a matching type for entry '%s' in the loaded assemblies. Specify the type name in the KnownTypes argument to the SyncReader instance.", wrapper.TypeName));
				}
			}
			//  Reflect this entity and get necessary info
			ReflectionUtility.GetPropertyInfoMapping(entityType);
			 ctorInfo = _stringToCtorInfoMapping.get(wrapper.TypeName);
		}
		 else {
			entityType = ctorInfo.getDeclaringClass();
		}
		//  Invoke the ctor
		Object obj = ctorInfo.newInstance((Object[])null);
		//  Set the parameters only for non tombstone items
		if ( !(wrapper.IsTombstone) ) {
			Object[] props = GetPropertyInfoMapping(entityType);
			for (int forEachVar0 = 0; forEachVar0 < props.length; forEachVar0++) {
				Field pinfo = (Field) props[forEachVar0];
				String value = wrapper.PropertyBag.get(pinfo.getName());
				try
				{
					pinfo.set(obj, GetValueFromType((java.lang.Class<?>)pinfo.getType(), value));
				}
				catch(Exception ex)
				{
					Log.w("Error:", wrapper.TypeName + "->" + pinfo.getName() + " has an invalid value:" + value);
					//ex.pri
				}
				//}
			}
		}
		IOfflineEntity entity =  (IOfflineEntity)obj ;
		 entity.ServiceMetadata = new OfflineEntityMetadata(wrapper.IsTombstone, wrapper.Id, wrapper.ETag, wrapper.EditUri);
		return entity;
	}

	private static Object GetValueFromType(java.lang.Class<?> type, String value) throws Exception {
		//Class<?> genericType = Class.forName(type.getName());
		if ( value == null ) {

			 if  (!type.isPrimitive()) {
				 if ( FormatterConstants.StringType == type) {
						return "";
				} else
					{
				return null;
					}
			}
			 else {
				//  Error case. Value cannot be null for a non nullable primitive type
				return null;
			}
		}
		else if (value == "")
		{
			if ( FormatterConstants.StringType == type  ) {
				return value;
			}
			else
			{
				return null;
			}
		}
//		if ( type != null ) {
//			 Constructor<?>[] allConstructors = type.getConstructors();
//			 type = (Class<?>)allConstructors[0].getGenericParameterTypes()[0];
//			 
//		}
		if ( FormatterConstants.StringType == type  ) {
			return value;
		} else if ( FormatterConstants.ByteArrayType == type ) {
			return Base64Coder.encodeString(value) ;
		} else if ( FormatterConstants.GuidType == type ) {
			return  UUID.fromString(value);
		} else if ( FormatterConstants.DateTimeType == type ||
                FormatterConstants.TimeSpanType == type ) {
			return FormatterUtilities.ParseDateTimeFromString(value, type);
		} else if ( FormatterConstants.ByteType == type ) {
			return  Byte.parseByte(value);
		} else if ( FormatterConstants.ShortType == type ) {
			return  Short.parseShort(value);
		} else if ( FormatterConstants.IntType == type  ||
                FormatterConstants.IntObjectType == type) {
			return  Integer.parseInt(value);
		} else if ( FormatterConstants.LongType == type ||
                FormatterConstants.LongObjectType == type ) {
			return  Long.parseLong(value);
		} else if ( FormatterConstants.BoolType == type ) {
			return  Boolean.parseBoolean(value);
		} else if ( FormatterConstants.CharType == type ) {
			return value ;
		} else if ( FormatterConstants.DoubleType == type ||
                FormatterConstants.FloatType == type || FormatterConstants.DoubleObjectType == type ||
                FormatterConstants.FloatObjectType == type)  {
			if (value == "0")
			{
				return 0;
			}
			else {
				try {
					
				       return Double.parseDouble(value);
				     } catch (NumberFormatException e) {
				        return 0;
				}
			
			}
		}
		else
		{
			return value;
		}
	}
}