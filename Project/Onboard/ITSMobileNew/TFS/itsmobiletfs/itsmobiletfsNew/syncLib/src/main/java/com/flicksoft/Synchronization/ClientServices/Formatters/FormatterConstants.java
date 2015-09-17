package com.flicksoft.Synchronization.ClientServices.Formatters;

import java.util.*;
import java.lang.reflect.*;
import javax.xml.namespace.*;

import com.flicksoft.Synchronization.ClientServices.SyncConflictResolution;

import android.R.bool;
	/**
	*  static final ants needed by formatter
	**/

class FormatterConstants {
	public  static final  String JsonDocumentElementName = "root";
    public  static final  String JsonRootElementName = "d";
    public  static final  String JsonTypeAttributeName = "type";
    public  static final  String JsonSyncMetadataElementName = "__sync";
    public  static final  String JsonSyncConflictElementName = "__syncConflict";
    public  static final  String JsonSyncErrorElementName = "__syncError";
    public  static final  String JsonSyncEntryMetadataElementName = "__metadata";
    public  static final  String JsonSyncResultsElementName = "results";
    public  static final  String JsonSyncEntryTypeElementName = "type";
    public  static final  String JsonSyncEntryUriElementName = "uri";
    //public static XNamespace JsonNamespace = XNamespace.Get("http://tempuri.org");

    public  static final  String JsonDateTimeFormat ="'/Date('S')/'";//"'/Date('yyyy-MM-dd'T'HH:mm:ss.SSSSSSS')/'"; 
    public  static final  String JsonTimeFormat = "time'{HH:mm:ss.SSSSSSS}'";
    public  static final  String JsonDateTimeOffsetFormat = "datetimeoffset'{0}'";
    public  static final  String JsonDateTimeOffsetLexicalRepresentation = "yyyy-MM-ddTHH:mm:ss.SSSSSSSZ";
    public  static final  String JsonDateTimeLexicalRepresentation = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS";
   // public static DateTime JsonDateTimeStartTime = new DateTime(1970, 1, 1); // Midnight of Jan 1 , 1970 as per OData Json standard
    public static long JsonNanoToMilliSecondsFactor = 10000;
    
	public static final String NullableTypeName = "Nullable`1";
	public static final String MoreChangesAvailableText = "moreChangesAvailable";
	public static final String ServerBlobText = "serverBlob";
	public static final String PluralizeEntityNameFormat = "{0}s";
 public static final String SyncConlflictElementName = "syncConflict";
 public static final String SyncErrorElementName = "syncError";
 public static final String ConflictEntryElementName = "conflictingChange";
 public static final String ErrorEntryElementName = "changeInError";
 public static final String ErrorDescriptionElementName = "errorDescription";
 public static final String ConflictResolutionElementName = "conflictResolution";
 public static final String IsDeletedElementName = "isDeleted";
 public static final String IsConflictResolvedElementName = "isResolved";
 public static final String TempIdElementName = "tempId";
 public static final String EtagElementName = "etag";
 public static final String EditUriElementName = "edituri";
 public static final String SingleQuoteString = "'";
 public static final String LeftBracketString = "(";
 public static final String RightBracketString = ")";

 public static final String ApplicationXmlContentType = "application/xml";
 public static final String PropertiesElementName = "properties";
 public static String AtomNamespaceUri = "http://www.w3.org/2005/Atom";

 public static String XmlNamespace = "http://www.w3.org/2000/xmlns/";
 public static final String SyncNsPrefix = "sync";
 public static String SyncNamespace = "http://odata.org/sync/v1";

 public static final String EdmxNsPrefix = "edmx";
 public static String EdmxNamespace = "http://schemas.microsoft.com/ado/2007/06/edmx";

 public static final String ODataMetadataNsPrefix = "m";
 public static String ODataMetadataNamespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/metadata";

 public static final String ODataDataNsPrefix = "d";
 public static String ODataDataNamespace = "http://schemas.microsoft.com/ado/2007/08/dataservices";
 public static String ODataSchemaNamespace = "http://schemas.microsoft.com/ado/2007/08/dataservices/schema";

 public static final String AtomDeletedEntryPrefix = "at";
 public static String AtomDeletedEntryNamespace = "http://purl.org/atompub/tombstones/1.0";

 public static final String AtomPubFeedElementName = "feed";
 public static final String AtomPubEntryElementName = "entry";
 public static final String AtomPubTitleElementName = "title";
 public static final String AtomPubIdElementName = "id";
 public static final String AtomPubContentElementName = "content";
 public static final String AtomPubCategoryElementName = "category";
 public static final String AtomPubUpdatedElementName = "updated";
 public static final String AtomPubLinkElementName = "link";
 public static final String AtomPubTermAttrName = "term";
 public static final String AtomPubSchemaAttrName = "schema";
 public static final String AtomPubRelAttrName = "rel";
 public static final String AtomPubHrefAttrName = "href";
 public static final String AtomPubXmlNsPrefix = "xmlns";
 public static final String AtomPubTypeElementName = "type";
 public static final String AtomPubIsNullElementName = "null";
 public static final String AtomPubAuthorElementName = "author";
 public static final String AtomPubNameElementName = "name";
 public static final String AtomPubEditLinkAttributeName = "edit";
 public static final String AtomDeletedEntryElementName = "deleted-entry";
 public static final String AtomReferenceElementName = "ref";
 public static final String AtomXmlNamespace = "http://www.w3.org/2005/Atom";

 public static final String AtomDateTimeOffsetLexicalRepresentation = "yyyy-MM-ddTHH:mm:ss.SSSSSSSZ";
 public static final String AtomDateTimeLexicalRepresentation = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSS";

 public static final  Type DateTimeType = Date.class;
 public static final Type TimeSpanType = Date.class;
 public static final Type ByteArrayType = byte[].class;
 public static final Type BoolType = bool.class;
 public static final Type FloatType = float.class;
 public static final Type FloatObjectType = Float.class;
 public static final Type DoubleType = double.class;
 public static final Type DoubleObjectType = Double.class;
 public static final Type GuidType = UUID.class;
 public static final Type StringType = String.class;
 public static final Type CharType = char.class;
 public static final Type ByteType = byte.class;
 public static final Type ShortType = short.class;
 public static final Type IntType = int.class;
 public static final Type IntObjectType = Integer.class;
 public static final Type LongType = long.class;
 public static final Type LongObjectType = Long.class;
 //public static final Type NullableType = typeof(Nullable<>);
 public static final Type SyncConflictResolutionType = SyncConflictResolution.class;

}