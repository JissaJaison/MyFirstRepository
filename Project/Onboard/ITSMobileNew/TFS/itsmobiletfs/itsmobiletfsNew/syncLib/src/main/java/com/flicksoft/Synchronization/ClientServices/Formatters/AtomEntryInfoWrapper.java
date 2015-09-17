package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.util.*;
import org.w3c.dom.*;
import java.net.*;
import com.flicksoft.util.*;

	/**
	* Class converted from .NET code
	**/

class AtomEntryInfoWrapper extends EntryInfoWrapper {
	/**
	* // TODO [ entry/&gt ]
	* // TODO [ or a &lt ]
	* // TODO [ deleted-entry/&gt ]
	**/

	

	public AtomEntryInfoWrapper(XmlReader reader) {
		super(reader);
	}

	/**
	*   <summary>
	*   Looks for a sync:syncConflict or an sync:syncError element
	*   < summary>
	*   <param name="entry">entry element< param>
	 * @throws Exception 
	**/
	protected void LoadConflictEntry(XmlReader parser) throws Exception {
		 boolean foundElement= parser.getComplexElementByTagName(FormatterConstants.SyncConlflictElementName) ;
		 
		 
		if ( foundElement) {
			//  Its an conflict
			this.IsConflict = true;
			//  Make sure it has an sync:conflictResolution element
			String resolutionType = parser.getElementValueByTagName(FormatterConstants.ConflictResolutionElementName);
			if ( resolutionType == null ) {
				throw new Exception("Conflict resolution not specified for entry element " + this.TypeName);
			}
			this.ConflictDesc =  resolutionType;
			
			//jump over conflictingChange
			parser.Read();
			parser.Read();
			this.ConflictWrapper = new AtomEntryInfoWrapper(parser);
			
			
			
			return;
		}
		
		foundElement = parser.getComplexElementByTagName(FormatterConstants.SyncNamespace + FormatterConstants.SyncErrorElementName);
		if ( foundElement ) {
			//  Its not an conflict
			this.IsConflict = false;
			//  Make sure it has an sync:errorDescription element
			String errorDesc = parser.getElementValueByTagName(FormatterConstants.SyncNamespace + FormatterConstants.ErrorDescriptionElementName);
			if (  errorDesc  != null && errorDesc != "" ) {
				this.ConflictDesc =  errorDesc;
			}
			foundElement = parser.getComplexElementByTagName(FormatterConstants.SyncNamespace + FormatterConstants.ErrorEntryElementName);
			if (!foundElement ) {
				throw new Exception("errorInChange not specified for syncError element " + this.TypeName);
			}
			this.ConflictWrapper = new AtomEntryInfoWrapper(parser);
		}
	}

	/**
	*   <summary>
	*   Inspects all m.properties element in the entry element to load all properties.
	*   < summary>
	*   <param name="entry">Entry element< param>
	 * @throws Exception 
	**/
	protected void LoadEntryProperties(XmlReader reader) throws Exception {
		
		if ( reader.getNamespaceURI().equals(FormatterConstants.AtomDeletedEntryNamespace))  {
			//  Read the tombstone
			this.IsTombstone = true;
			String id = reader.getElementValueByTagName(FormatterConstants.AtomReferenceElementName);
			if (  id  != null ) {
				this.Id =  id;
			}
			if (this.Id == null || this.Id == "" ) {
				//  No atom:id element was found in the tombstone. Throw.
				throw new Exception("A atom:ref element must be present for a tombstone entry. Entity in error: " + FormatterConstants.AtomReferenceElementName);
			}
			boolean tagFound = reader.MovetoTag(FormatterConstants.AtomPubCategoryElementName);
			if (tagFound)
			{
				
					this.TypeName = reader.getValue();
				
			}
			
			if ( this.TypeName == null || this.TypeName == "" ) {
				throw  new Exception(
	                    String.format("category element not found in {0} element.", (this.IsTombstone)? FormatterConstants.AtomDeletedEntryElementName : FormatterConstants.AtomPubEntryElementName)) ;
			}
			//for the syncConflict
			if (reader.Read())
			{
				if (reader.getLocalName().equals(FormatterConstants.SyncConlflictElementName ))
				{
					LoadConflictEntry(reader);
				}
			}
				
		}
		 else {

			//  Read ETag if present
			String etag = reader.getAttributeValue(FormatterConstants.ODataMetadataNamespace, FormatterConstants.EtagElementName) ;
			if ( etag  != null && etag != "" ) {
				this.ETag = etag;
			}
			//  Read TempId if present
			String theTag, theValue;
			for (int i = 0 ; i< 2; i++)
			{
				if (reader.Read())
				{
					theValue = reader.getValue();
				    if (FormatterConstants.TempIdElementName.equals(reader.getLocalName()))
				    {
				    	 this.TempId = theValue;
				    }
				    else if (FormatterConstants.AtomPubIdElementName.equals(reader.getLocalName()))
				    {
				    	 this.Id = theValue;
				    } 
			    
			    
				}
			}
			
			if  (this.Id == null || this.Id.equals( "" )) {
				//  No atom:id or sync:tempid element was found. Throw.
				throw  new Exception("A atom:id or a sync:tempId element must be present. Entity in error: " + FormatterConstants.AtomPubIdElementName);
			}
			//  Read EditUri if present
			boolean found =  reader.MovetoTag( FormatterConstants.AtomPubLinkElementName);
			if (found)
			{
				String value = reader.getAttributeValue(FormatterConstants.AtomNamespaceUri, FormatterConstants.AtomPubEditLinkAttributeName);
				if ( this.EditUri != null ) {
					//  Found duplicate edit urls in payload. Throw.
					throw new Exception(String.format("Multiple Edit Url's found for atom with {0}: '{1}'", (this.Id == null) ? "TempId" : "Id", (this.Id == null) ? this.TempId : this.Id)) ;
				}
				String hrefValue = reader.getAttributeValue(FormatterConstants.AtomNamespaceUri, FormatterConstants.AtomPubHrefAttrName);
				if (hrefValue == null || hrefValue.equals(""))
				{
//                    throw new Exception(String.format("No href attribute found in the edit link for atom with  %s: %s",
//                            (this.Id == null) ? "TempId" : "Id", (this.Id == null) ? this.TempId : this.Id)) ;					
				}
				if (value != null && !value.equals(""))
				{
					this.EditUri = new URI(value);
				}
			
			}
			//load category
			LoadTypeName(reader);
			
			//load content
			
			found =  reader.MovetoTag( FormatterConstants.AtomPubContentElementName);
			if ( found ) {
				 found = reader.MovetoTag(FormatterConstants.PropertiesElementName);
				 
				  if (found)
				 {
					while (reader.MovetoNextSubElementOf( FormatterConstants.PropertiesElementName))
					{
						String attrValue = reader.getAttributeValue(FormatterConstants.ODataMetadataNamespace, FormatterConstants.AtomPubIsNullElementName);
						if (attrValue != null && attrValue == "true")
						{
							this.PropertyBag.put(reader.getLocalName(), null);
						}
						else
						{
							this.PropertyBag.put(reader.getLocalName(), reader.getValue());
						}
					}
				 }
				 
			}

		}
	}

	/// <summary>
    /// Looks for either a &lt;entry/&gt; or a &lt;deleted-entry/&gt; subelement within the outer element.
    /// </summary>
    /// <param name="entryElement">The outer entry element</param>
    /// <returns>The inner entry or the deleted-entry subelement</returns>
    private XmlReader GetSubElement(XmlReader reader)
    {
        String element = reader.getElementValueByTagName(FormatterConstants.AtomPubEntryElementName);
        if (element == null)
        {
        	element = reader.getElementValueByTagName(FormatterConstants.AtomDeletedEntryElementName);
        }
        return reader;
    }
    
	/**
	*   <summary>
	*   Looks for the category element in the entry for the type name
	*   < summary>
	*   <param name="entry">Entry element< param>
	 * @throws Exception 
	**/
	protected void LoadTypeName(XmlReader reader) throws Exception {
		boolean isTombstone = reader.getNamespaceURI().equalsIgnoreCase(FormatterConstants.AtomDeletedEntryNamespace);
		
		 
		boolean tagFound = reader.MovetoTag(FormatterConstants.AtomPubCategoryElementName);
		if (tagFound)
		{
			if ( isTombstone ) {
				this.TypeName = reader.getValue();
			}
			 else {
				 int count = reader.parser.getAttributeCount();
				this.TypeName = reader.parser.getAttributeValue(null, FormatterConstants.AtomPubTermAttrName);
			}
		}
		
		if ( this.TypeName == null || this.TypeName == "" ) {
			throw  new Exception(
                    String.format("category element not found in {0} element.", (isTombstone)? FormatterConstants.AtomDeletedEntryElementName : FormatterConstants.AtomPubEntryElementName)) ;
		}
	}
}