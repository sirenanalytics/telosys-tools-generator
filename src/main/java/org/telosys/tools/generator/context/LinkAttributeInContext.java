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

import org.telosys.tools.generator.GeneratorException;
import org.telosys.tools.generator.context.doc.VelocityMethod;
import org.telosys.tools.generator.context.doc.VelocityObject;
import org.telosys.tools.generator.context.names.ContextName;
import org.telosys.tools.generic.model.Link;

/**
 * Foreign Key pair of attributes ( origin attribute with referenced attribute ) 
 * 
 * @author Laurent Guerin
 *
 */
//-------------------------------------------------------------------------------------
@VelocityObject(
//	contextName = ContextName.FK_ATTRIBUTE ,
	contextName = ContextName.LINK_ATTRIBUTE ,  // error fixed in v 4.0.1
	text = {
			"Foreign Key attribute",
			""
	},
	since = "3.4.0",
	example= {
			"",
			"#foreach( $fkAttribute in $fk.attributes )",
			"    $fkAttribute.xxx - $fkAttribute.xxx ",
			"#end"
	}
)
//-------------------------------------------------------------------------------------
public class LinkAttributeInContext {

	private final String linkName ;	
	private final String originEntityName ;	
	private final String referencedEntityName ;

	private final String originAttributeName ;	
	private final String referencedAttributeName ;
	
	private final ModelInContext modelInContext ;  // v 3.4.0

	//-------------------------------------------------------------------------------------
	/**
	 * Constructor
	 */
	public LinkAttributeInContext( ModelInContext modelInContext, EntityInContext entity, Link link,
			String originAttributeName, String referencedAttributeName) { 
		super();
		
		this.linkName = link.getFieldName();
		this.originEntityName = entity.getName();
//		this.referencedEntityName = link.getTargetEntityClassName();
		this.referencedEntityName = link.getReferencedEntityName();
		
		this.originAttributeName = originAttributeName;
		this.referencedAttributeName  = referencedAttributeName;

		this.modelInContext = modelInContext;
	}
	 
	public String getLinkName() {
		return this.linkName;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the origin attribute name"
			}
	)
	public String getOriginAttributeName() {
		return this.originAttributeName;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the origin attribute object"
			}
	)
	public AttributeInContext getOriginAttribute() throws GeneratorException {
		EntityInContext entity = this.modelInContext.getEntityByClassName(this.originEntityName);
		return entity.getAttributeByName(this.originAttributeName);
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the referenced attribute name "
			}
	)
	public String getReferencedAttributeName() {
		return this.referencedAttributeName;
	}

	//-------------------------------------------------------------------------------------
	@VelocityMethod(
		text={	
			"Returns the referenced attribute object"
			}
	)
	public AttributeInContext getReferencedAttribute() throws GeneratorException {
		EntityInContext entity = this.modelInContext.getEntityByClassName(this.referencedEntityName);
		return entity.getAttributeByName(this.referencedAttributeName);
	}

}
