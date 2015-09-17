package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.lang.reflect.*;
import java.util.*;
import javax.xml.transform.*;
import org.w3c.dom.*;

import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.*;
import javax.xml.datatype.DatatypeConstants.Field;
import javax.xml.namespace.*;
import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import com.flicksoft.Synchronization.ClientServices.*;
import com.flicksoft.util.*;
	/**
	*   <summary>
	*    SyncWriter implementation for the OData Atompub format
	*   < summary>
	* 
	**/

public class ODataAtomWriter extends SyncWriter {
	Document  _document;
	Element _root;

	public ODataAtomWriter(URI serviceUri) {
		super(serviceUri);
	}

	  public static String now() {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(FormatterConstants.AtomDateTimeLexicalRepresentation);
	    return sdf.format(cal.getTime());

	  }
	/**
	*   <summary>
	*   Should be called prior to any Items are added to the stream. This ensures that the stream is
	*   set up with the right doc level feed parameters
	*   < summary>
	*   <param name="isLastBatch">Whether this feed will be the last batch or not.< param>
	*   <param name="serverBlob">Sync server blob.< param>
	**/
	public void StartFeed(boolean isLastBatch, byte[] serverBlob, ByteArrayOutputStream out) {
		 super.StartFeed(isLastBatch, serverBlob, out);
	        DocumentBuilderFactory factory
	          = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
				DOMImplementation impl = builder.getDOMImplementation();
				
				_document =  impl.createDocument(null,null,null);
				//  Add namespace prefixes
				String baseNs = this._baseUri.toString();
				String  atom = FormatterConstants.AtomXmlNamespace;
				_root =  _document.createElement(FormatterConstants.AtomPubFeedElementName);
				
				_root.setAttribute("xmlns:" + "base", baseNs );
				_root.setAttribute(FormatterConstants.AtomPubXmlNsPrefix, FormatterConstants.AtomXmlNamespace );
				_root.setAttribute("xmlns:" + FormatterConstants.ODataDataNsPrefix, FormatterConstants.ODataDataNamespace);
				_root.setAttribute("xmlns:" + FormatterConstants.ODataMetadataNsPrefix, FormatterConstants.ODataMetadataNamespace);
				_root.setAttribute("xmlns:" + FormatterConstants.AtomDeletedEntryPrefix, FormatterConstants.AtomDeletedEntryNamespace);
				_root.setAttribute("xmlns:" + FormatterConstants.SyncNsPrefix, FormatterConstants.SyncNamespace);
				//  Add atom title element
				Element tempElem = _document.createElement(FormatterConstants.AtomPubTitleElementName );

				tempElem.setTextContent("");
				_root.appendChild(tempElem);
				
				//  Add id element
				tempElem = _document.createElement(FormatterConstants.AtomPubIdElementName);

				tempElem.setTextContent("{" + UUID.randomUUID().toString() + "}");
				_root.appendChild(tempElem);
				
				//  Add atom updated element
				tempElem = _document.createElement(FormatterConstants.AtomPubUpdatedElementName);

				tempElem.setTextContent(now());
				_root.appendChild(tempElem);

				//  add atom link element
				tempElem = _document.createElement(FormatterConstants.AtomPubLinkElementName);
				tempElem.setAttribute(FormatterConstants.AtomPubRelAttrName, "self");
				tempElem.setAttribute(FormatterConstants.AtomPubHrefAttrName, "");

				
				_root.appendChild(tempElem);
					

				//  Add the is last batch sync extension
				tempElem = _document.createElement("sync:" + FormatterConstants.MoreChangesAvailableText);
				
				tempElem.setTextContent(new Boolean(!isLastBatch).toString());
				_root.appendChild(tempElem);

				//  Add the serverBlob sync extension
				tempElem = _document.createElement("sync:" + FormatterConstants.ServerBlobText);
				tempElem.setTextContent((serverBlob != null) ? Base64Coder.encodeLines(serverBlob) : "");
				_root.appendChild(tempElem);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        


		

	}

	/**
	*   <summary>
	*   Called by the runtime when all entities are written and contents needs to flushed to the underlying stream.
	*   < summary>
	*   <param name="writer">XmlWriter to which this feed will be serialized to< param>
	 * @throws Exception 
	**/
	public void WriteFeed(OutputStream outputStream) throws Exception {
		if ( outputStream == null ) {
			throw  new Exception("writer");
		}
		
		_document.appendChild(_root);
		   // Use a Transformer for output
		  TransformerFactory tFactory =
		    TransformerFactory.newInstance();
		  Transformer transformer = tFactory.newTransformer();

		  DOMSource source = new DOMSource(_document);
		  StreamResult result = new StreamResult(outputStream);
		  //StreamResult result = new StreamResult(new StringWriter());
		  transformer.transform(source, result);
		  //String xmlString = result.getWriter().toString();
	}

	/**
	*   <summary>
	*   Adds an IOfflineEntity and its associated Conflicting Error entity as an Atom entry element
	*   < summary>
	*   <param name="live">Live Entity< param>
	*   <param name="liveTempId">TempId for the live entity< param>
	*   <param name="conflicting">Conflicting entity that will be sent in synnConflict or syncError extension< param>
	*   <param name="conflictingTempId">TempId for the conflicting entity< param>
	*   <param name="desc">Error description or the conflict resolution< param>
	*   <param name="isConflict">Denotes if its an errorElement or conflict. Used only when <paramref name="desc" > is not null< param>
	*   <param name="emitMetadataOnly">Bool flag that denotes whether a partial metadata only entity is to be written< param>
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws DOMException 
	**/
	public void WriteItemInternal(IOfflineEntity live, String liveTempId, IOfflineEntity conflicting, String conflictingTempId, String desc, boolean isConflict, boolean emitMetadataOnly)  {
		Element entryElement;
		try {
			entryElement = WriteEntry(live, liveTempId, emitMetadataOnly);
			_root.appendChild(entryElement);
		if ( conflicting != null ) {
			Element conflictElement = _document.createElement(FormatterConstants.SyncNamespace + ((isConflict) ? FormatterConstants.SyncConlflictElementName : FormatterConstants.SyncErrorElementName));
			//  Write the confliction resolution or errorElement.
			conflictElement.appendChild(_document.createElementNS(FormatterConstants.SyncNamespace + ((isConflict) ? FormatterConstants.ConflictResolutionElementName : FormatterConstants.ErrorDescriptionElementName), desc)) ;
			//  Write the confliction resolution or errorElement.
			// TODO [ 
                Element conflictingEntryElement = _document.createElement(FormatterConstants.SyncNamespace + ((isConflict) ? FormatterConstants.ConflictEntryElementName : FormatterConstants.ErrorEntryElementName)) ;
                _root.appendChild(entryElement);
			 try {
				conflictingEntryElement.appendChild(WriteEntry(conflicting, conflictingTempId, false/*emitPartial*/));
				conflictElement.appendChild(conflictingEntryElement) ;
				entryElement.appendChild(conflictElement);
			} catch (DOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		} catch (DOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

	/**
	*   <summary>
	*   Writes the <entry > tag and all its related elements.
	*   < summary>
	*   <param name="live">Actual entity whose value is to be emitted.< param>
	*   <param name="tempId">The temporary Id if any< param>
	*   <param name="emitPartial">Bool flag that denotes whether a partial metadata only entity is to be written< param>
	*   <returns>XElement representation of the entry element< returns>
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws DOMException 
	**/
	private Element WriteEntry(IOfflineEntity live, String tempId, boolean emitPartial) throws DOMException, IllegalArgumentException, IllegalAccessException {
		String typeName = live.getClass().getName();
		Element tempElem;
		Attr tempAttr ;
		if ( !(live.ServiceMetadata.IsTombstone) ) {
			Element entryElement = _document.createElement(FormatterConstants.AtomPubEntryElementName); 
			//  Add Etag
			if ( !(live.ServiceMetadata.Etag == null || live.ServiceMetadata.Etag == "")) {
				tempAttr = _document.createAttribute(FormatterConstants.ODataMetadataNsPrefix +":" + FormatterConstants.EtagElementName);
				tempAttr.setValue(live.ServiceMetadata.Etag);
				entryElement.appendChild(tempAttr);
			}
			//  Add TempId element
			if ( !(tempId == null || tempId == "") ) {
				tempElem = _document.createElement(FormatterConstants.SyncNsPrefix +":" + FormatterConstants.TempIdElementName);
				tempElem.setTextContent(tempId);
				entryElement.appendChild(tempElem);
			}
			//  Add Id element
			tempElem = _document.createElement(FormatterConstants.AtomPubIdElementName);
			tempElem.setTextContent((live.ServiceMetadata.ID == null || live.ServiceMetadata.ID == "") ? "" : live.ServiceMetadata.ID);
			entryElement.appendChild(tempElem);
			
			//  Add title element
			tempElem = _document.createElement( FormatterConstants.AtomPubTitleElementName);
			
			tempAttr = _document.createAttribute(FormatterConstants.AtomPubTypeElementName);
			tempAttr.setValue("text");
			tempElem.setAttributeNode(tempAttr);
			
			entryElement.appendChild(tempElem);
			
			


			//  Add updated element
			tempElem = _document.createElement(FormatterConstants.AtomPubUpdatedElementName);
			tempElem.setTextContent(now().toString());
			entryElement.appendChild(tempElem);

			//  Add author element
			tempElem = _document.createElement(FormatterConstants.AtomPubAuthorElementName);
			entryElement.appendChild(tempElem);
			tempElem = _document.createElement( FormatterConstants.AtomPubNameElementName);
			entryElement.appendChild(tempElem);
			//  Write the <link> element
			
			tempElem = _document.createElement( FormatterConstants.AtomPubLinkElementName);
			tempAttr = _document.createAttribute(FormatterConstants.AtomPubRelAttrName);
			tempAttr.setValue(FormatterConstants.AtomPubEditLinkAttributeName);
			tempElem.setAttributeNode(tempAttr);
			
			
			tempAttr = _document.createAttribute(FormatterConstants.AtomPubTitleElementName);
			tempAttr.setValue(typeName);
			tempElem.setAttributeNode(tempAttr);
			
			tempAttr = _document.createAttribute(FormatterConstants.AtomPubHrefAttrName);
			tempAttr.setValue((live.ServiceMetadata.EditUri != null) ? live.ServiceMetadata.EditUri.toString() : "");
			tempElem.setAttributeNode(tempAttr);
			
			entryElement.appendChild(tempElem);

			//  Write the <category> element
			tempElem = _document.createElement(FormatterConstants.AtomPubCategoryElementName);
			tempAttr = _document.createAttribute(FormatterConstants.AtomPubTermAttrName);
			tempAttr.setValue(typeName);
			tempElem.setAttributeNode(tempAttr);
			tempAttr = _document.createAttribute(FormatterConstants.AtomPubSchemaAttrName);
			tempAttr.setValue(FormatterConstants.ODataSchemaNamespace);
			tempElem.setAttributeNode(tempAttr);
			entryElement.appendChild(tempElem);
			
			Element contentElement = _document.createElement(FormatterConstants.AtomPubContentElementName);
			if ( !(emitPartial) ) {
				//  Write the entity contents
				 contentElement.appendChild(WriteEntityContents(live)) ;
			}
			//  Add the contents entity to the outer entity.
			entryElement.appendChild(contentElement);
			return entryElement;
		}
		 else {
			//  Write the at:deleted-entry tombstone element
			Element tombstoneElement = _document.createElement(FormatterConstants.AtomDeletedEntryPrefix + ":" + FormatterConstants.AtomDeletedEntryElementName);
			tempElem = _document.createElement(FormatterConstants.AtomReferenceElementName);
			tempElem.setTextContent(live.ServiceMetadata.ID);
			tombstoneElement.appendChild(tempElem);
			/* TODO [ tombstoneElement.Add(new XElement(, )) ] */;
			
			tempElem = _document.createElement(FormatterConstants.SyncNsPrefix + ":" + FormatterConstants.AtomPubCategoryElementName);
			tempElem.setTextContent(typeName);
			tombstoneElement.appendChild(tempElem);
			//tombstoneElement.appendChild(tempElem);
			return tombstoneElement;
		}
	}

	/**
	*   <summary>
	*   This writes the public contents of the Entity in the properties element.
	*   < summary>
	*   <param name="entity">Entity< param>
	*   <returns>XElement representation of the properties element< returns>
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	**/
	Element WriteEntityContents(IOfflineEntity entity) throws IllegalArgumentException, IllegalAccessException {
		Element tempElem;
		Attr tempAttr;
		Element contentElement = _document.createElement(FormatterConstants.ODataMetadataNsPrefix + ":" + FormatterConstants.PropertiesElementName) ;
		//  Write only the primary keys if its an tombstone
		 Object[] properties;
		try {
			properties = ReflectionUtility.GetPropertyInfoMapping(entity.getClass());
			
		//  Write individual properties to the feed,
			for (int forEachVar0 = 0; forEachVar0 <  properties.length; forEachVar0++) {
				java.lang.reflect.Field fi = (java.lang.reflect.Field)properties[forEachVar0];
				String edmType = FormatterUtilities.GetEdmType(fi.getType());
				Object value = fi.get(entity);
				java.lang.Class<?> propType = fi.getType();
				Type tempType = fi.getGenericType();
				
				if(tempType instanceof ParameterizedType){
				    ParameterizedType aType = (ParameterizedType) tempType;
				    Type[] fieldArgTypes = aType.getActualTypeArguments();
				    propType = (java.lang.Class<?>)fieldArgTypes[0];
				}
				
				
				if ( value == null ) {
					//contentElement.Add
					tempElem = _document.createElement(FormatterConstants.ODataDataNsPrefix + ":" +  fi.getName());
					tempAttr = _document.createAttribute(FormatterConstants.ODataMetadataNsPrefix + ":" +  FormatterConstants.AtomPubTypeElementName );
					tempAttr.setNodeValue(edmType.toString());
					tempElem.appendChild(tempAttr);
					tempAttr = _document.createAttribute(FormatterConstants.ODataMetadataNsPrefix + ":" +  FormatterConstants.AtomPubIsNullElementName );
					tempAttr.setNodeValue("true");	
					tempElem.appendChild(tempAttr);
					contentElement.appendChild(tempElem);
					//		_document. XAttribute(FormatterConstants.ODataMetadataNamespace + FormatterConstants.AtomPubIsNullElementName, true))) ] */;
				} else if ( propType ==  FormatterConstants.DateTimeType) {
					
					tempElem = _document.createElement(FormatterConstants.ODataDataNsPrefix + ":" +  fi.getName());
					tempAttr = _document.createAttribute(FormatterConstants.ODataMetadataNsPrefix + ":" + FormatterConstants.AtomPubTypeElementName );
					tempAttr.setNodeValue(edmType.toString());
					tempElem.setAttributeNode(tempAttr);
					
					String dateString = FormatterUtilities.ConvertDateTimeToAtom((Date)value);
					//tempAttr.setNodeValue(FormatterUtilities.ConvertDateTimeToAtom((Date)value));	
					tempElem.setTextContent(dateString);
					contentElement.appendChild(tempElem);
					
				} else if ( propType != FormatterConstants.ByteArrayType ) {
	                            
	            				tempElem = _document.createElement(FormatterConstants.ODataDataNsPrefix + ":" + fi.getName());
	            				tempAttr = _document.createAttribute(FormatterConstants.ODataMetadataNsPrefix + ":" + FormatterConstants.AtomPubTypeElementName );
	            				tempAttr.setNodeValue(edmType.toString());
	            				tempElem.setAttributeNode(tempAttr);
	            				tempElem.setTextContent(value.toString());
	            				contentElement.appendChild(tempElem);
				}
				 else {
					 byte[] bytes = (byte[])value;
	 				tempElem = _document.createElement(FormatterConstants.ODataDataNsPrefix + ":" + fi.getName());
					tempAttr = _document.createAttribute(FormatterConstants.ODataMetadataNsPrefix + ":" + FormatterConstants.AtomPubTypeElementName );
					tempAttr.setNodeValue(edmType.toString());
					tempElem.setAttributeNode(tempAttr);
					tempElem.setTextContent(com.flicksoft.util.Base64Coder.encodeLines(bytes));
					contentElement.appendChild(tempElem);
					
					
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return contentElement;
	}
}