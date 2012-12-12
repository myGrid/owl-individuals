package uk.org.mygrid.owlindividuals.api;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Set;

import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.DefaultPrefixManager;


public interface Individuals {

	public void addAnnotationProperty(OWLAnnotationSubject subject,
			String predicateCurie, OWLAnnotationValue value);

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, boolean value);

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, double value);

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, float value);

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, int value);

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, OWLIndividual object);

	public void addAnnotationProperty(OWLIndividual subject,
			String predicateCurie, String value, String dataTypeCurie);

	public void addAnnotationPropertyPlain(OWLIndividual subject,
			String predicateCurie, String value);

	public void addAnnotationPropertyPlain(OWLIndividual subject,
			String predicateCurie, String value, String lang);

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			boolean value);

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			double value);

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			float value);

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			int value);

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			OWLLiteral literal);

	public void addDataProperty(OWLIndividual s, String predicateCurie,
			String value, String dataTypeCurie);

	public void addDataPropertyPlain(OWLIndividual s, String predicateCurie,
			String plainLiteral);

	public void addDataPropertyPlain(OWLIndividual s, String predicateCurie,
			String plainLiteral, String lang);

	/**
	 * Add an object property to an individual
	 * 
	 * @param subject
	 *            The subject individual
	 * @param predicateCurie
	 *            A CURIE or &lt;URI&gt; identifying the property
	 * @param object
	 *            The object individual
	 * @throws UnknownObjectPropertyException
	 *             if {@link #isStrict()} and the object property is not defined
	 *             in any of the imported ontologies. (Note that this might be
	 *             because it is an annotation property, in which case you need
	 *             to use addAnnotationProperty).
	 */
	public void addObjectProperty(OWLIndividual subject, String predicateCurie,
			OWLIndividual object);

	public void addObjectProperty(OWLIndividual subject, String predicateCurie,
			String objectCurie);

	public void addObjectProperty(String subjectCurie, String predicateCurie,
			String objectCurie);

	public void copyImports(OWLOntology src, OWLOntology dest);

	public OWLNamedIndividual createIndividual(String individualCurie,
			String... typeCuries);

	public void fillInOntology();

	public void fillInOntology(OWLReasoner reasoner);

	public Set<Boolean> getDataPropertiesBooleans(OWLIndividual individual,
			String predicateCurie);

	public Set<Double> getDataPropertiesDoubles(OWLIndividual individual,
			String predicateCurie);

	public Set<Float> getDataPropertiesFloats(OWLIndividual individual,
			String predicateCurie);

	public Set<Integer> getDataPropertiesIntegers(OWLIndividual individual,
			String predicateCurie);

	public Set<OWLLiteral> getDataPropertiesLiterals(OWLIndividual individual,
			String predicateCurie);

	public Set<String> getDataPropertiesPlain(OWLIndividual individual,
			String predicateCurie);

	public OWLLiteral getDataPropertyLiteral(OWLIndividual individual,
			String predicateCurie);

	public String getDataProperty(OWLIndividual individual,
			String predicateCurie);

	public String getDataPropertyPlain(OWLIndividual individual,
			String predicateCurie);

	public boolean getDataPropertyBoolean(OWLIndividual individual,
			String predicateCurie);

	public int getDataPropertyInteger(OWLIndividual individual,
			String predicateCurie);

	public double getDataPropertyDouble(OWLIndividual individual,
			String predicateCurie);

	public float getDataPropertyFloat(OWLIndividual individual,
			String predicateCurie);

	public OWLNamedIndividual getIndividual(String individualCurie);

	public Set<OWLNamedIndividual> getIndividuals();

	public Set<OWLIndividual> getIndividualsOfType(String typeCurie);

	public OWLOntologyManager getManager();

	public Set<OWLIndividual> getObjectProperties(OWLIndividual individual,
			String predicateCurie);

	public OWLIndividual getObjectProperty(OWLIndividual individual,
			String predicateCurie);

	public OWLOntology getOntology();

	public OWLAnnotationProperty getOWLAnnotationProperty(String predicateCurie);

	public OWLClass getOWLClass(String typeCurie);

	public OWLDataProperty getOWLDataProperty(String predicateCurie);

	public OWLDatatype getOWLDataType(String dataTypeCurie);

	public OWLObjectProperty getOWLObjectProperty(String predicateCurie);

	public DefaultPrefixManager getPrefixManager();

	public void importOntology(String prefix, String namespace)
			throws OWLOntologyCreationException;

	public void importOntology(String prefix, String namespace,
			String loadFromIRI) throws OWLOntologyCreationException;

	public void importOntologyTo(IRI importIRI, OWLOntology importingOntology);

	public boolean isInstanceOf(OWLIndividual individual, String string);

	public boolean isStrict();

	public boolean isStrictClasses();

	public boolean isStrictDatatypes();

	public boolean isStrictIndividuals();

	public boolean isStrictProperties();

	public void loadOntology(File file) throws OWLOntologyCreationException;

	public void loadOntology(InputStream inStream)
			throws OWLOntologyCreationException;

	public void loadOntology(InputStream inStream, String ontologyIRI)
			throws OWLOntologyCreationException;

	public void loadOntology(OWLOntologyDocumentSource src)
			throws OWLOntologyCreationException;

	public void loadOntology(Reader reader) throws OWLOntologyCreationException;

	public void loadOntology(Reader reader, String ontologyIRI)
			throws OWLOntologyCreationException;

	public void loadOntology(String ontologyIRI)
			throws OWLOntologyCreationException;

	public void loadOntologyFromString(String ontologyStr)
			throws OWLOntologyCreationException;

	public void loadOntologyFromString(String ontologyStr, String ontologyIRI)
			throws OWLOntologyCreationException;

	public void saveOntology(OutputStream outputStream)
			throws OWLOntologyStorageException;

	public void setManager(OWLOntologyManager manager);

	public void setOntology(OWLOntology ontology);

	public void setPrefixManager(DefaultPrefixManager prefixManager);

	public void setStrict(boolean isStrict);

	public void setStrictClasses(boolean isStrictClasses);

	public void setStrictDatatypes(boolean isStrictDatatypes);

	public void setStrictIndividuals(boolean isStrictIndividuals);

	public void setStrictProperties(boolean isStrictProperties);

	public OWLAnnotationSubject toAnnotationSubject(OWLObject object);

	public OWLAnnotationValue toAnnotationValue(OWLObject object);

	public String toIRI(OWLIndividual individual);

	public IRI toIRI(String curie);

}