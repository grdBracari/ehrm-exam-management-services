////////////////////////////////////////////////////////////////////////
//
// TextDocumentAsMFNodeAdapter.java
//
// This file was generated by MapForce 2020r2.
//
// YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
// OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
//
// Refer to the MapForce Documentation for further details.
// http://www.altova.com/mapforce
//
////////////////////////////////////////////////////////////////////////


package com.altova.text;

import com.altova.mapforce.IMFDocumentNode;

public class TextDocumentAsMFNodeAdapter extends TextNodeAsMFNodeAdapter implements IMFDocumentNode {
	private String filename;
	
	public TextDocumentAsMFNodeAdapter(ITextNode node, String filename) {
		super(node);
		this.filename = filename;
	}
	
	public String getDocumentUri() {return filename;}
}
