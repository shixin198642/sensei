package com.sensei.search.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.SortField;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.browseengine.bobo.api.FacetSpec;
import com.browseengine.bobo.facets.DefaultFacetHandlerInitializerParam;
import com.sensei.search.req.SenseiRequest;

public class RequestConverter2 {

	public static String[] getStrings(JSONObject obj,String field){
		  String[] strArray = null;
		  JSONArray array = obj.optJSONArray(field);
		  if (array!=null){
			int count = array.length();
			strArray = new String[count];
			for (int i=0;i<count;++i){
				strArray[i] = array.optString(i);
			}
		  }
		  return strArray;
	  }
	  
	  private static int[] getInts(JSONObject obj,String field,int defaultVal){
		  int[] intArray = null;
		  JSONArray array = obj.optJSONArray(field);
		  if (array!=null){
			int count = array.length();
			intArray = new int[count];
			for (int i=0;i<count;++i){
				intArray[i] = array.optInt(i,defaultVal);
			}
		  }
		  return intArray;
	  }
	  
	  private static Set<Integer> getIntSet(JSONObject obj,String field,int defaultVal){
		  HashSet<Integer> intSet = null;
		  JSONArray array = obj.optJSONArray(field);
		  if (array!=null){
			int count = array.length();
			intSet = new HashSet<Integer>(count);
			for (int i=0;i<count;++i){
				intSet.add(array.optInt(i,defaultVal));
			}
		  }
		  return intSet;
	  }
	  
	  public static String[] getStrings(JSONArray jsonArray) throws Exception{
		  int count = jsonArray.length();
		  String[] vals = new String[count];
		  for (int i=0;i<count;++i){
			vals[i] = jsonArray.getString(i);
		  }
		  return vals;
	  }
	  
	public static SenseiRequest fromJSON(JSONObject json) throws Exception{
		SenseiRequest req = new SenseiRequest();
		
		// paging params
		int offset = json.optInt("from", 0);
		int count = json.optInt("size",10);
		
		req.setOffset(offset);
		req.setCount(count);
		
		// group by params
		JSONObject groupBy = json.optJSONObject("groupBy");
		if (groupBy!=null){
		  req.setGroupBy(groupBy.optString("column", null));
		  req.setMaxPerGroup(groupBy.optInt("top", 3));
		}
		
		// facetinit
        JSONObject facetInitParams = json.optJSONObject("facetInit");
        if (facetInitParams != null)
        {
          Iterator<String> keyIter = facetInitParams.keys();
          while (keyIter.hasNext())
          {
            // may have multiple facets;
            String facetName = keyIter.next();
            DefaultFacetHandlerInitializerParam param =
                new DefaultFacetHandlerInitializerParam();
    
            JSONObject jsonParams = facetInitParams.getJSONObject(facetName);
            if (jsonParams != null && jsonParams.length() > 0)
            {
              Iterator<String> paramIter = jsonParams.keys();
              while (paramIter.hasNext())
              {
                // each facet may have multiple parameters to be configured;
                String paramName = paramIter.next();
                JSONObject jsonParamValues = jsonParams.getJSONObject(paramName);
                String type = jsonParamValues.optString("type", "string");
                JSONArray jsonValues = jsonParamValues.optJSONArray("values");
                if (jsonValues != null)
                {
                  if (type.equals("int"))
                    param.putIntParam(paramName, convertJSONToIntArray(jsonValues));
                  else if (type.equals("string"))
                    param.putStringParam(paramName, convertJSONToStringArray(jsonValues));
                  else if (type.equals("boolean"))
                    param.putBooleanParam(paramName, convertJSONToBoolArray(jsonValues));
                  else if (type.equals("long"))
                    param.putLongParam(paramName, convertJSONToLongArray(jsonValues));
                  else if (type.equals("bytes"))
                    param.putByteArrayParam(paramName, convertJSONToByteArray(jsonValues));
                  else if (type.equals("double"))
                    param.putDoubleParam(paramName, convertJSONToDoubleArray(jsonValues));
                }
              }
              req.setFacetHandlerInitializerParam(facetName, param);
            }
    
          }
        }
		
		 // facets
		  
		  JSONObject facets = json.optJSONObject("facets");
		  if (facets!=null){
			  Iterator<String> keyIter = facets.keys();
			  while (keyIter.hasNext()){
				  String field = keyIter.next();
				  JSONObject facetObj = facets.getJSONObject(field);
				  if (facetObj!=null){
					 FacetSpec facetSpec = new FacetSpec();
					 facetSpec.setMaxCount(facetObj.optInt("max", 10));
					 facetSpec.setMinHitCount(facetObj.optInt("minCount", 1));
					 facetSpec.setExpandSelection(facetObj.optBoolean("expand", false));
					 
					 String orderBy = facetObj.optString("order", "hits");
					 FacetSpec.FacetSortSpec facetOrder = FacetSpec.FacetSortSpec.OrderHitsDesc;
					 if ("val".equals(orderBy)){
						 facetOrder = FacetSpec.FacetSortSpec.OrderValueAsc;
					 }
					 
					 facetSpec.setOrderBy(facetOrder);
					 req.setFacetSpec(field, facetSpec);
				  }
			  }
		  }
		// sorts
		  
		  JSONArray sortArray = json.optJSONArray("sort");
		  if (sortArray!=null && sortArray.length()>0){
			  ArrayList<SortField> sortFieldList = new ArrayList<SortField>(sortArray.length());
			  for (int i=0;i<sortArray.length();++i){
				String strForm = sortArray.optString(i, null);
				if (strForm!=null && "_score".equals(strForm)){
					sortFieldList.add(SortField.FIELD_SCORE);
			    	continue;
				}
				if (sortArray.optString(i,null)==null){
					
				}
			    JSONObject sortObj = sortArray.optJSONObject(i);
			    if (sortObj!=null){
			       String[] fieldNames = JSONObject.getNames(sortObj);
			       if (fieldNames!=null && fieldNames.length>0){
			    	   String field = fieldNames[0];
			    	   boolean reverse=false;
			    	   if ("desc".equals(sortObj.optString(field, "asc"))){
			    		   reverse = true;
			    	   }
			    	   sortFieldList.add(new SortField(field,SortField.CUSTOM,reverse));
			       }
			    }
			  }
			  if (sortFieldList.size()>0){
			    req.setSort(sortFieldList.toArray(new SortField[sortFieldList.size()]));
			  }
		  }
		
		// other
		  
		boolean fetchStored = json.optBoolean("fetchStored");
		req.setFetchStoredFields(fetchStored);
		  
		String[] termVectors = getStrings(json,"fetchTermVectors");
		if (termVectors!=null && termVectors.length>0){
		  req.setTermVectorsToFetch(new HashSet<String>(Arrays.asList(termVectors)));
		}
		  

		req.setPartitions(getIntSet(json,"partitions",0));
		  
		req.setShowExplanation(json.optBoolean("explain",false));
		  
		String routeParam = json.optString("routeParam",null);
		if(routeParam.equals("null"))
		  routeParam = null;
		req.setRouteParam(routeParam);
		  
		return req;
	}

  /**
   * @param jsonValues
   * @return
   * @throws JSONException
   */
  private static double[] convertJSONToDoubleArray(JSONArray jsonArray) throws JSONException
  {
    double[] doubleArray = new double[jsonArray.length()];
    if (jsonArray != null && jsonArray.length() > 0)
    {
      for (int i = 0; i < jsonArray.length(); i++)
      {
        doubleArray[i] = jsonArray.getDouble(i);
      }
    }
    return doubleArray;
  }

  /**
   * @param jsonValues
   * @return
   * @throws JSONException
   * @throws NumberFormatException
   */
  private static byte[] convertJSONToByteArray(JSONArray jsonArray) throws NumberFormatException,
      JSONException
  {
    byte[] byteArray = new byte[jsonArray.length()];
    if (jsonArray != null && jsonArray.length() > 0)
    {
      for (int i = 0; i < jsonArray.length(); i++)
      {
        byteArray[i] = Byte.valueOf(String.valueOf(jsonArray.get(i)));
      }
    }
    return byteArray;
  }

  /**
   * @param jsonValues
   * @return
   * @throws JSONException
   */
  private static long[] convertJSONToLongArray(JSONArray jsonArray) throws JSONException
  {
    long[] longArray = new long[jsonArray.length()];
    if (jsonArray != null && jsonArray.length() > 0)
    {
      for (int i = 0; i < jsonArray.length(); i++)
      {
        longArray[i] = jsonArray.getLong(i);
      }
    }
    return longArray;
  }

  /**
   * @param jsonValues
   * @return
   * @throws JSONException
   */
  private static boolean[] convertJSONToBoolArray(JSONArray jsonArray) throws JSONException
  {
    boolean[] boolArray = new boolean[jsonArray.length()];
    if (jsonArray != null && jsonArray.length() > 0)
    {
      for (int i = 0; i < jsonArray.length(); i++)
      {
        boolArray[i] = jsonArray.getBoolean(i);
      }
    }
    return boolArray;
  }

  /**
   * @param jsonValues
   * @return
   * @throws JSONException
   */
  private static List<String> convertJSONToStringArray(JSONArray jsonArray) throws JSONException
  {
    List<String> arString = new ArrayList<String>();
    if (jsonArray != null && jsonArray.length() > 0)
    {
      for (int i = 0; i < jsonArray.length(); i++)
      {
        arString.add(jsonArray.getString(i));
      }
    }
    return arString;
  }

  /**
   * @param jsonValues
   * @return
   * @throws JSONException
   */
  private static int[] convertJSONToIntArray(JSONArray jsonArray) throws JSONException
  {
    int[] intArray = new int[jsonArray.length()];
    if (jsonArray != null && jsonArray.length() > 0)
    {
      for (int i = 0; i < jsonArray.length(); i++)
      {
        intArray[i] = jsonArray.getInt(i);
      }
    }
    return intArray;
  }
}
