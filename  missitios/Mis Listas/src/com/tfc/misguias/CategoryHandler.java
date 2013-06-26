package com.tfc.misguias;

import java.util.Vector;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
public class CategoryHandler extends DefaultHandler{
 
    public CategoryHandler() {
	super();
	this.myParsedCategoryDataSet = new Vector<ParsedCategoryDataSet>();
    }	
 
    // Variables de control para saber cuando estamos en el interior de cada etiqueta
    @SuppressWarnings("unused")
    private boolean in_category = false;
    private boolean in_id = false;
    private boolean in_name = false;
    private boolean in_description = false;
    private boolean in_icon = false;
 
    // En esta variable guardamos el texto encontrado entre las etiquetas
    StringBuilder builder;
 
    // Aquí guardamos cada objeto categoria
    private ParsedCategoryDataSet DataSet;
 
    // Vector donde se guardaran todas las categorías encontradas
    private Vector<ParsedCategoryDataSet> myParsedCategoryDataSet; 
 
    public Vector<ParsedCategoryDataSet> getParsedCategoryDataSets() {
        return this.myParsedCategoryDataSet;
    }      
 
    public Vector<ParsedCategoryDataSet> getParsedData() {
         return this.myParsedCategoryDataSet;
    }
 
    @Override
    public void startDocument() throws SAXException {
         // Comenzamos a leer el fichero xml, creamos el vector donde se guardarán las categorías
         this.myParsedCategoryDataSet = new Vector<ParsedCategoryDataSet>();
    }
 
    @Override
    public void endDocument() throws SAXException {
         // Ha terminado de leer el fichero, en este paso no hacemos nada
    }     
 
    @Override
    public void startElement(String namespaceURI, String localName,
              String qName, Attributes atts) throws SAXException {
         if (localName.equals("category")) {
             // Ha encontrado la etiqueta principal de cada elemento "category"
             // Creamos un nuevo objeto categoría donde iremos guardando los datos
             this.in_category = true;
             DataSet = new ParsedCategoryDataSet();
         }else if (localName.equals("id")) {
             // Estamos dentro de la etiqueta "id", creamos el StringBuilder que utilizaremos
             // en el método characters para guardar el contenido
             this.in_id = true;
             builder = new StringBuilder();
         }else if (localName.equals("name")) {
             // Estamos dentro de la etiqueta "name", creamos el StringBuilder que utilizaremos
             // en el método characters para guardar el contenido
             this.in_name = true;
             builder = new StringBuilder();
         }else if (localName.equals("description")) {
             // Estamos dentro de la etiqueta "name", creamos el StringBuilder que utilizaremos
             // en el método characters para guardar el contenido
             this.in_description= true;
             builder = new StringBuilder();
         }else if (localName.equals("icon")) {
             // Estamos dentro de la etiqueta "name", creamos el StringBuilder que utilizaremos
             // en el método characters para guardar el contenido
             this.in_icon= true;
             builder = new StringBuilder();
         }
    } 
 
    @Override
    public void endElement(String namespaceURI, String localName, String qName)
              throws SAXException {
        if (localName.equals("category")) {
            // Hemos llegado al final de la etiqueta principal de cada elemento "category"
            // Añadimos al vector el elemento leído
            this.in_category = false;
            myParsedCategoryDataSet.add(DataSet);
        }else if (localName.equals("id")) {
            // Ha encontrado la etiqueta de cierre de "id"
            this.in_id = false;
        }else if (localName.equals("name")) {
            // Ha encontrado la etiqueta de cierre de "name"
            this.in_name = false;
        }
        else if (localName.equals("description")) {
            // Ha encontrado la etiqueta de cierre de "name"
            this.in_description = false;
        }
        else if (localName.equals("icon")) {
            // Ha encontrado la etiqueta de cierre de "name"
            this.in_category = false;
        }
    } 
 
    @Override
    public void characters(char ch[], int start, int length) {
 
       // Si estamos dentro de la etiqueta "id"
       if(this.in_id){
       	   if (builder!=null) {
       	        for (int i=start; i<start+length; i++) {
                    // Añadimos al StringBuilder (definido al encontrar el comienzo de la etiqueta "id")
                    // lo que haya entre las etiquetas de inicio y fin
       	            builder.append(ch[i]);
       	        }
       	   }        	 
           // Lo asignamos al "id" del objeto categoría (DataSet)
       	   DataSet.setId(builder.toString()); 
       }
 
       // Si estamos dentro de la etiqueta "id"
       if(this.in_name){
           if (builder!=null) {
    	       for (int i=start; i<start+length; i++) {
                    // Añadimos al StringBuilder (definido al encontrar el comienzo de la etiqueta "name")
                    // lo que haya entre las etiquetas de inicio y fin
    	            builder.append(ch[i]);
    	        }
     	   }            	 
           // Lo asignamos al "name" del objeto categoría (DataSet)
           DataSet.setName(builder.toString()); 
       }   
       // Si estamos dentro de la etiqueta "description"
       if(this.in_description){
           if (builder!=null) {
    	       for (int i=start; i<start+length; i++) {
                    // Añadimos al StringBuilder (definido al encontrar el comienzo de la etiqueta "name")
                    // lo que haya entre las etiquetas de inicio y fin
    	            builder.append(ch[i]);
    	        }
     	   }            	 
           // Lo asignamos al "name" del objeto categoría (DataSet)
           DataSet.setName(builder.toString()); 
       }  
       // Si estamos dentro de la etiqueta "id"
       if(this.in_icon){
           if (builder!=null) {
    	       for (int i=start; i<start+length; i++) {
                    // Añadimos al StringBuilder (definido al encontrar el comienzo de la etiqueta "name")
                    // lo que haya entre las etiquetas de inicio y fin
    	            builder.append(ch[i]);
    	        }
     	   }            	 
           // Lo asignamos al "name" del objeto categoría (DataSet)
           DataSet.setName(builder.toString()); 
       }  
 
   } 
}