/**
 *  Copyright (C) 2008-2017  Telosys project org. ( http://www.telosys.org/ )
 *
 *  Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.gnu.org/licenses/lgpl.html
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.telosys.tools.generator.languages.literals;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.telosys.tools.generator.context.AttributeInContext;
import org.telosys.tools.generator.languages.types.LanguageType;
import org.telosys.tools.generic.model.types.NeutralType;

/**
 * Literal values provider for "C++" language
 * 
 * @author Laurent GUERIN
 *
 */
public class LiteralValuesProviderForCPlusPlus extends LiteralValuesProvider {
	
	private static final String NULL_LITERAL  = "NULL" ;  // e.g. : char* a = NULL;
	private static final String TRUE_LITERAL  = "true" ;  // e.g. : bool flag = true;
	private static final String FALSE_LITERAL = "false" ; // e.g. : bool flag = false;

	@Override
	public String getLiteralNull() {
		return NULL_LITERAL;
	}
	
	@Override
	public String getLiteralTrue() {
		return TRUE_LITERAL;
	}

	@Override
	public String getLiteralFalse() {
		return FALSE_LITERAL;
	}
	
	@Override
	public LiteralValue generateLiteralValue(LanguageType languageType, int maxLength, int step) {
		// see : https://golang.org/test/literal.go
		String neutralType = languageType.getNeutralType(); 

		//--- STRING
		if ( NeutralType.STRING.equals(neutralType) ) {
			String value = buildStringValue(maxLength, step);
			return new LiteralValue("\"" + value + "\"", value) ;			
		}
		
		//--- NUMBER / INTEGER
		else if (  NeutralType.BYTE.equals(neutralType) 
				|| NeutralType.SHORT.equals(neutralType)
				|| NeutralType.INTEGER.equals(neutralType) 
				|| NeutralType.LONG.equals(neutralType) ) {
			Long value = buildIntegerValue(neutralType, step);  
			return new LiteralValue(value.toString(), value) ; // eg : 123
		}
		
		//--- NUMBER (NOT INTEGER)
		else if (  NeutralType.FLOAT.equals(neutralType) 
				|| NeutralType.DOUBLE.equals(neutralType) 
				|| NeutralType.DECIMAL.equals(neutralType) ) {
			BigDecimal value = buildDecimalValue(neutralType, step);
			return new LiteralValue(value.toString(), value) ; // eg :  123.77
		}

		//--- BOOLEAN
		else if ( NeutralType.BOOLEAN.equals(neutralType)  ) {
			boolean value = buildBooleanValue(step);
			return new LiteralValue(value ? TRUE_LITERAL : FALSE_LITERAL, Boolean.valueOf(value)) ;
		}

//		//--- DATE, TIME and TIMESTAMP :  ????
//		else if ( NeutralType.DATE.equals(neutralType)  ) {
//			return NULL_LITERAL ;
//		}
//		else if ( NeutralType.TIME.equals(neutralType)  ) {
//			return NULL_LITERAL ;
//		}
//		else if ( NeutralType.TIMESTAMP.equals(neutralType)  ) {
//			return NULL_LITERAL ;
//		}		
//		//--- BINARY
//		else if ( NeutralType.BINARY.equals(neutralType)  ) {
//			return NULL_LITERAL ;
//		}
		
//		return NULL_LITERAL ; 
		return new LiteralValue(NULL_LITERAL, null);
	}
	
	/* 
	 * Returns something like that : 
	 *   ' == 100' 
	 *   '.equals("xxx")'
	 */
	@Override
	public String getEqualsStatement(String value, LanguageType languageType) {

		// Always "==" ( whatever the type ) 
		return " == " + value ;
	}
	
	private static final Map<String,String> defaultValues = new HashMap<>();
	static {
		defaultValues.put(NeutralType.STRING,  "\"\"");  // string 
		defaultValues.put(NeutralType.BOOLEAN, FALSE_LITERAL);  
		defaultValues.put(NeutralType.BYTE,    "'\0'");  // char or unsigned char
		defaultValues.put(NeutralType.SHORT,   "0");  
		defaultValues.put(NeutralType.INTEGER, "0");  
		defaultValues.put(NeutralType.LONG,    "0");  
		defaultValues.put(NeutralType.FLOAT,   "0");  
		defaultValues.put(NeutralType.DOUBLE,  "0");  
		defaultValues.put(NeutralType.DECIMAL, "0");  

//		defaultValues.put(NeutralType.DATE,      "?");  
//		defaultValues.put(NeutralType.TIME,      "?"); 
//		defaultValues.put(NeutralType.TIMESTAMP, "?"); 
		defaultValues.put(NeutralType.BINARY,    "{}"); // void array, example : char x[10] = {};
	}
//	@Override
//	public String getDefaultValueNotNull(LanguageType languageType) {
//		String type = languageType.getNeutralType();
//		String defaultValue = defaultValues.get(type);
//		return defaultValue != null ? defaultValue : NULL_LITERAL ; 
//	}
	
	@Override
	public String getInitValue(AttributeInContext attribute, LanguageType languageType) {
		if (attribute.isNotNull()) {
			// not null attribute
			String defaultValue = defaultValues.get(languageType.getNeutralType());
			return defaultValue != null ? defaultValue : NULL_LITERAL ; 
		} else {
			// nullable attribute
			return NULL_LITERAL;
		}
	}
}
