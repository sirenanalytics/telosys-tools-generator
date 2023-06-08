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
package com.sirenanalytics.telosys.tools.generator.context;

import org.telosys.tools.generator.context.AttributeInContext;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.generator.context.names.ContextName;
import org.telosys.tools.generic.model.SirenParams;
import org.telosys.tools.generic.model.enums.BooleanValue;
import org.telosys.tools.generic.model.enums.FetchType;

//-------------------------------------------------------------------------------------
@VelocityObject(
		contextName=ContextName.SIJPA,
		text = { 
				"Object providing a set of utility functions for JPA (Java Persistence API) code generation",
				""
		},
		since = "2.0.7"
 )
//-------------------------------------------------------------------------------------
public class SirenJpaInContext {

	private boolean   genTargetEntity = false ; // v 3.3.0
	private String    collectionType  = "List" ; // next ver after v 3.3.0
	
	private FetchType linkManyToOneFetchType  = FetchType.UNDEFINED; // v 3.3.0
	private FetchType linkOneToOneFetchType   = FetchType.UNDEFINED; // v 3.3.0
	private FetchType linkOneToManyFetchType  = FetchType.UNDEFINED; // v 3.3.0
	private FetchType linkManyToManyFetchType = FetchType.UNDEFINED; // v 3.3.0
	
	private BooleanValue joinColumnInsertable = BooleanValue.UNDEFINED; // v 3.3.0
	private BooleanValue joinColumnUpdatable  = BooleanValue.UNDEFINED; // v 3.3.0
	
	private boolean  genColumnDefinition = false ; // v 3.4.0
	
	//-------------------------------------------------------------------------------------
	// CONSTRUCTOR
	//-------------------------------------------------------------------------------------
	public SirenJpaInContext() {
		super();
	}

	//-------------------------------------------------------------------------------------------------------------
	// J.P.A. ANNOTATIONS FOR FIELDS
	//-------------------------------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the JPA annotations for the given field (with a left margin)"
			},
		example={ 
			"$sijpa.fieldAnnotations( 4, $field )" },
		parameters = { 
			"leftMargin : the left margin (number of blanks) ",
			"field : the field to be annotated "
			},
		since = "2.0.7"
	)
	public String fieldAnnotations(int leftMargin, AttributeInContext attribute )
    {
		StringBuffer ret = new StringBuffer();
		SirenParams sirenParams = attribute.getSirenParams();
		if (sirenParams != null) {
			//NotBlank
			if(sirenParams.getSirenParam(SirenParams.NoBlanks, SirenParams.Exists) != null
			    && sirenParams.getSirenParam(SirenParams.NoBlanks, SirenParams.Message) != null
					) {
				ret.append("    @NotBlank");
				//ret.append(SirenParams.NoBlanks);
				ret.append("(message=\"");
				ret.append(sirenParams.getSirenParam(SirenParams.NoBlanks, SirenParams.Message));
				ret.append("\")");
			}
			if(		sirenParams.getSirenParam(SirenParams.ArabicOrEnglishOnlyConstraint, SirenParams.Exists) != null
				//&&  sirenParams.getSirenParam(SirenParams.ArabicOrEnglishOnlyConstraint, SirenParams.Message) != null
					) {
				ret.append("    @");
				ret.append(SirenParams.ArabicOrEnglishOnlyConstraint);
			}			
		}
		return ret.toString(); 
    }
}
