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
package org.telosys.tools.generator.context;

import java.util.LinkedList;
import java.util.List;

import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.generator.context.names.ContextName;
import org.telosys.tools.generator.context.tools.LinesBuilder;

//-------------------------------------------------------------------------------------
@VelocityObject(
	contextName=ContextName.JAVA,
	text = { 
		"Object providing a set of utility functions for JAVA language code generation",
		""
	},
	since = "2.0.7"
 )
//-------------------------------------------------------------------------------------
public class Java {

	private static final List<String> VOID_STRINGS_LIST = new LinkedList<>();

	private String buildIndentationWithSpaces(int nSpaces) {
		StringBuilder spaces = new StringBuilder();
		for ( int n = 0 ; n < nSpaces ; n++ ) {
			spaces.append(" ");
		}
		return spaces.toString();
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing all the code for a Java 'equals' method",
			"Tabulations are used for code indentation"
			},
		example={ 
			"$java.equalsMethod( $entity.name, $entity.attributes )" },
		parameters = { 
			"className : the Java class name (simple name or full name)",
			"attributes : list of attributes to be used in the equals method"},
		since = "2.0.7"
			)
	public String equalsMethod( String className, List<AttributeInContext> attributes ) {
		
		return equalsMethod( className , attributes, new LinesBuilder() ); 
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing all the code for a Java 'equals' method",
			"Spaces are used for code indentation"
			},
		example={ 
			"$java.equalsMethod( $entity.name, $entity.attributes, 4 )" },
		parameters = { 
			"className : the Java class name (simple name or full name)",
			"attributes : list of attributes to be used in the equals method",
			"indentSpaces : number of spaces to be used for each indentation level"},
		since = "2.0.7"
			)
	public String equalsMethod( String className, List<AttributeInContext> attributes, int indentSpaces ) {
		
		return equalsMethod( className , attributes, new LinesBuilder(buildIndentationWithSpaces(indentSpaces)) ); 
	}
	
	//-------------------------------------------------------------------------------------
	private String equalsMethod( String className, List<AttributeInContext> fieldsList, LinesBuilder lb ) {

		int indent = 1 ;
		lb.append(indent, "public boolean equals(Object obj) { ");
		
		indent++;
		lb.append(indent, "if ( this == obj ) return true ; ");
		lb.append(indent, "if ( obj == null ) return false ;");
		lb.append(indent, "if ( this.getClass() != obj.getClass() ) return false ; ");
		
		// Cast obj to the given className 
		lb.append( indent, className + " other = (" + className + ") obj; ");
		
		if ( fieldsList != null ) {
			for ( AttributeInContext attribute : fieldsList ) {
				
				String attributeName = attribute.getName() ;
				lb.append(indent, "//--- Attribute " + attributeName );
				if ( attribute.isPrimitiveType() ) {
					if ( attribute.isFloatType() ) {
						// float
						lb.append(indent, 
								"if ( Float.floatToIntBits(" + attributeName 
								+ ") != Float.floatToIntBits(other." + attributeName + ") ) return false ; ");
					}
					else if ( attribute.isDoubleType() ) {
						// double 
						lb.append(indent, 
								"if ( Double.doubleToLongBits(" + attributeName 
								+ ") != Double.doubleToLongBits(other." + attributeName + ") ) return false ; ");
					}
					else {
						// char, byte, short, int, long, boolean 
						lb.append(indent, "if ( " + attributeName + " != other." + attributeName + " ) return false ; ");
					}
				}
				else if ( isArray(attribute) ) {
					// char[], byte[], String[], ...
					lb.append(indent, "if ( ! Arrays.equals(" + attributeName + ", other." + attributeName + ") ) return false ; ");
				}
				else {
					lb.append(indent, "if ( " + attributeName + " == null ) { ");
						lb.append(indent+1, "if ( other." + attributeName + " != null ) ");
							lb.append(indent+2, "return false ; ");
					lb.append(indent, "} else if ( ! " + attributeName + ".equals(other."+attributeName+") ) " );
						lb.append(indent+1, "return false ; ");
				}
			}
		} 
		
		lb.append(indent, "return true; ");
		
		indent--;
		lb.append(indent, "} ");

		return lb.toString();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Returns a string containing all the code for a Java 'hashCode' method",
				"Tabulations are used for code indentation"
				},
			example={ 
				"$java.hashCode( $entity.name, $entity.attributes )" },
			parameters = { 
				"className  : the Java class name (simple name or full name)",
				"attributes : list of attributes to be used in the equals method"},
			since = "2.0.7"
				)
	public String hashCodeMethod( String className, List<AttributeInContext> attributes ) {
		return hashCodeMethod(attributes, new LinesBuilder() ); 
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Returns a string containing all the code for a Java 'hashCode' method",
				"Spaces are used for code indentation"
				},
			example={ 
				"$java.hashCode( $entity.name, $entity.attributes, 4 )" },
			parameters = { 
				"className  : the Java class name (simple name or full name)",
				"attributes : list of attributes to be used in the equals method",
				"indentSpaces : number of spaces to be used for each indentation level"},
			since = "2.0.7"
				)
	public String hashCodeMethod( String className, List<AttributeInContext> attributes, int indentSpaces ) {
		return hashCodeMethod(attributes, new LinesBuilder(buildIndentationWithSpaces(indentSpaces)) ); 
	}
	
	//-------------------------------------------------------------------------------------
	private String hashCodeMethod(List<AttributeInContext> fieldsList, LinesBuilder lb ) {

		int indent = 1 ;
		lb.append(indent, "public int hashCode() { ");

		boolean longtempVarDefined = false ;
		indent++;
			lb.append(indent, "final int prime = 31; ");
			lb.append(indent, "int result = 1; ");
			lb.append(indent, "");
			
			if ( fieldsList != null ) {
				for ( AttributeInContext attribute : fieldsList ) {
					
					String attributeName = attribute.getName() ;
					lb.append(indent, "//--- Attribute " + attributeName );
					if ( attribute.isPrimitiveType() ) {
						//--- Primitive types
						if ( attribute.isBooleanType() ) {
							// boolean
							lb.append(indent, "result = prime * result + (" + attributeName + " ? 1231 : 1237 );");
						}
						else if ( attribute.isLongType() ) {
							// long (must be converted to int)
							lb.append(indent, "result = prime * result + (int) (" + attributeName 
									+ " ^ (" + attributeName + " >>> 32));");
						}
						else if ( attribute.isFloatType() ) {
							// float
							lb.append(indent, "result = prime * result + Float.floatToIntBits(" + attributeName + ");");
						}
						else if ( attribute.isDoubleType() ) {
							// double
							if ( ! longtempVarDefined ) {
								lb.append(indent, "long temp;");
								longtempVarDefined = true ;
							}
							lb.append(indent, "temp = Double.doubleToLongBits(" + attributeName + ");");
							lb.append(indent, "result = prime * result + (int) (temp ^ (temp >>> 32));");
						}
						else {
							// char, byte, short, int 
							lb.append(indent, "result = prime * result + " + attributeName + ";");
						}
					}
					else if ( isArray(attribute) ) { 
						// char[], byte[], String[], ...
						lb.append(indent, "result = prime * result + Arrays.hashCode(" + attributeName + ");");
					}
					else {
						//--- Objects : just use the 'hashCode' method
						lb.append(indent, "result = prime * result + ((" + attributeName + " == null) ? 0 : " 
								+ attributeName + ".hashCode() ) ; ");
					}
				}
			} 

			lb.append(indent, "");
			lb.append(indent, "return result; ");
		indent--;
		lb.append(indent, "} ");

		return lb.toString();
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Returns the list of Java classes to be imported for the given attributes",
				""
				},
			example={ 
				"#foreach( $import in $java.imports($entity.attributes) )",
				"import $import;",
				"#end)" },
			parameters = {
				"attributes : list of attributes" },
			since = "2.0.7"
				)
	public List<String> imports( List<AttributeInContext> attributesList ) {
		if ( attributesList != null ) {
			JavaImportsList imports = new JavaImportsList();
			for ( AttributeInContext attribute : attributesList ) {
				// register the type to be imported if necessary
				imports.declareType( attribute.getFullType() ); 
			}
			return imports.getFinalImportsList();			
		}
		return VOID_STRINGS_LIST ;
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
			text={	
				"Returns the list of Java classes to be imported for the given entity",
				"The imports are determined using all the entity attributes and links ",
				"Examples for attributes : 'java.time.LocalDateTime', 'java.math.BigDecimal', etc",
				"Examples for links : 'java.util.List', etc"
				},
			example={ 
				"#foreach( $import in $java.imports($entity) )",
				"import $import;",
				"#end)" },
			parameters = {
				"entity : entity to be used " },
			since = "2.0.7"
				)
	public List<String> imports( EntityInContext entity ) {
		if ( entity != null ) {
			JavaImportsList imports = new JavaImportsList();
			//--- All the attributes
			for ( AttributeInContext attribute : entity.getAttributes() ) {
				// register the type to be imported if necessary
				imports.declareType( attribute.getFullType() ); 
			}
			//--- All the links 
			for ( LinkInContext link : entity.getLinks() ) {
				if ( link.isCardinalityOneToMany() || link.isCardinalityManyToMany() ) {
					String type = link.getFieldType();
					if ( type.contains("Set<") && type.contains(">") ) {
						imports.declareType("java.util.Set");
					} 
					else if ( type.contains("Collection<") && type.contains(">") ) {
						imports.declareType("java.util.Collection");
					} 
					else {
						// by default "List" 
						imports.declareType("java.util.List");
					}
				}
				else {
					// ManyToOne or OneToOne => bean ( "Book", "Person", ... )
					// Supposed to be in the same package
				}
			}
			//--- Resulting list of imports
			return imports.getFinalImportsList();
		}
		return VOID_STRINGS_LIST ;
	}

	//-------------------------------------------------------------------------------------
	// toString METHOD GENERATION
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing all the code for a Java 'toString()' method",
			"Generates a 'toString' method using all the attributes of the given entity",
			"(except non-printable attributes)",
			"Indentation with TABS (1 tab for each indentation level)"
			},
		example={ 
			"$java.toStringMethod( $entity, 2 )" },
		parameters = { 
			"entity : the entity for which to generate the 'toString' method",
			"indentationLevel : initial indentation level" },
		since = "4.1.0"
			)
//	public String toStringMethod( EntityInContext entity, int indentSpaces ) {
	public String toStringMethod( EntityInContext entity, int indentationLevel ) {
//		return toStringMethod(entity.getAttributes(), indentSpaces );
		return buildToStringMethod( entity, entity.getAttributes(), indentationLevel, new LinesBuilder() ); 		
	}
	
	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing all the code for a Java 'toString()' method",
			"Generates a 'toString' method using all the attributes of the given entity",
			"(except non-printable attributes)",
			"Indentation with SPACES (1 'indentationString' for each indentation level)"
			},
		example={ 
			"$csharp.toStringMethod( $entity, 2, '  ' )" },
		parameters = { 
			"entity : the entity for which to generate the 'toString' method",
			"indentationLevel : initial indentation level",
			"indentationString : string to use for each indentation (usually N spaces)"},
		since = "4.1.0"
			)
	public String toStringMethod( EntityInContext entity, int indentationLevel, String indentationString ) {
		return buildToStringMethod( entity, entity.getAttributes(), indentationLevel, new LinesBuilder(indentationString) ); 
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing all the code for a Java 'toString()' method",
			"Generates a 'toString' method using the given attributes ",
			"(except non-printable attributes)",
			"Indent with TABS (1 tab for each indentation level)"
			},
		example={ 
			"$csharp.toStringMethod( $entity, $attributes, 2 )" },
		parameters = { 
			"entity : the entity for which to generate the 'toString' method",
			"attributes : list of attributes to be used in the 'toString' method",
			"indentationLevel : initial indentation level" },
		since = "4.1.0"
			)
	public String toStringMethod( EntityInContext entity, List<AttributeInContext> attributes, int indentationLevel ) {
		return buildToStringMethod( entity, attributes, indentationLevel, new LinesBuilder() ); 
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns a string containing all the code for a Java 'toString()' method",
			"Generates a 'toString' method using the given attributes ",
			"(except non-printable attributes)",
			"Indentation with spaces (1 'indentationString' for each indentation level)"
			},
		example={ 
			"$csharp.toStringMethod( $entity, $attributes, 2, '  ' )" },
		parameters = { 
			"entity : the entity for which to generate the 'toString' method",
			"attributes : list of attributes to be used in the 'toString' method",
			"indentationLevel : initial indentation level",
			"indentationString : string to use for each indentation (usually N spaces) "},
		since = "4.1.0"
			)
	public String toStringMethod( EntityInContext entity, List<AttributeInContext> attributes, int indentationLevel, String indentationString ) {
		return buildToStringMethod( entity, attributes, indentationLevel, new LinesBuilder(indentationString) ); 
	}

	//-------------------------------------------------------------------------------------
//	@VelocityMethod(
//		text={	
//			"Returns a string containing all the code for a Java 'toString' method",
//			"Generates a 'toString' method using all the given attributes ",
//			"(excluded types are 'array', 'Clob', 'Blob', and 'Long Text String') "
//			},
//		example={ 
//			"$java.toStringMethod( $attributes, 4 )" },
//		parameters = { 
//			"attributes : list of attributes to be used in the 'toString' method",
//			"indentSpaces : number of spaces to be used for each indentation level"},
//		since = "2.1.0"
//			)
//	public String toStringMethod( List<AttributeInContext> attributes, int indentSpaces ) {
//
//		LinesBuilder lb = new LinesBuilder(buildIndentationWithSpaces(indentSpaces)) ;
//		int indent = 1 ;
//		lb.append(indent, "public String toString() { ");
//		
//		indent++;
//		lb.append(indent, "StringBuilder sb = new StringBuilder(); ");
//		//--- All the given attributes 
//		if ( attributes != null ) {
//			toStringForAttributes( attributes, lb, indent );
//		}
//		lb.append(indent, "return sb.toString(); ");
//		indent--;
//		
//		lb.append(indent, "} ");
//		return lb.toString();
//	}
    
	//-------------------------------------------------------------------------------------
//	@VelocityMethod(
//		text={	
//			"Returns a string containing all the code for a Java 'toString' method",
//			"Generates a 'toString' method with the primary key attribute or the embedded key ",
//			"and the given list of 'non key' attributes if their type is usable in a 'toString' method",
//			"(excluded types are 'array', 'Clob', 'Blob', and 'Long Text String') "
//			},
//		example={ 
//			"$java.toStringMethod( $entity, $nonKeyAttributes, $embeddedIdName, 4 )" },
//		parameters = { 
//			"entity : the entity to be used",
//			"nonKeyAttributes : list of attributes that are not in the Primary Key",
//			"embeddedIdName : variable name for the embedded id (used only if the entity has a composite primary key) " },
//		since = "2.0.7"
//			)
//	public String toStringMethod( EntityInContext entity, List<AttributeInContext> nonKeyAttributes, String embeddedIdName ) {
//			
//		return toStringMethod( entity , nonKeyAttributes, embeddedIdName, new LinesBuilder() ); 
//	}
		
	//-------------------------------------------------------------------------------------
//	@VelocityMethod(
//		text={	
//			"Returns a string containing all the code for a Java 'toString' method",
//			"Generates a 'toString' method with the primary key attribute or the embedded key ",
//			"and the given list of 'non key' attributes if their type is usable in a 'toString' method",
//			"(excluded types are 'array', 'Clob', 'Blob', and 'Long Text String') "
//			},
//		example={ 
//			"$java.toStringMethod( $entity, $nonKeyAttributes, $embeddedIdName, 4 )" },
//		parameters = { 
//			"entity : the entity to be used",
//			"nonKeyAttributes : list of attributes that are not in the Primary Key",
//			"embeddedIdName : variable name for the embedded id (used only if the entity has a composite primary key) ",
//			"indentSpaces : number of spaces to be used for each indentation level"},
//		since = "2.0.7"
//			)
//	public String toStringMethod( EntityInContext entity, List<AttributeInContext> nonKeyAttributes, 
//			String embeddedIdName, int indentSpaces )  {
//		
//		return toStringMethod( entity , nonKeyAttributes, embeddedIdName, new LinesBuilder(buildIndentationWithSpaces(indentSpaces)) ); 
//	}
	
	//-------------------------------------------------------------------------------------
//	private String toStringMethod( EntityInContext entity, List<AttributeInContext> nonKeyAttributes, 
//			String embeddedIdName, LinesBuilder lb )  {
//
//		int indent = 1 ;
//		lb.append(indent, "public String toString() { ");
//		
//		indent++;
//		lb.append(indent, "StringBuilder sb = new StringBuilder(); ");
//		
//		lb.append(indent, "sb.append(\"[\"); ");
//		//--- PRIMARY KEY attributes ( composite key or not )
//		if ( entity.hasCompositePrimaryKey() && ( embeddedIdName != null ) ) {
//			// Embedded id 
//			toStringForEmbeddedId( embeddedIdName, lb, indent );
//		}
//		else {
//			// No embedded id ( or no name for it )
//			List<AttributeInContext> keyAttributes = entity.getKeyAttributes() ;
//			toStringForAttributes( keyAttributes, lb, indent );
//		}
//		lb.append(indent, "sb.append(\"]:\"); ");
//		
//		//--- NON KEY attributes ( all the attributes that are not in the Primary Key )
//		if ( nonKeyAttributes != null ) {
//			toStringForAttributes( nonKeyAttributes, lb, indent );
//		}
//				
//		lb.append(indent, "return sb.toString(); ");
//		
//		indent--;
//		lb.append(indent, "} ");
//
//		return lb.toString();
//	}
	
//    /**
//     * Uses the given attributes except if their type is not usable   
//     * @param attributes
//     * @param lb
//     * @param indent
//     * @return
//     */
//    private int toStringForAttributes( List<AttributeInContext> attributes, LinesBuilder lb, int indent  ) 
//    {    	
//    	if ( null == attributes ) return 0 ;
//    	int count = 0 ;
//    	for ( AttributeInContext attribute : attributes ) {
//    		if ( usableInToString( attribute ) ) {
//                if ( count > 0 ) // if it's not the first one
//                {
//        			lb.append(indent, "sb.append(\"|\");" );
//                }        		
//    			lb.append(indent, "sb.append(" + attribute.getName() + ");" );
//    			count++ ;
//    		}
//    		else {
//    			String sLongText = attribute.isLongText() ? " Long Text" : "" ; 
//    			lb.append(indent, "// attribute '" + attribute.getName() 
//    					+ "' not usable (type = " + attribute.getType() + sLongText + ")");
//    		}
//    	}
//    	return count ;
//    }
    
//    /**
//     * Just use the embedded primary with its own 'toString'
//     * @param embeddedIdName
//     * @param lb
//     * @param indent
//     * @return
//     */
//    private int toStringForEmbeddedId( String embeddedIdName, LinesBuilder lb, int indent  )
//    {
//		lb.append(indent, "if ( " + embeddedIdName + " != null ) {  ");
//		lb.append(indent, "    sb.append(" + embeddedIdName + ".toString());  ");
//		lb.append(indent, "}  ");
//		lb.append(indent, "else {  ");
//		lb.append(indent, "    sb.append( \"(null-key)\" ); ");
//		lb.append(indent, "}  ");
//		return 1 ;
//    }

    /**
     * @param attribute
     * @return
     * @since v 3.0.0
     */
    private boolean isArray( AttributeInContext attribute ) {
    	String type = attribute.getSimpleType();
		if ( type != null && type.trim().endsWith("]")) {
				return true ;
		}
		return false ;    	
    }

	//-------------------------------------------------------------------------------------
	/**
	 * Returns a string containing all the code for the "toString" method
	 * @param entity
	 * @param attributes
	 * @param indentLevel
	 * @param lb
	 * @return
	 */
	private String buildToStringMethod( EntityInContext entity, List<AttributeInContext> attributes, int indentLevel, LinesBuilder lb ) {
    	if ( entity == null ) {
    		throw new IllegalArgumentException("$java.toStringMethod(..) : entity arg is null");
    	}
    	if ( attributes == null ) {
    		throw new IllegalArgumentException("$java.toStringMethod(..) : attributes arg is null");
    	}
		int indent = indentLevel ;
		lb.append(indent, "public String toString() { ");
		indent++;
    	if ( attributes.isEmpty() ) {
    		//--- No attributes
    		lb.append(indent, "return \"" + entity.getName() + " [no attribute]\" ;");
    	}
    	else {
    		//--- Build return concat with all the given attributes 
    		buildToStringMethodBody( entity, attributes, indent, lb );
    	}
		indent--;
		lb.append(indent, "}");
		return lb.toString();
	}
	
    /**
     * Builds the body of the "toString" method using the given LinesBuilder
     * @param entity
     * @param attributes
     * @param indentationLevel
     * @param lb
     */
    private void buildToStringMethodBody( EntityInContext entity, List<AttributeInContext> attributes, int indentationLevel, LinesBuilder lb) 
    {    	
    	if ( null == attributes ) return ;
    	int count = 0 ;
    	// first lines
    	lb.append(indentationLevel, "String separator = \"|\";");
		lb.append(indentationLevel, "StringBuilder sb = new StringBuilder();"); 
		lb.append(indentationLevel, "sb.append(\"" + entity.getName() + "[\");");  // append the class name, example : sb.append("Employee[")
    	for ( AttributeInContext attribute : attributes ) {
    		if ( usableInToString( attribute ) ) {
    			String startOfLine = "";
                if ( count > 0 ) {
                	startOfLine = "sb.append(separator)" ; // not the first one => append separator before
                }
                else {
                	startOfLine = "sb" ; // first one => no separator before
                }
    			lb.append(indentationLevel, startOfLine + ".append(\"" + attribute.getName() + "=\").append(" + attribute.getName() + ");"); 
    			// example: sb.append("firstName=").append(firstName) 
    			count++ ;
    		}
    		else {
    			lb.append(indentationLevel, "// attribute '" + attribute.getName() + "' (type " + attribute.getType() + ") not usable in toString() " );
    		}
    	}
    	// last line
    	lb.append(indentationLevel, "sb.append(\"]\");" ); 
		lb.append(indentationLevel, "return sb.toString();" );
    }

    /**
     * Returns true if the given type is usable in a 'toString' method
     * @param attribute
     * @return
     */
    private boolean usableInToString( AttributeInContext attribute ) {
    	if ( attribute.isBinaryType() ) return false ;
    	if ( isArray(attribute) ) return false ;
    	if ( attribute.isLongText() ) return false ;
    	
    	String sType = attribute.getType();
    	if ( null == sType ) return false ;
    	String s = sType.trim() ;
    	if ( s.endsWith("Blob") || s.endsWith("Clob") ) return false ; 
    	return true ;
    }
    
}
