////////////////////////////////////////////////////////////////////////
//
// ISerializer.java
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

package com.altova.text.tablelike;

public interface ISerializer {
	void serialize(java.io.OutputStream stream) throws MappingException;
	void serialize(java.io.Writer writer) throws MappingException;
	void deserialize(java.io.InputStream stream) throws MappingException;
	void deserialize(java.io.Reader reader) throws MappingException;

	void setEncoding(String encoding, boolean bBigEndian, boolean bBOM);
}