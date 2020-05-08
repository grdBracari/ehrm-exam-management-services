/**
 * MFElement.java
 *
 * This file was generated by MapForce 2020r2.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the MapForce Documentation for further details.
 * http://www.altova.com/mapforce
 */

package com.altova.mapforce;

import javax.xml.namespace.QName;

public class MFElement implements IMFNode {
	private QName qname;
	private String nodeName;
	private IEnumerable children;
	private java.util.ArrayList<Object> childrenCache;

	public static class ArrayListAsEnumerable implements IEnumerable {
		java.util.ArrayList<?> a;
		public ArrayListAsEnumerable(java.util.ArrayList<?> a) { this.a = a; }
		public IEnumerator enumerator() { return new Enumerator(a); }

		static class Enumerator implements IEnumerator {
			java.util.ArrayList<?> a;
			int index;
			public Enumerator(java.util.ArrayList<?> a) { this.a = a; this.index = -1; }
			public boolean moveNext() { ++index; return index < a.size(); }
			public int position() { return index + 1; }
			public Object current() { return a.get(index); }
			public void close() { }
		}
	}
	
	public static class ListAsEnumerable implements IEnumerable {
		java.util.List<?> a;
		public ListAsEnumerable(java.util.List<?> a) { this.a = a; }
		public IEnumerator enumerator() { return new Enumerator(a); }

		static class Enumerator implements IEnumerator {
			java.util.ListIterator<?> li;
			Object current;
			int index;
			int size;
			public Enumerator(java.util.List<?> a) { li = a.listIterator(); size = a.size(); index = -1; current = null; }
			public boolean moveNext() {
				if (li.hasNext()) {
					index = li.nextIndex();
					current = li.next();
					return true;
				}				
					
				return false;
			}
			public int position() { return index; }
			public Object current() { return current; }
			public void close() { }
		}
	}
	
	private void createCache() {
		try {
			if (childrenCache == null) {
				childrenCache = new java.util.ArrayList<Object>();
				for (IEnumerator en = children.enumerator(); en.moveNext(); ) {
					childrenCache.add(en.current());
				}
				children = new ArrayListAsEnumerable(childrenCache);
			}
		} catch (Exception e) {}
	}
		
	public MFElement(String localName, String namespaceURI, String prefix, IEnumerable children) {
		this.qname = new QName(namespaceURI, localName, prefix);
		this.nodeName = prefix.equals("") ? localName : prefix + ":" + localName;
		this.children = children;
	}
		
	public MFElement(QName qname, IEnumerable children) {
		this.qname = qname;
		this.nodeName = qname.getPrefix().equals("") ? qname.getLocalPart() : qname.getPrefix() + ":" + qname.getLocalPart();
		this.children = children;
	}

	public MFElement(String nodename, IEnumerable children) {
		this.qname = new QName(nodename);
		this.nodeName = nodename;
		this.children = children;
	}
	
	public String getLocalName() {
		return qname.getLocalPart();
	}

	public String getNamespaceURI() {
		return qname.getNamespaceURI();
	}
	
	public String getPrefix() {
		return qname.getPrefix();
	}

	public String getNodeName() {
		return nodeName;
	}

	public QName getQName() {
		return qname;
	}
	
	public int getNodeKind() {
		return MFNodeKind_Element;
	}

	public IEnumerable select(int mfQueryKind, Object query) {
		switch (mfQueryKind) {
			case MFQueryKind_All:
				return new MFNodeByKindFilter(children, MFNodeKind_AllChildren);
				
			case MFQueryKind_AllChildren:
				createCache();
				return new MFNodeByKindFilter(children, MFNodeKind_Children);
			
			case MFQueryKind_AllAttributes:
				createCache();
				return new MFNodeByKindFilter(children, MFNodeKind_Attribute | MFNodeKind_Field);
			
			case MFQueryKind_AttributeByQName:
				createCache();
				return new MFNodeByKindAndQNameFilter(children, MFNodeKind_Attribute | MFNodeKind_Field,
					(javax.xml.namespace.QName) query);

			case MFQueryKind_ChildrenByQName:
				createCache();
				return new MFNodeByKindAndQNameFilter(children, MFNodeKind_Element,
						(javax.xml.namespace.QName) query);

			case MFQueryKind_AttributeByNodeName:
				createCache();
				return new MFNodeByKindAndNodeNameFilter(children, MFNodeKind_Attribute | MFNodeKind_Field,
					(java.lang.String) query);

			case MFQueryKind_ChildrenByNodeName:
				createCache();
				return new MFNodeByKindAndNodeNameFilter(children, MFNodeKind_Element,
						(java.lang.String) query);

			case MFQueryKind_SelfByQName:
				if (qname.equals(query))
					return new MFSingletonSequence(this);
				else
					return new MFEmptySequence();

			default:
				throw new UnsupportedOperationException("Unsupported query type."); 
		}
	}

	public String value() throws Exception {
		String s =  "";
				
		for (IEnumerator v = select(IMFNode.MFQueryKind_AllChildren, null).enumerator(); v.moveNext();) {
			Object o = v.current();
			if (o instanceof IMFNode) {
				IMFNode n = (IMFNode) o;
				if (n.getNodeKind() != IMFNode.MFNodeKind_Comment && n.getNodeKind() != IMFNode.MFNodeKind_ProcessingInstruction)
					s += n.value();
			} else if (o instanceof javax.xml.namespace.QName)
				s += com.altova.CoreTypes.castToString((javax.xml.namespace.QName) o);
			else
				s += o.toString();
		}
		return s;
	}
	
	public javax.xml.namespace.QName qnameValue() {
		try {
			IEnumerator e = select(MFQueryKind_AllChildren, null).enumerator();
		
			if (!e.moveNext())
				throw new RuntimeException("Trying to convert NULL to QName.");
			
			javax.xml.namespace.QName q;
			
			if (e.current() instanceof IMFNode)
				q = ((IMFNode) e.current()).qnameValue();
			else
				q = (javax.xml.namespace.QName) e.current();
		
			if (e.moveNext())
				throw new RuntimeException("Trying to convert multiple values to QName.");
			
			return q;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage());
		}
	}
	
	public Object typedValue() throws Exception {
		return MFNode.collectTypedValue(select(IMFNode.MFQueryKind_AllChildren, null));
	}
}
