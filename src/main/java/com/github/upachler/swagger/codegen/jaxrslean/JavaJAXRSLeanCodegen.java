/*
Copyright 2016 the swagger-codegen project, Uwe Pachler

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Based on the JAX RS Spec module in swagger-codegen, modified by Uwe Pachler
*/
package com.github.upachler.swagger.codegen.jaxrslean;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.swagger.codegen.CliOption;
import io.swagger.codegen.CodegenConstants;
import io.swagger.codegen.CodegenModel;
import io.swagger.codegen.CodegenOperation;
import io.swagger.codegen.CodegenProperty;
import io.swagger.codegen.languages.AbstractJavaJAXRSServerCodegen;
import io.swagger.models.Operation;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;

public class JavaJAXRSLeanCodegen extends AbstractJavaJAXRSServerCodegen
{	
	static final String USE_JAXB_ANNOTATIONS = "useJaxbAnnotations";
	
	static final Map<String,String> BOXED_TO_PRIMITIVE_MAP;
	
	private boolean useJaxbAnnotations = true;
	
	static {
		Map<String,String> m = new HashMap<String,String>();
		m.put("Double", "double");
		m.put("Float", "float");
		m.put("Long", "long");
		m.put("Integer", "int");
		m.put("Short", "short");
		m.put("Byte", "byte");
		m.put("Boolean", "boolean");
		BOXED_TO_PRIMITIVE_MAP = Collections.unmodifiableMap(m);
	}
	
	public JavaJAXRSLeanCodegen()
	{
        super();
        invokerPackage = "io.swagger.api";
        artifactId = "swagger-jaxrs-lean";
        outputFolder = "";
		sourceFolder = outputFolder;
		
        modelTemplateFiles.put("model.mustache", ".java");
        apiTemplateFiles.put("api.mustache", ".java");
        apiPackage = "io.swagger.api";
        modelPackage = "io.swagger.model";

        apiTestTemplateFiles.clear(); // TODO: add api test template
        modelTestTemplateFiles.clear(); // TODO: add model test template

        // clear model and api doc template as this codegen
        // does not support auto-generated markdown doc at the moment
        //TODO: add doc templates
        modelDocTemplateFiles.remove("model_doc.mustache");
        apiDocTemplateFiles.remove("api_doc.mustache");

        additionalProperties.put("title", title);

		// use default mapping for dates (string)
        typeMapping.remove("date");
        typeMapping.remove("DateTime");	// remove odd DateTime mapping defined in superclass
		
		// for date-time, use XMLGregorianCalendar
        typeMapping.put("date-time", "XMLGregorianCalendar"); // Map DateTime fields to Java standart class 'XMLGregorianCalendar'

        importMapping.put("XMLGregorianCalendar", "javax.xml.datatype.XMLGregorianCalendar"); // Map DateTime fields to Java standard class 'XMLGregorianCalendar'

        super.embeddedTemplateDir = templateDir = getClass().getPackage().getName().replace('.', '/') + "/templates";

        for ( int i = 0; i < cliOptions.size(); i++ ) {
            if ( CodegenConstants.LIBRARY.equals(cliOptions.get(i).getOpt()) ) {
                cliOptions.remove(i);
                break;
            }
        }
                
        CliOption library = new CliOption(CodegenConstants.LIBRARY, "library template (sub-template) to use");
        library.setDefault(DEFAULT_LIBRARY);

        Map<String, String> supportedLibraries = new LinkedHashMap<String,String>();

        supportedLibraries.put(DEFAULT_LIBRARY, "JAXRS");
        library.setEnum(supportedLibraries);

		additionalProperties.put(USE_JAXB_ANNOTATIONS, true);
		
        cliOptions.add(library);
	}

	public void setUseJaxbAnnotations(boolean useJaxbAnnotations) {
		this.useJaxbAnnotations = useJaxbAnnotations;
	}

	public boolean isUseJaxbAnnotations() {
		return useJaxbAnnotations;
	}
	
	@Override
	public void processOpts()
	{
		super.processOpts();

        if (additionalProperties.containsKey(USE_JAXB_ANNOTATIONS)) {
            boolean useJaxbAnnotationsProp = Boolean.valueOf(additionalProperties.get(USE_JAXB_ANNOTATIONS).toString());
            this.setUseJaxbAnnotations(useJaxbAnnotationsProp);
        }
		
		supportingFiles.clear(); // Don't need extra files provided by AbstractJAX-RS & Java Codegen
	} 

	@Override
	public String getName()
	{
		return "jaxrs-lean";
	}
	
    @Override
    public void addOperationToGroup(String tag, String resourcePath, Operation operation, CodegenOperation co, Map<String, List<CodegenOperation>> operations) {
        String basePath = resourcePath;
        if (basePath.startsWith("/")) {
            basePath = basePath.substring(1);
        }
        int pos = basePath.indexOf("/");
        if (pos > 0) {
            basePath = basePath.substring(0, pos);
        }

        if (basePath == "") {
            basePath = "default";
        } else {
            if (co.path.startsWith("/" + basePath)) {
                co.path = co.path.substring(("/" + basePath).length());
            }
            co.subresourceOperation = !co.path.isEmpty();
        }
        List<CodegenOperation> opList = operations.get(basePath);
        if (opList == null) {
            opList = new ArrayList<CodegenOperation>();
            operations.put(basePath, opList);
        }
        opList.add(co);
        co.baseName = basePath;
    }
    
    @Override
    public void postProcessModelProperty(CodegenModel model, CodegenProperty property) {
        super.postProcessModelProperty(model, property);
        model.imports.remove("ApiModelProperty");
        model.imports.remove("ApiModel");
        model.imports.remove("JsonSerialize");
        model.imports.remove("ToStringSerializer");
        model.imports.remove("JsonValue");
        model.imports.remove("JsonProperty");
		
		// prevent writing 'default' values that are the same as the Java default
		// after class initialization, there's not need to have them im code
		if(property.defaultValue.equals("null") || property.defaultValue.equals("0") || property.defaultValue.equals("false")) {
			property.defaultValue = null;
		}
		
		String boxedType = property.datatypeWithEnum;
		if(Boolean.TRUE.equals(property.required)) {
			String primitiveType = BOXED_TO_PRIMITIVE_MAP.get(property.datatypeWithEnum);
			if(primitiveType != null) {
				property.datatype = primitiveType;
				property.datatypeWithEnum = primitiveType;
			}
		}
		property.vendorExtensions.put("boxedType", boxedType);
    }

	@Override
	public Map<String, Object> postProcessModels(Map<String, Object> objs) {
		objs = super.postProcessModels(objs);
		
		// remove all imports from io.swagger.* - Because these imports are 
		// added by superclasses rather late in the process, we need to hook
		// in the file postprocessing step. The import additions are hard wired
		// in code there, withtout obvious configurability to turn them off.
		List<Map<String,Object>> imports = (List<Map<String,Object>>) objs.get("imports");
		List<Map<String,Object>> removeList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> im : imports) {
			Object v = im.get("import");
			if(v.toString().startsWith("io.swagger.")) {
				removeList.add(im);
			}
		}
		imports.removeAll(removeList);
		
		return objs;
	}
    
	@Override
    public void preprocessSwagger(Swagger swagger) {
		//copy input swagger to output folder 
    	try {
			String swaggerJson = Json.pretty(swagger);
            FileUtils.writeStringToFile(new File(outputFolder + File.separator + "swagger.json"), swaggerJson);
		} catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
		}
		super.preprocessSwagger(swagger);

    }
    @Override
    public String getHelp()
    {
        return "Generates a Java JAXRS Server according to JAXRS 2.0 specification.";
    }
}
