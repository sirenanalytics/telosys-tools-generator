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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.telosys.tools.generator.context.AttributeInContext;
import org.telosys.tools.generator.context.EntityInContext;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.generator.context.names.ContextName;
import org.telosys.tools.generator.context.tools.AnnotationsBuilder;
import org.telosys.tools.generic.model.SirenParams;

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

	//-------------------------------------------------------------------------------------
	// CONSTRUCTOR
	//-------------------------------------------------------------------------------------
	public SirenJpaInContext() {
		super();
	}
	
	//-------------------------------------------------------------------------------------
	// ENTITY JPA ANNOTATIONS
	//-------------------------------------------------------------------------------------
	@VelocityMethod ( 
		text= { 
			"Returns a multiline String containing all the Java JPA annotations required for the current entity",
			"with the given left marging before each line"
		},
		parameters = {
			"leftMargin : the left margin (number of blanks)",
			"entity : the entity to be annotated"
		},
		example={	
			"$sijpa.fieldAnnotationIncludes(4, $entity)"
		},
		since = "2.0.7"
	)
	public String fieldAnnotationIncludes(int iLeftMargin, EntityInContext entity)
    {
		HashMap<String, String> includes = new HashMap<String, String>();
		
		List<AttributeInContext> list = entity.getAttributes();
		for (int i=0; i<list.size(); i++) {
			AttributeInContext attribute = list.get(i);

			SirenParams sirenParams = attribute.getSirenParams();
			if (sirenParams != null) {
				//NotBlank
				if(sirenParams.getSirenParam(SirenParams.NoBlanks, SirenParams.Exists) != null) {
					includes.put("import javax.validation.constraints.NotBlank;", "import javax.validation.constraints.NotBlank;");
				}
				if(sirenParams.getSirenParam(SirenParams.NoNulls, SirenParams.Exists) != null) {
					includes.put("import javax.validation.constraints.NotNull;", "import javax.validation.constraints.NotNull;");
				}
				if(sirenParams.getSirenParam(SirenParams.ArabicOrEnglishOnlyConstraint, SirenParams.Exists) != null) {
					includes.put("import com.sirenanalytics.booking.model.validation.ArabicOrEnglishOnlyConstraint;", "import com.sirenanalytics.booking.model.validation.ArabicOrEnglishOnlyConstraint;");
				}
				if(sirenParams.getSirenParam(SirenParams.EmailConstraint, SirenParams.Exists) != null) {
					includes.put("import com.sirenanalytics.booking.model.validation.EmailConstraint;", "import com.sirenanalytics.booking.model.validation.EmailConstraint;");
				}
				if(sirenParams.getSirenParam(SirenParams.LebaneseMobileConstraint, SirenParams.Exists) != null) {
					includes.put("import com.sirenanalytics.booking.model.validation.LebaneseMobileConstraint;", "import com.sirenanalytics.booking.model.validation.LebaneseMobileConstraint;");
				}
				if(    sirenParams.getSirenParam(SirenParams.MinMaxSize, SirenParams.Exists) != null
					|| sirenParams.getSirenParam(SirenParams.MinSize, SirenParams.Exists) != null
					|| sirenParams.getSirenParam(SirenParams.MaxSize, SirenParams.Exists) != null) {
					includes.put("import javax.validation.constraints.Size;", "import javax.validation.constraints.Size;");					
				}
			}
		}

		ArrayList<String> arr = new ArrayList<String>();
		
		Iterator<String> iter = includes.values().iterator();
		while (iter != null && iter.hasNext()) {
			arr.add(iter.next());
		}

		Collections.sort(arr);
		
		AnnotationsBuilder b = new AnnotationsBuilder(iLeftMargin);
		for(String imp : arr) {
			b.addLine(imp);
		}
		
		return b.getAnnotations();
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
			if(    sirenParams.getSirenParam(SirenParams.NoBlanks, SirenParams.Exists) != null
			    && sirenParams.getSirenParam(SirenParams.NoBlanks, SirenParams.Message) != null
					) {
				ret.append("    @NotBlank");
				//ret.append(SirenParams.NoBlanks);
				ret.append("(message=\"");
				ret.append(sirenParams.getSirenParam(SirenParams.NoBlanks, SirenParams.Message));
				ret.append("\")");
			}
			//NotNulls
			if(    sirenParams.getSirenParam(SirenParams.NoNulls, SirenParams.Exists) != null
			    && sirenParams.getSirenParam(SirenParams.NoNulls, SirenParams.Message) != null
					) {
				if(ret.length() > 0) ret.append("\n");
				ret.append("    @NotNull");
				//ret.append(SirenParams.NoBlanks);
				ret.append("(message=\"");
				ret.append(sirenParams.getSirenParam(SirenParams.NoNulls, SirenParams.Message));
				ret.append("\")");
			}			
			if(		sirenParams.getSirenParam(SirenParams.ArabicOrEnglishOnlyConstraint, SirenParams.Exists) != null) {
				if(ret.length() > 0) ret.append("\n");
				ret.append("    @");
				ret.append(SirenParams.ArabicOrEnglishOnlyConstraint);
			}
			if(		sirenParams.getSirenParam(SirenParams.LebaneseMobileConstraint, SirenParams.Exists) != null) {
					if(ret.length() > 0) ret.append("\n");
					ret.append("    @");
					ret.append(SirenParams.LebaneseMobileConstraint);
			}
			if(		sirenParams.getSirenParam(SirenParams.NumbersGreaterThanZeroOnlyConstraint, SirenParams.Exists) != null) {
				if(ret.length() > 0) ret.append("\n");
				ret.append("    @");
				ret.append(SirenParams.NumbersGreaterThanZeroOnlyConstraint);
			}			
			if(		sirenParams.getSirenParam(SirenParams.EmailConstraint, SirenParams.Exists) != null) {
				if(ret.length() > 0) ret.append("\n");
				ret.append("    @");
				ret.append(SirenParams.EmailConstraint);
			}			
			if(        sirenParams.getSirenParam(SirenParams.MinMaxSize, SirenParams.Exists) != null
				    && sirenParams.getSirenParam(SirenParams.MinMaxSize, SirenParams.Min) != null
				    && sirenParams.getSirenParam(SirenParams.MinMaxSize, SirenParams.Max) != null
					) {
				if(ret.length() > 0) ret.append("\n");				
				ret.append("    @Size");
				//ret.append(SirenParams.NoBlanks);
				ret.append("(min=");
				ret.append(sirenParams.getSirenParam(SirenParams.MinMaxSize, SirenParams.Min));
				ret.append(", max=");
				ret.append(sirenParams.getSirenParam(SirenParams.MinMaxSize, SirenParams.Max));					
				ret.append(")");
			}
			if(        sirenParams.getSirenParam(SirenParams.MinSize, SirenParams.Exists) != null
				    && sirenParams.getSirenParam(SirenParams.MinSize, SirenParams.Min) != null
					) {
				if(ret.length() > 0) ret.append("\n");				
				ret.append("    @Size");
				//ret.append(SirenParams.NoBlanks);
				ret.append("(min=");
				ret.append(sirenParams.getSirenParam(SirenParams.MinSize, SirenParams.Min));
				ret.append(")");
			}
			if(        sirenParams.getSirenParam(SirenParams.MaxSize, SirenParams.Exists) != null
				    && sirenParams.getSirenParam(SirenParams.MaxSize, SirenParams.Max) != null
					) {
				if(ret.length() > 0) ret.append("\n");				
				ret.append("    @Size");
				//ret.append(SirenParams.NoBlanks);
				ret.append("(max=");
				ret.append(sirenParams.getSirenParam(SirenParams.MaxSize, SirenParams.Max));
				ret.append(")");
			}
		}
		return ret.toString(); 
    }
}
