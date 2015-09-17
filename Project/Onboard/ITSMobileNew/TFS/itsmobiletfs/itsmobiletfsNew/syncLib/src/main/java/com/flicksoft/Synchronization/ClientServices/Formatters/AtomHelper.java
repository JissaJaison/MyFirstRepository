package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xmlpull.v1.XmlPullParser;

import com.flicksoft.util.*;
	/**
	* contains the methods to help parse OData Atom feed
	**/

class AtomHelper {

	/**
	*   <summary>
	*   Check whether the XmlReader is currently at the start of an element with
	*   the given name in the Atom namespace
	*   < summary>
	*   <param name="reader">XmlReader to check on< param>
	*   <param name="name">Element name< param>
	*   <returns>True if the reader if at the indicated Atom element< returns>
	**/
	static boolean IsAtomElement(XmlReader curNode, String name) {
		//Node curNode = reader.getFirstChild();
		boolean result = false;
		boolean result1 = false;
		boolean result2 = false;
		boolean result3 = false;
		short nodeType = curNode.getNodeType();
		String localName = curNode.getLocalName();
		String namespace = curNode.getNamespaceURI();
		
		result1 = (nodeType == XmlPullParser.START_TAG); 
		result2 = localName.equals(name);
		result3 = namespace.equals(FormatterConstants.AtomNamespaceUri);
		
		result = result1 && result2 && result3;
		return result;
	}

	/**
	*   <summary>
	*   Check whether the XmlReader is currently at the start of an tombstone element with
	*   the given name in the Tombstone namespace
	*   < summary>
	*   <param name="reader">XmlReader to check on< param>
	*   <param name="name">Element name< param>
	*   <returns>True if the reader if at the indicated Atom element< returns>
	**/
	static boolean IsAtomTombstone(XmlReader curNode, String name) {
		return curNode.getNodeType() == XmlPullParser.START_TAG 
        	&& curNode.getLocalName().equals(name)
        	&&curNode.getNamespaceURI().equals(FormatterConstants.AtomDeletedEntryNamespace);
	}

	/**
	*   <summary>
	*   Check whether the XmlReader is currently at the start of an element
	*   in the Odata namespace
	*   < summary>
	*   <param name="reader">XmlReader to check on< param>
	*   <param name="ns">Element Namespace name< param>
	*   <returns>True if the reader if at the indicated namespace< returns>
	**/
	static boolean IsODataNamespace(XmlReader curNode,  String ns) {
		return curNode.getNodeType() == XmlPullParser.START_TAG 
			&&curNode.getNamespaceURI().equals(ns);
	}

	/**
	*   <summary>
	*   Check whether the XmlReader is currently at the start of an element with
	*   the given name in the sync namespace
	*   < summary>
	*   <param name="reader">XmlReader to check on< param>
	*   <param name="name">Element name< param>
	*   <returns>True if the reader if at the indicated anchor element< returns>
	**/
	static boolean IsSyncElement(XmlReader curNode, String name) {
		return curNode.getNodeType() == XmlPullParser.START_TAG 
    	&& curNode.getLocalName().equals(name)
    	&&curNode.getNamespaceURI().equals(FormatterConstants.SyncNamespace);
	}
}