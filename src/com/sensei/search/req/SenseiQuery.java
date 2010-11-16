package com.sensei.search.req;

import java.nio.charset.Charset;

public class SenseiQuery {
  private byte[] _bytes;
  static Charset utf8Charset = Charset.forName("UTF-8");
	
  public SenseiQuery(byte[] bytes){
	  _bytes = bytes;
  }
  
  final public byte[] toBytes(){
	  return _bytes;
  }
  
  @Override
  public String toString()
  {
	return new String(_bytes,utf8Charset);
  }
}