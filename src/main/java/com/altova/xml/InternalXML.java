/**
 * InternalXML.java
 *
 * This file was generated by MapForce 2020r2.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the MapForce Documentation for further details.
 * http://www.altova.com/mapforce
 */


package com.altova.xml;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.altova.mapforce.IEnumerable;
import com.altova.mapforce.IEnumerator;
import com.altova.mapforce.IMFNode;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public abstract class InternalXML {
	public static String XMLToString( com.altova.mapforce.IMFNode node ) throws Exception {
		Document doc = XmlTreeOperations.createDocument();
		com.altova.xml.MFDOMWriter.write(new com.altova.mapforce.MFSingletonSequence(node), doc);
		return XmlTreeOperations.saveXml( doc, false, true );
	}

	public static String XMLToGroupingKey( com.altova.mapforce.IEnumerable e) throws Exception {
		StringBuffer sBuf = new StringBuffer();
		write(e, sBuf);
		return sBuf.toString();
	}
	
	public static String XMLToGroupingKey( com.altova.mapforce.IMFNode node) throws Exception {
		StringBuffer sBuf = new StringBuffer();
		write(new com.altova.mapforce.MFSingletonSequence(node), sBuf);
		return sBuf.toString();
	}

	public static void write(IEnumerable what, StringBuffer sBuf) throws Exception {
		for (IEnumerator en = what.enumerator(); en.moveNext();) {
			if (en.current() instanceof IMFNode) {
				IMFNode el = (IMFNode)en.current();
				if ((el.getNodeKind() & IMFNode.MFNodeKind_Element) != 0) {
					sBuf.append( "<{" + el.getNamespaceURI() + "}" + el.getLocalName());
					writeAttributes( el.select(IMFNode.MFQueryKind_AllAttributes, null), sBuf);
					sBuf.append( ">");
					write(el.select(IMFNode.MFQueryKind_AllChildren, null), sBuf);
					sBuf.append( "</{" + el.getNamespaceURI() + "}" + el.getLocalName() + ">");
				}
				if ((el.getNodeKind() & IMFNode.MFNodeKind_Attribute) != 0) {
					//done by writeAttributes
				} else if ((el.getNodeKind() & IMFNode.MFNodeKind_Comment) != 0) {
					//comments are ignored
				} else if ((el.getNodeKind() & IMFNode.MFNodeKind_ProcessingInstruction) != 0) {
					//processinginstructions are ignored
				} else if ((el.getNodeKind() & IMFNode.MFNodeKind_CData) != 0) {
					sBuf.append( MFDOMWriter.getValue( en.current(), null));
				} else if ((el.getNodeKind() & IMFNode.MFNodeKind_Text) != 0) {
					String s = MFDOMWriter.getValue( en.current(), null);
					if (!com.altova.functions.Lang.trim(s, "\n\t\r ").equals( ""))
						sBuf.append( s);
				}
			} else {
				sBuf.append( MFDOMWriter.getValue( en.current(), null));
			}
		}
	}

	private static void writeAttributes( IEnumerable what, StringBuffer sBuf) throws Exception {
		Vector<IMFNode> vAttributes = new Vector<IMFNode>( );
		for (IEnumerator en = what.enumerator(); en.moveNext();) {
			IMFNode el = (IMFNode)en.current();
			vAttributes.add(el);
		}
		
		Collections.sort( vAttributes, new Comparator<IMFNode>() {
			public int compare(IMFNode n1, IMFNode n2) {
				String key1 = n1.getNamespaceURI() + n1.getLocalName();
				String key2 = n2.getNamespaceURI() + n2.getLocalName();
				return key1.compareTo( key2);
			}
		});
		
		for (int i = 0; i < vAttributes.size(); ++i) {
			IMFNode el = vAttributes.get(i);
			if (!el.getLocalName().equals( "xmlns") && !el.getNamespaceURI().equals("http://www.w3.org/2000/xmlns/")) {
				sBuf.append( " {" + el.getNamespaceURI() + "}");
				sBuf.append( el.getLocalName() + "=\"" + MFDOMWriter.getValue( el, null) + "\"");
			}
		}
	}
	
	public static byte[] XMLToBase64Binary( Node xmlTree, String encoding, String byteorder, boolean bBOM ) throws Exception {
		return XmlTreeOperations.saveXmlBinary( (Document) xmlTree, encoding, byteorder.compareToIgnoreCase("big endian") == 0, bBOM, false );
	}

	public static byte[] XMLToBase64Binary( com.altova.mapforce.IMFNode node, String encoding, String byteorder, boolean bBOM ) throws Exception {
		Document doc = XmlTreeOperations.createDocument();
		com.altova.xml.MFDOMWriter.write(new com.altova.mapforce.MFSingletonSequence(node), doc);
		return XMLToBase64Binary(doc, encoding, byteorder, bBOM);
	}

	public static com.altova.mapforce.IMFNode XMLFromString( String xmlString ) throws Exception {
		return new DOMNodeAsMFNodeAdapter(XmlTreeOperations.loadXml(xmlString));
	}

	public static com.altova.mapforce.IMFNode XMLFromBase64Binary( byte[] xmlString ) throws Exception {
		return new DOMNodeAsMFNodeAdapter(XmlTreeOperations.loadXmlBinary(xmlString));
	}

	public static void ThrowException( String message ) throws Exception {
		throw new Exception( message );
	}

	public static IMFNode callWebservice(
		IMFNode node,
		String service,
		String operation,
		QName wsdl_operation,
		String soap_action,
		String connection_endpoint,
		String call_style,
		String username,
		String password,
		int timeout,
		String http_location,
		String input_style,
		int soapVersion) throws Exception {
		
		WebServiceCall call;
		if (call_style.equals("rpc") || call_style.equals("document")) {
			call = new WebServiceCall(connection_endpoint, wsdl_operation.getNamespaceURI(), operation, soap_action, "utf-8",
					call_style.equals("rpc")
					? WebServiceCall.SOAP_RPC_ENCODED
					: WebServiceCall.SOAP_DOCUMENT_LITERAL,
					(soapVersion == 1 ) ? WebServiceCall.SOAP_11 : WebServiceCall.SOAP_12 );
		} else if (call_style.equals("get") || call_style.equals("post")) {
			call = new WebServiceCall(connection_endpoint, http_location,
					input_style.equals("url-encoded") ? WebServiceCall.HTTP_URL_ENCODED
					: input_style.equals("url-replacement") ? WebServiceCall.HTTP_URL_REPLACEMENT
					: WebServiceCall.HTTP_XML,
					"utf-8",
					call_style.equals("get") ? WebServiceCall.HTTP_GET : WebServiceCall.HTTP_POST,
					(soapVersion == 1 ) ? WebServiceCall.SOAP_11 : WebServiceCall.SOAP_12
					);
		} else
			throw new UnsupportedOperationException("Unexpected call type.");

		call.setCredentials(username, password);
		Document doc = XmlTreeOperations.createDocument();
		com.altova.xml.MFDOMWriter.write(new com.altova.mapforce.MFSingletonSequence(node), doc);
		return new DOMNodeAsMFNodeAdapter(call.call(doc));
	}
	
	public static IMFNode throwWebserviceFault(String faultName, String message, IMFNode detail ) throws Exception {
		org.w3c.dom.Document faultDoc = com.altova.xml.XmlTreeOperations.createDocument();
		com.altova.xml.MFDOMWriter.write(detail.select(IMFNode.MFQueryKind_All, null), faultDoc);
		throw new com.altova.UserException( message, faultDoc );
	}
}
